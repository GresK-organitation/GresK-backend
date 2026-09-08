#!/usr/bin/env bash
#
# Smoke test de GresK backend — valida los flujos críticos reales antes de una demo.
#
# Todos los endpoints, roles y DTOs de este script fueron leídos directamente del
# código (controllers, DTOs, SecurityConfig, seed SQL), no asumidos.
#
# Omitido a propósito (no existe en el backend hoy):
#   - Pasaporte musical (sellos, wrapped, niveles de gamificación)
#   - Comunidad social (seguir usuarios, feed social, grupos de afinidad, matchmaking)
#   - CRM de superfans
#
# Excluido a propósito (requiere credenciales de terceros no configuradas o
# transiciones de estado que no se pueden probar de forma segura/idempotente):
#   - Gmail OAuth: connect/callback/disconnect/sync-now (GMAIL_CLIENT_ID/SECRET vacíos)
#   - Aprobar/editar/borrar drafts de email generados por IA (no hay ingestión real de Gmail)
#   - Contratos: sign/archive/cancel/clone/signed-pdf (transiciones de estado terminales)
#   - Riders: clone/from-template (redundantes con create para efectos de smoke test)
#
# Uso: bash smoke_test.sh
# Configurable: BASE_URL=http://localhost:8080 bash smoke_test.sh

set -uo pipefail

# ─────────────────────────────────────────────────────────────────────────────
# Config
# ─────────────────────────────────────────────────────────────────────────────

BASE_URL="${BASE_URL:-http://localhost:8080}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [[ -f "${SCRIPT_DIR}/.env" ]]; then
    set -a
    # shellcheck disable=SC1091
    source "${SCRIPT_DIR}/.env"
    set +a
fi

if [[ -z "${JWT_SECRET:-}" ]]; then
    echo "ERROR: JWT_SECRET no está definido." >&2
    echo "  Exporta la variable o crea un .env en la raíz del proyecto (ver .env.example)." >&2
    exit 1
fi

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m'

PASS=0
FAIL=0
TOTAL=0

TMP_BODY="$(mktemp)"
trap 'rm -f "$TMP_BODY"' EXIT

# ─────────────────────────────────────────────────────────────────────────────
# Helpers
# ─────────────────────────────────────────────────────────────────────────────

section() {
    echo ""
    echo -e "${BLUE}── $1 ──${NC}"
}

# check NAME EXPECTED_STATUSES ACTUAL_STATUS
# EXPECTED_STATUSES puede ser "200" o una lista "200|204"
check() {
    local name="$1" expected="$2" actual="$3"
    TOTAL=$((TOTAL + 1))
    if [[ "|${expected}|" == *"|${actual}|"* ]]; then
        PASS=$((PASS + 1))
        echo -e "  ${GREEN}✓${NC} ${name} (HTTP ${actual})"
    else
        FAIL=$((FAIL + 1))
        echo -e "  ${RED}✗${NC} ${name} — esperado ${expected}, recibido ${actual}"
        echo "     body: $(head -c 300 "$TMP_BODY")"
    fi
}

skip() {
    echo -e "  ${YELLOW}⚠${NC} $1 (omitido)"
}

# req METHOD PATH TOKEN [JSON_DATA]
req() {
    local method="$1" path="$2" token="$3" data="${4:-}"
    local args=(-s -o "$TMP_BODY" -w "%{http_code}" -X "$method" "${BASE_URL}${path}")
    [[ -n "$token" ]] && args+=(-H "Authorization: Bearer ${token}")
    [[ -n "$data" ]] && args+=(-H "Content-Type: application/json" -d "$data")
    curl "${args[@]}"
}

# req_multipart METHOD PATH TOKEN FORM_PART...
req_multipart() {
    local method="$1" path="$2" token="$3"; shift 3
    local args=(-s -o "$TMP_BODY" -w "%{http_code}" -X "$method" "${BASE_URL}${path}")
    [[ -n "$token" ]] && args+=(-H "Authorization: Bearer ${token}")
    for part in "$@"; do
        args+=(-F "$part")
    done
    curl "${args[@]}"
}

# json_field FIELD_NAME  — lee un campo string plano del último body recibido ($TMP_BODY)
json_field() {
    local field="$1"
    grep -o "\"${field}\"[[:space:]]*:[[:space:]]*\"[^\"]*\"" "$TMP_BODY" | head -1 \
        | sed -E 's/.*:[[:space:]]*"([^"]*)"/\1/'
}

b64url() {
    base64 | tr -d '\n' | tr '+/' '-_' | tr -d '='
}

# generate_jwt SUB EMAIL ROLE  — firma un JWT HS256 idéntico al de AccountJwtTokenGenerator.java
generate_jwt() {
    local sub="$1" email="$2" role="$3"
    local now exp header payload header_b64 payload_b64 signing_input signature
    now=$(date +%s)
    exp=$((now + 3600))
    header='{"alg":"HS256","typ":"JWT"}'
    payload=$(printf '{"sub":"%s","email":"%s","roles":["ROLE_%s"],"iat":%d,"exp":%d}' \
        "$sub" "$email" "$role" "$now" "$exp")
    header_b64=$(printf '%s' "$header" | b64url)
    payload_b64=$(printf '%s' "$payload" | b64url)
    signing_input="${header_b64}.${payload_b64}"
    signature=$(printf '%s' "$signing_input" | openssl dgst -sha256 -hmac "$JWT_SECRET" -binary | b64url)
    printf '%s.%s' "$signing_input" "$signature"
}

# date_offset DAYS  → LocalDate ISO (YYYY-MM-DD), portable GNU/BSD date
date_offset() {
    local n="$1"
    if date --version >/dev/null 2>&1; then
        date -u -d "${n} days" +"%Y-%m-%d"
    else
        # BSD date's -v requires an explicit sign, even for zero (-v0d fails)
        [[ "$n" != -* ]] && n="+${n}"
        date -u -v"${n}"d +"%Y-%m-%d"
    fi
}

# iso_offset DAYS  → Instant ISO (YYYY-MM-DDT00:00:00Z), portable GNU/BSD date
iso_offset() {
    local n="$1"
    if date --version >/dev/null 2>&1; then
        date -u -d "${n} days" +"%Y-%m-%dT00:00:00Z"
    else
        [[ "$n" != -* ]] && n="+${n}"
        date -u -v"${n}"d +"%Y-%m-%dT00:00:00Z"
    fi
}

# ─────────────────────────────────────────────────────────────────────────────
# Identidades demo (ver smoke_test.sh header y el plan aprobado para el porqué)
# ─────────────────────────────────────────────────────────────────────────────

NOW_TS=$(date +%s)
USER_EMAIL="smoketest.${NOW_TS}@gresk.demo"
USER_PASSWORD="SmokeTest123!"
USER_TOKEN=""
USER_ACCOUNT_ID=""

PROMOTER_ACCOUNT_ID="11111111-1111-1111-1111-111111111101"
PROMOTER_EMAIL="contacto@barcelonalive.demo"
PROMOTER_TOKEN=$(generate_jwt "$PROMOTER_ACCOUNT_ID" "$PROMOTER_EMAIL" "PROMOTER")

ADMIN_TOKEN=""

EVENT_ID="44444444-4444-4444-4444-444444444401"
ARTIST_ID="33333333-3333-3333-3333-333333333301"

TICKET_ID=""
REVIEW_ID=""
JOURNAL_ENTRY_ID=""
NEW_ARTIST_ID=""
RIDER_ID=""
RIDER_SHARE_TOKEN=""
CONTRACT_ID=""
CONTRACT_SHARE_TOKEN=""
AGENDA_ENTRY_ID=""
LIST_ID=""

TODAY=$(date_offset 0)
IN_7_DAYS=$(date_offset 7)
NINETY_DAYS_AGO=$(iso_offset -90)
TODAY_INSTANT=$(iso_offset 0)
YEAR=$(date +%Y)
MONTH=$((10#$(date +%m)))

# ─────────────────────────────────────────────────────────────────────────────
# AUTH
# ─────────────────────────────────────────────────────────────────────────────

test_auth() {
    section "AUTH"

    local reg_data status
    reg_data=$(printf '{"email":"%s","password":"%s","name":"Smoke Tester","city":"Barcelona","musicGenres":["ROCK"]}' \
        "$USER_EMAIL" "$USER_PASSWORD")
    status=$(req_multipart POST "/api/v1/auth/register/user" "" "data=${reg_data};type=application/json")
    check "Registro USER demo" "201" "$status"
    USER_ACCOUNT_ID=$(json_field accountId)

    status=$(req POST "/api/v1/auth/login" "" "$(printf '{"email":"%s","password":"%s"}' "$USER_EMAIL" "$USER_PASSWORD")")
    check "Login correcto (USER demo)" "200" "$status"
    USER_TOKEN=$(json_field token)

    status=$(req POST "/api/v1/auth/login" "" "$(printf '{"email":"%s","password":"wrong-password"}' "$USER_EMAIL")")
    check "Login incorrecto (password errónea)" "401" "$status"

    if [[ -n "${ADMIN_EMAIL:-}" && -n "${ADMIN_PASSWORD:-}" ]]; then
        status=$(req POST "/api/v1/auth/login" "" "$(printf '{"email":"%s","password":"%s"}' "$ADMIN_EMAIL" "$ADMIN_PASSWORD")")
        check "Login ADMIN real" "200" "$status"
        ADMIN_TOKEN=$(json_field token)
    else
        skip "Login ADMIN — ADMIN_EMAIL/ADMIN_PASSWORD no definidos en el entorno"
    fi
}

# ─────────────────────────────────────────────────────────────────────────────
# ADMIN
# ─────────────────────────────────────────────────────────────────────────────

test_admin() {
    section "ADMIN"

    if [[ -z "$ADMIN_TOKEN" ]]; then
        skip "Bloque ADMIN completo — no hay token ADMIN disponible"
        return
    fi

    local status
    status=$(req GET "/api/v1/admin/promoters" "$ADMIN_TOKEN")
    check "Listar promotoras" "200" "$status"

    status=$(req GET "/api/v1/admin/users" "$ADMIN_TOKEN")
    check "Listar usuarios" "200" "$status"

    # Cuenta PROMOTER fresca (queda PENDING) para probar la aprobación real —
    # aprobar una cuenta USER ya ACTIVE lanza IllegalStateException (Account.java:47-51)
    local promoter_reg_data promoter_email status_reg
    promoter_email="smoketest.promoter.${NOW_TS}@gresk.demo"
    promoter_reg_data=$(printf '{"email":"%s","password":"%s","name":"Smoke Promoter %s","street":"Carrer Fake 1","city":"Barcelona","country":"España","description":"promotora de prueba","musicalGenres":["ROCK"],"phone":"+34600000000","website":"https://example.com"}' \
        "$promoter_email" "$USER_PASSWORD" "$NOW_TS")
    status_reg=$(req_multipart POST "/api/v1/auth/register/promoter" "" "data=${promoter_reg_data};type=application/json")
    if [[ "$status_reg" == "201" ]]; then
        local pending_account_id
        pending_account_id=$(json_field accountId)
        status=$(req PATCH "/api/v1/admin/account/${pending_account_id}/approve" "$ADMIN_TOKEN")
        check "Aprobar promotora recién registrada (PENDING → ACTIVE)" "204" "$status"
    else
        skip "Aprobar promotora — el registro previo de la promotora de prueba falló (HTTP ${status_reg})"
    fi
}

# ─────────────────────────────────────────────────────────────────────────────
# TICKETS + REVIEWS
# ─────────────────────────────────────────────────────────────────────────────

test_tickets_reviews() {
    section "TICKETS + REVIEWS"

    local status data

    status=$(req POST "/api/v1/tickets" "$USER_TOKEN" "$(printf '{"eventId":"%s"}' "$EVENT_ID")")
    check "Compra de ticket" "201" "$status"
    TICKET_ID=$(json_field id)

    status=$(req POST "/api/v1/tickets" "$USER_TOKEN" "$(printf '{"eventId":"%s"}' "$EVENT_ID")")
    check "Compra de ticket duplicada" "409" "$status"

    status=$(req GET "/api/v1/users/me/tickets" "$USER_TOKEN")
    check "Mis tickets" "200" "$status"

    status=$(req GET "/api/v1/tickets/${TICKET_ID}/qr" "$USER_TOKEN")
    check "QR del ticket" "200" "$status"

    data=$(printf '{"ticketId":"%s","eventId":"%s","artistRating":5,"soundRating":4,"ambienceRating":5,"venueRating":4,"setlistRating":5,"comment":"Smoke test review"}' \
        "$TICKET_ID" "$EVENT_ID")
    status=$(req POST "/api/v1/reviews" "$USER_TOKEN" "$data")
    check "Crear reseña (con ticket)" "201" "$status"
    REVIEW_ID=$(json_field id)

    status=$(req GET "/api/v1/reviews/events/${EVENT_ID}" "$USER_TOKEN")
    check "Lista de reseñas del evento" "200" "$status"

    status=$(req GET "/api/v1/reviews/events/${EVENT_ID}/stats" "$USER_TOKEN")
    check "Stats de reseñas del evento" "200" "$status"

    status=$(req GET "/api/v1/reviews/users/me" "$USER_TOKEN")
    check "Mis reseñas" "200" "$status"

    if [[ -n "$REVIEW_ID" ]]; then
        status=$(req POST "/api/v1/reviews/${REVIEW_ID}/likes" "$USER_TOKEN")
        check "Dar like a reseña" "200" "$status"

        status=$(req POST "/api/v1/reviews/${REVIEW_ID}/likes" "$USER_TOKEN")
        check "Like duplicado a reseña" "409" "$status"

        status=$(req DELETE "/api/v1/reviews/${REVIEW_ID}/likes" "$USER_TOKEN")
        check "Quitar like a reseña" "200" "$status"
    fi
}

# ─────────────────────────────────────────────────────────────────────────────
# JOURNAL (entrada libre, sin ticket ni evento)
# ─────────────────────────────────────────────────────────────────────────────

test_journal() {
    section "JOURNAL (diario libre)"

    local status data
    data=$(printf '{"artistName":"Unknown Busker","date":"%s","datePrecision":"EXACT_DATE","visibility":"PUBLIC","genre":"ELECTRONIC","notes":"Concierto sorpresa en la calle"}' "$TODAY")
    status=$(req POST "/api/v1/journal/entries" "$USER_TOKEN" "$data")
    check "Crear entrada de diario libre (sin evento)" "201" "$status"
    JOURNAL_ENTRY_ID=$(json_field id)

    if [[ -n "$JOURNAL_ENTRY_ID" ]]; then
        status=$(req GET "/api/v1/journal/entries/${JOURNAL_ENTRY_ID}" "$USER_TOKEN")
        check "Detalle de entrada de diario" "200" "$status"
    fi

    status=$(req GET "/api/v1/journal/entries/mine" "$USER_TOKEN")
    check "Mis entradas de diario" "200" "$status"

    status=$(req GET "/api/v1/journal/rating-templates?genre=ELECTRONIC" "$USER_TOKEN")
    check "Plantillas de rating sugeridas" "200" "$status"
}

# ─────────────────────────────────────────────────────────────────────────────
# MUSIC DNA / DISCOVERY / DEMANDA
# ─────────────────────────────────────────────────────────────────────────────

test_musicdna_discovery() {
    section "MUSIC DNA + DISCOVERY + DEMANDA"

    local status
    status=$(req GET "/api/v1/users/me/music-dna" "$USER_TOKEN")
    check "ADN Musical del usuario" "200|204" "$status"

    status=$(req GET "/api/v1/discovery/artists?sizeTiers=MICRO&sizeTiers=SMALL&city=Barcelona" "$USER_TOKEN")
    check "Discovery con filtros (sizeTier + ciudad)" "200" "$status"

    status=$(req GET "/api/v1/discovery/artists/${ARTIST_ID}" "$USER_TOKEN")
    check "Detalle de artista (discovery)" "200" "$status"

    status=$(req GET "/api/v1/discovery/artists/${ARTIST_ID}/connections" "$USER_TOKEN")
    check "Conexiones de artista" "200" "$status"

    status=$(req GET "/api/v1/discovery/before-anyone" "$USER_TOKEN")
    check "Modo 'antes que nadie'" "200" "$status"

    status=$(req GET "/api/v1/discovery/surprise-me" "$USER_TOKEN")
    check "Modo 'sorpréndeme'" "200" "$status"

    status=$(req POST "/api/v1/discovery/artists/${ARTIST_ID}/demand" "$USER_TOKEN")
    check "Señal de demanda: activar ('quiero que venga')" "200" "$status"

    status=$(req POST "/api/v1/discovery/artists/${ARTIST_ID}/demand" "$USER_TOKEN")
    check "Señal de demanda: toggle (desactivar — es idempotente, no 409)" "200" "$status"
}

# ─────────────────────────────────────────────────────────────────────────────
# PROMOTER PROFILE
# ─────────────────────────────────────────────────────────────────────────────

test_promoter_profile() {
    section "PERFIL DE PROMOTORA"

    local status
    status=$(req GET "/api/v1/promoters/me" "$PROMOTER_TOKEN")
    check "Perfil propio de la promotora" "200" "$status"

    status=$(req GET "/api/v1/promoters/me/dashboard" "$PROMOTER_TOKEN")
    check "Dashboard de promotora" "200" "$status"

    status=$(req GET "/api/v1/promoters/me/events" "$PROMOTER_TOKEN")
    check "Eventos de la promotora" "200" "$status"
}

# ─────────────────────────────────────────────────────────────────────────────
# EVENTOS + FLASH DEAL + CALENDARIO/HEATMAP
# ─────────────────────────────────────────────────────────────────────────────

test_events_flashdeal() {
    section "EVENTOS + FLASH DEAL + CALENDARIO"

    local status
    status=$(req GET "/api/v1/events?city=Barcelona&genre=ELECTRONIC" "")
    check "Lista pública de eventos con filtros" "200" "$status"

    status=$(req GET "/api/v1/events/last-minute" "")
    check "Eventos de última hora" "200" "$status"

    status=$(req GET "/api/v1/events/${EVENT_ID}" "")
    check "Detalle público de evento" "200" "$status"

    status=$(req GET "/api/v1/events/calendar?from=${TODAY}&to=${IN_7_DAYS}" "$PROMOTER_TOKEN")
    check "Calendario de la promotora" "200" "$status"

    status=$(req GET "/api/v1/events/calendar/heatmap?year=${YEAR}&month=${MONTH}" "$PROMOTER_TOKEN")
    check "Heatmap de calendario (B2B)" "200" "$status"

    status=$(req PUT "/api/v1/events/${EVENT_ID}/flash-deal" "$PROMOTER_TOKEN" \
        '{"flashDealEnabled":true,"flashDealHoursThreshold":6,"flashDealDiscountPercent":20}')
    check "Activar flash deal" "200" "$status"

    status=$(req GET "/api/v1/events/${EVENT_ID}" "")
    check "Verificar flash deal activo (GET público)" "200" "$status"
    if ! grep -q '"flashDealEnabled":true' "$TMP_BODY"; then
        echo -e "  ${YELLOW}⚠${NC} el evento respondió 200 pero flashDealEnabled no es true"
    fi
}

# ─────────────────────────────────────────────────────────────────────────────
# ARTISTS
# ─────────────────────────────────────────────────────────────────────────────

test_artists() {
    section "ARTISTS"

    local status data
    data=$(printf '{"name":"Smoke Test Artist %s","origin":"Barcelona, España","genres":["ELECTRONIC"],"bio":"Artista de prueba generado por smoke test.","status":"CONFIRMED","contact":"smoketest.%s@gresk.demo"}' "$NOW_TS" "$NOW_TS")
    status=$(req_multipart POST "/api/v1/artists" "$PROMOTER_TOKEN" "data=${data};type=application/json")
    check "Crear artista" "201" "$status"
    NEW_ARTIST_ID=$(json_field id)

    status=$(req GET "/api/v1/artists/me" "$PROMOTER_TOKEN")
    check "Mis artistas" "200" "$status"

    if [[ -n "$NEW_ARTIST_ID" ]]; then
        status=$(req GET "/api/v1/artists/${NEW_ARTIST_ID}" "$PROMOTER_TOKEN")
        check "Detalle de artista propio" "200" "$status"
    fi

    status=$(req GET "/api/v1/artists/spotify/search?name=Coldplay" "$PROMOTER_TOKEN")
    check "Búsqueda de artista en Spotify" "200" "$status"
}

# ─────────────────────────────────────────────────────────────────────────────
# RIDERS
# ─────────────────────────────────────────────────────────────────────────────

test_riders() {
    section "RIDERS"

    if [[ -z "$NEW_ARTIST_ID" ]]; then
        skip "Bloque RIDERS completo — no se creó artista propio previamente"
        return
    fi

    local status
    status=$(req POST "/api/v1/riders" "$PROMOTER_TOKEN" "$(printf '{"artistId":"%s","name":"Rider Smoke Test"}' "$NEW_ARTIST_ID")")
    check "Crear rider" "201" "$status"
    RIDER_ID=$(json_field id)

    if [[ -z "$RIDER_ID" ]]; then
        skip "Resto de tests de RIDERS — no se obtuvo riderId"
        return
    fi

    status=$(req GET "/api/v1/riders/${RIDER_ID}" "$PROMOTER_TOKEN")
    check "Detalle de rider" "200" "$status"

    # publish() exige soundSystem, stageDimensions y backlineItems no vacío
    # (TechnicalRider.java:99-106) — se rellenan aquí para poder probar publish/link.
    status=$(req PUT "/api/v1/riders/${RIDER_ID}" "$PROMOTER_TOKEN" \
        '{"soundCheckDurationMinutes":30,"additionalNotes":"Smoke test update","soundSystem":{"consoleBrand":"Yamaha","consoleChannels":32,"monitorMixes":4,"paDescription":"L-Acoustics K2","processorNotes":"n/a"},"stageDimensions":{"widthMeters":10,"depthMeters":8,"minHeightMeters":4,"powerOutlets":8,"hasDrumRiser":false},"backlineItems":[{"category":"DRUMS","description":"Full kit","brand":"Pearl","model":"Export","required":true}]}')
    check "Actualizar rider" "200" "$status"

    status=$(req POST "/api/v1/riders/${RIDER_ID}/publish" "$PROMOTER_TOKEN")
    check "Publicar rider" "200|201" "$status"

    status=$(req GET "/api/v1/riders/${RIDER_ID}/pdf" "$PROMOTER_TOKEN")
    check "Descargar PDF de rider" "200" "$status"

    status=$(req POST "/api/v1/riders/${RIDER_ID}/share-link" "$PROMOTER_TOKEN")
    check "Generar share-link de rider" "200" "$status"
    RIDER_SHARE_TOKEN=$(json_field shareToken)

    if [[ -n "$RIDER_SHARE_TOKEN" ]]; then
        status=$(req GET "/api/v1/riders/${RIDER_ID}/public/${RIDER_SHARE_TOKEN}" "")
        check "Acceso público a rider vía share-link" "200" "$status"
    fi

    status=$(req POST "/api/v1/events/${EVENT_ID}/rider" "$PROMOTER_TOKEN" "$(printf '{"riderId":"%s"}' "$RIDER_ID")")
    check "Vincular rider a evento" "201" "$status"

    status=$(req GET "/api/v1/events/${EVENT_ID}/checklist" "$PROMOTER_TOKEN")
    check "Checklist de rider del evento" "200" "$status"

    status=$(req GET "/api/v1/promoter/rider-alerts" "$PROMOTER_TOKEN")
    check "Alertas de rider" "200" "$status"

    status=$(req GET "/api/v1/promoter/pending-riders" "$PROMOTER_TOKEN")
    check "Riders pendientes" "200" "$status"
}

# ─────────────────────────────────────────────────────────────────────────────
# CONTRACTS
# ─────────────────────────────────────────────────────────────────────────────

test_contracts() {
    section "CONTRACTS"

    local status data
    data='{"type":"PERFORMANCE","partyAName":"Barcelona Live Events","partyATaxId":"B12345678","partyAAddress":"Carrer de Mallorca 123, Barcelona","partyASignatoryName":"Smoke Tester","partyASignatoryRole":"Manager","partyAEmail":"contacto@barcelonalive.demo"}'
    status=$(req POST "/api/v1/contracts" "$PROMOTER_TOKEN" "$data")
    check "Crear contrato" "201" "$status"
    CONTRACT_ID=$(json_field id)

    if [[ -z "$CONTRACT_ID" ]]; then
        skip "Resto de tests de CONTRACTS — no se obtuvo contractId"
        return
    fi

    status=$(req GET "/api/v1/contracts/${CONTRACT_ID}" "$PROMOTER_TOKEN")
    check "Detalle de contrato" "200" "$status"

    status=$(req GET "/api/v1/contracts?status=DRAFT" "$PROMOTER_TOKEN")
    check "Lista de contratos (filtro status)" "200" "$status"

    status=$(req GET "/api/v1/contracts/stats" "$PROMOTER_TOKEN")
    check "Stats de contratos" "200" "$status"

    status=$(req POST "/api/v1/contracts/${CONTRACT_ID}/share-link" "$PROMOTER_TOKEN")
    check "Generar share-link de contrato" "200" "$status"
    CONTRACT_SHARE_TOKEN=$(json_field shareToken)

    if [[ -n "$CONTRACT_SHARE_TOKEN" ]]; then
        status=$(req GET "/api/v1/contracts/public/${CONTRACT_SHARE_TOKEN}" "")
        check "Acceso público a contrato vía share-link" "200" "$status"
    fi

    status=$(req POST "/api/v1/contracts/${CONTRACT_ID}/send" "$PROMOTER_TOKEN")
    check "Enviar contrato a firma" "200" "$status"
}

# ─────────────────────────────────────────────────────────────────────────────
# CONTRACTS v2 (plantillas, cláusulas, firma digital simulada, versiones, audit trail)
# ─────────────────────────────────────────────────────────────────────────────

test_contracts_v2() {
    section "CONTRACTS v2 (plantillas / firma digital / audit trail)"

    if [[ -z "$CONTRACT_ID" ]]; then
        skip "Bloque CONTRACTS v2 completo — no hay CONTRACT_ID de la sección anterior"
        return
    fi

    local status

    status=$(req GET "/api/v1/clause-templates?type=PERFORMANCE" "$PROMOTER_TOKEN")
    check "Catálogo de cláusulas de sistema (PERFORMANCE)" "200" "$status"

    local clause_data clause_status
    clause_data=$(printf '{"code":"SMOKE-%s","category":"CUSTOM","title":"Cláusula smoke test","contentTemplate":"Contenido de prueba.","applicableTypes":["PERFORMANCE"],"jurisdictionScope":"ES"}' "$NOW_TS")
    clause_status=$(req POST "/api/v1/clause-templates" "$PROMOTER_TOKEN" "$clause_data")
    check "Crear cláusula custom" "201" "$clause_status"

    status=$(req GET "/api/v1/contract-templates?type=PERFORMANCE" "$PROMOTER_TOKEN")
    check "Listar plantillas de contrato (sistema + propias)" "200" "$status"
    local TEMPLATE_ID
    TEMPLATE_ID=$(json_field id)

    if [[ -n "$TEMPLATE_ID" ]]; then
        status=$(req GET "/api/v1/contracts/${CONTRACT_ID}/pdf/template/${TEMPLATE_ID}" "$PROMOTER_TOKEN")
        check "Renderizar contrato desde plantilla Markdown" "200" "$status"
    else
        skip "Renderizar contrato desde plantilla — no se obtuvo templateId"
    fi

    local envelope_status
    envelope_status=$(req POST "/api/v1/contracts/${CONTRACT_ID}/signature/envelopes" "$PROMOTER_TOKEN" '{"provider":"MANUAL"}')
    check "Iniciar firma digital (proveedor simulado)" "201" "$envelope_status"
    local ENVELOPE_ID
    ENVELOPE_ID=$(json_field id)

    if [[ -n "$ENVELOPE_ID" ]]; then
        status=$(req GET "/api/v1/contracts/${CONTRACT_ID}/signature/envelopes/${ENVELOPE_ID}" "$PROMOTER_TOKEN")
        check "Consultar envelope de firma" "200" "$status"
    else
        skip "Consultar envelope de firma — no se obtuvo envelopeId"
    fi

    status=$(req GET "/api/v1/contracts/${CONTRACT_ID}/versions" "$PROMOTER_TOKEN")
    check "Historial de versiones del contrato" "200" "$status"

    status=$(req GET "/api/v1/contracts/${CONTRACT_ID}/audit-trail" "$PROMOTER_TOKEN")
    check "Audit trail del contrato" "200" "$status"

    status=$(req POST "/api/v1/webhooks/signature/stub?token=invalid-token" "" '{"providerEnvelopeId":"x","eventType":"DELIVERED"}')
    check "Seguridad: webhook de firma con token inválido" "401" "$status"
}

# ─────────────────────────────────────────────────────────────────────────────
# AGENDA
# ─────────────────────────────────────────────────────────────────────────────

test_agenda() {
    section "AGENDA"

    local status
    status=$(req POST "/api/v1/agenda/entries" "$PROMOTER_TOKEN" '{"type":"TASK","title":"Smoke test task"}')
    check "Crear entrada de agenda" "201" "$status"
    AGENDA_ENTRY_ID=$(json_field id)

    if [[ -n "$AGENDA_ENTRY_ID" ]]; then
        status=$(req GET "/api/v1/agenda/entries/${AGENDA_ENTRY_ID}" "$PROMOTER_TOKEN")
        check "Detalle de entrada de agenda" "200" "$status"

        status=$(req PATCH "/api/v1/agenda/entries/${AGENDA_ENTRY_ID}/complete" "$PROMOTER_TOKEN")
        check "Completar entrada de agenda" "200" "$status"
    fi

    status=$(req GET "/api/v1/agenda/view?from=${TODAY}&to=${IN_7_DAYS}" "$PROMOTER_TOKEN")
    check "Vista de agenda en rango" "200" "$status"

    status=$(req GET "/api/v1/agenda/view?from=${TODAY}&to=${IN_7_DAYS}" "$USER_TOKEN")
    check "Seguridad: vista de agenda con token USER" "403" "$status"
}

# ─────────────────────────────────────────────────────────────────────────────
# CURATION (listas curadas)
# ─────────────────────────────────────────────────────────────────────────────

test_curation() {
    section "CURATION (listas curadas)"

    local status
    status=$(req POST "/api/v1/lists" "$USER_TOKEN" '{"title":"Mi lista smoke test","visibility":"PUBLIC"}')
    check "Crear lista curada" "201" "$status"
    LIST_ID=$(json_field id)

    if [[ -z "$LIST_ID" ]]; then
        skip "Resto de tests de CURATION — no se obtuvo listId"
        return
    fi

    if [[ -n "$JOURNAL_ENTRY_ID" ]]; then
        status=$(req POST "/api/v1/lists/${LIST_ID}/items" "$USER_TOKEN" \
            "$(printf '{"entryType":"JOURNAL_ENTRY","entryId":"%s"}' "$JOURNAL_ENTRY_ID")")
        check "Añadir entrada de diario a la lista" "201" "$status"
    fi

    status=$(req GET "/api/v1/lists/${LIST_ID}" "$USER_TOKEN")
    check "Detalle de lista" "200" "$status"

    status=$(req GET "/api/v1/lists/mine" "$USER_TOKEN")
    check "Mis listas" "200" "$status"

    status=$(req GET "/api/v1/lists/discover" "$USER_TOKEN")
    check "Descubrir listas públicas" "200" "$status"

    status=$(req POST "/api/v1/lists/${LIST_ID}/follow" "$USER_TOKEN")
    check "Seguir lista propia" "200|201" "$status"
}

# ─────────────────────────────────────────────────────────────────────────────
# EMAIL INTELLIGENCE (solo lo que no depende de una conexión Gmail real)
# ─────────────────────────────────────────────────────────────────────────────

test_email_intelligence() {
    section "EMAIL INTELLIGENCE"

    local status
    status=$(req GET "/api/v1/promoters/me/emails" "$PROMOTER_TOKEN")
    check "Bandeja de emails (vacía, sin Gmail conectado)" "200" "$status"

    status=$(req GET "/api/v1/promoters/me/events/${EVENT_ID}/emails" "$PROMOTER_TOKEN")
    check "Emails del evento" "200" "$status"

    status=$(req GET "/api/v1/promoters/me/events/${EVENT_ID}/emails/summary" "$PROMOTER_TOKEN")
    check "Resumen de emails del evento" "200" "$status"

    status=$(req GET "/api/v1/promoters/me/drafts" "$PROMOTER_TOKEN")
    check "Drafts pendientes (vacío)" "200" "$status"

    status=$(req GET "/api/v1/promoters/me/events/${EVENT_ID}/rider" "$PROMOTER_TOKEN")
    check "Rider extraído por IA del evento" "200|204" "$status"

    status=$(req POST "/api/v1/email/gmail/webhook?token=invalid-token" "" '{}')
    check "Seguridad: webhook Gmail con token inválido" "401" "$status"

    skip "Gmail OAuth connect/callback/disconnect/sync-now — GMAIL_CLIENT_ID/SECRET no configurados"
    skip "Aprobar/editar/borrar drafts — no hay ingestión real de Gmail para generarlos"
}

# ─────────────────────────────────────────────────────────────────────────────
# TENDENCIAS
# ─────────────────────────────────────────────────────────────────────────────

test_tendencias() {
    section "TENDENCIAS"

    local status
    status=$(req GET "/api/v1/tendencias/chronicles" "")
    check "Crónicas publicadas" "200" "$status"

    status=$(req GET "/api/v1/tendencias/artists/most-reviewed?from=${NINETY_DAYS_AGO}&to=${TODAY_INSTANT}&limit=10" "")
    check "Artistas más reseñados" "200" "$status"

    status=$(req GET "/api/v1/tendencias/artists/top-rated?minReviews=1&limit=10" "")
    check "Artistas mejor puntuados" "200" "$status"

    status=$(req GET "/api/v1/tendencias/venues/most-visited?from=${NINETY_DAYS_AGO}&to=${TODAY_INSTANT}&limit=10" "")
    check "Salas más visitadas" "200" "$status"

    status=$(req GET "/api/v1/tendencias/genres/trending?currentFrom=${NINETY_DAYS_AGO}&currentTo=${TODAY_INSTANT}&previousFrom=${NINETY_DAYS_AGO}&previousTo=${NINETY_DAYS_AGO}&limit=10" "")
    check "Géneros en tendencia" "200" "$status"

    status=$(req GET "/api/v1/tendencias/events/most-discussed?from=${NINETY_DAYS_AGO}&to=${TODAY_INSTANT}&limit=10" "")
    check "Eventos más comentados" "200" "$status"

    status=$(req GET "/api/v1/tendencias/events/highest-sell-through?from=${NINETY_DAYS_AGO}&to=${TODAY_INSTANT}&limit=10" "")
    check "Eventos con mayor venta" "200" "$status"

    if [[ -n "$ADMIN_TOKEN" ]]; then
        status=$(req POST "/api/v1/tendencias/admin/feed-sources" "$ADMIN_TOKEN" \
            "$(printf '{"name":"Smoke Test Feed %s","feedUrl":"https://example.com/feed-%s.xml"}' "$NOW_TS" "$NOW_TS")")
        check "Registrar fuente de feed (ADMIN)" "201" "$status"

        status=$(req GET "/api/v1/tendencias/admin/feed-sources" "$ADMIN_TOKEN")
        check "Listar fuentes de feed (ADMIN)" "200" "$status"
    else
        skip "Fuentes de feed (ADMIN) — no hay token ADMIN disponible"
    fi
}

# ─────────────────────────────────────────────────────────────────────────────
# SEGURIDAD (matriz cruzada de roles)
# ─────────────────────────────────────────────────────────────────────────────

test_security() {
    section "SEGURIDAD"

    local status
    status=$(req GET "/api/v1/events/calendar/heatmap?year=${YEAR}&month=${MONTH}" "$USER_TOKEN")
    check "Heatmap de calendario con token USER" "403" "$status"

    if [[ -n "$ADMIN_TOKEN" ]]; then
        status=$(req GET "/api/v1/admin/promoters" "$PROMOTER_TOKEN")
        check "Endpoint ADMIN con token PROMOTER" "403" "$status"
    else
        skip "Endpoint ADMIN con token PROMOTER — no hay token ADMIN para confirmar que el bloque existe"
    fi

    # Body válido y completo a propósito: si faltan campos, la validación (400)
    # se dispara antes de que Spring Security evalúe el @PreAuthorize de rol,
    # y el test dejaría de medir lo que dice medir.
    local valid_event_data
    valid_event_data=$(printf '{"title":"Debug Event","genre":"ELECTRONIC","price":10.0,"currency":"EUR","totalCapacity":100,"eventDate":"%s","revealAt":"%s","street":"Test St 1","city":"Barcelona","country":"España","venue":"Test Venue","latitude":41.38,"longitude":2.17,"artistId":"%s"}' \
        "$(iso_offset 30)" "$(iso_offset -1)" "$ARTIST_ID")
    status=$(req_multipart POST "/api/v1/events" "$USER_TOKEN" "data=${valid_event_data};type=application/json")
    check "Crear evento con token USER" "403" "$status"

    status=$(req GET "/api/v1/reviews/users/me" "")
    check "Endpoint autenticado sin token" "401|403" "$status"
}

# ─────────────────────────────────────────────────────────────────────────────
# MAIN
# ─────────────────────────────────────────────────────────────────────────────

echo "GresK backend — smoke test"
echo "BASE_URL: ${BASE_URL}"

test_auth
test_admin
test_tickets_reviews
test_journal
test_musicdna_discovery
test_promoter_profile
test_events_flashdeal
test_artists
test_riders
test_contracts
test_contracts_v2
test_agenda
test_curation
test_email_intelligence
test_tendencias
test_security

echo ""
echo "─────────────────────────────────────────"
if [[ "$FAIL" -eq 0 ]]; then
    echo -e "${GREEN}Resultado: ${PASS}/${TOTAL} tests pasados${NC}"
else
    echo -e "${RED}Resultado: ${PASS}/${TOTAL} tests pasados (${FAIL} fallos)${NC}"
fi

[[ "$FAIL" -eq 0 ]]
