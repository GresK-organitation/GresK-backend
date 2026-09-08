-- Un template de sistema (Markdown con placeholders {{variable.path}}) por cada
-- ContractType, para que el motor de plantillas sea usable de inmediato sin que la
-- promotora tenga que redactar nada antes de probarlo.

INSERT INTO contract_templates (id, promoter_id, type, name, body_markdown, variables, default_clause_template_ids, version, active)
VALUES (
    '00000000-0000-0000-0011-000000000001', NULL, 'PERFORMANCE', 'Actuación en directo (sistema)',
$md$# CONTRATO DE ACTUACIÓN EN DIRECTO

**Referencia:** {{contract.referenceNumber}}
**Lugar y fecha de firma:** {{contract.contractCity}}, {{contract.contractDate}}

## Partes contratantes

**Parte A (Promotor):** {{partyA.name}}, con NIF/CIF {{partyA.taxId}}, domicilio en {{partyA.address}} ({{partyA.country}}), representada por {{partyA.signatoryName}} ({{partyA.signatoryRole}}).

**Parte B (Artista):** {{partyB.name}}, con NIF/CIF {{partyB.taxId}}, domicilio en {{partyB.address}} ({{partyB.country}}), representada por {{partyB.signatoryName}} ({{partyB.signatoryRole}}).

## Objeto

Las partes acuerdan la realización de una actuación en directo del artista **{{artist.name}}** en el evento **{{event.title}}**, celebrado en {{event.venue}} el {{event.eventDate}}, con un aforo total de {{event.capacityTotal}} personas.

## Detalles de la actuación

- Recinto: {{performance.venue}}
- Fecha: {{performance.eventDate}}
- Horario: {{performance.showTime}}
- Duración aproximada: {{performance.durationMinutes}} minutos

## Condiciones económicas

El caché pactado asciende a **{{financial.feeAmount}} {{financial.feeCurrency}}**.

Retención fiscal aplicable: {{financial.withholdingTax.type}} ({{financial.withholdingTax.ratePercentage}}%). Importe retenido: {{financial.withholdingTax.withheldAmount}} {{financial.feeCurrency}}.

## Cláusulas

*(las cláusulas del catálogo aplicables a este tipo de contrato se anexan a continuación del presente cuerpo)*

## Firmas

En {{contract.contractCity}}, a {{contract.contractDate}}. Jurisdicción: {{contract.jurisdiction}}.

---

**PARTE A** — {{partyA.signatoryName}}, {{partyA.signatoryRole}}
Firma: _______________________

**PARTE B** — {{partyB.signatoryName}}, {{partyB.signatoryRole}}
Firma: _______________________
$md$,
    '[{"path":"partyA.name","label":"Nombre parte A","required":true},{"path":"partyB.name","label":"Nombre parte B","required":true},{"path":"performance.venue","label":"Recinto","required":true},{"path":"performance.eventDate","label":"Fecha del evento","required":true},{"path":"financial.feeAmount","label":"Caché","required":true},{"path":"financial.withholdingTax.type","label":"Tipo de retención","required":false}]'::jsonb,
    '["00000000-0000-0000-0001-000000000001","00000000-0000-0000-0001-000000000002","00000000-0000-0000-0001-000000000003","00000000-0000-0000-0001-000000000004","00000000-0000-0000-0001-000000000005","00000000-0000-0000-0001-000000000006","00000000-0000-0000-0001-000000000007","00000000-0000-0000-0001-000000000008"]'::jsonb,
    1, true
);

INSERT INTO contract_templates (id, promoter_id, type, name, body_markdown, variables, default_clause_template_ids, version, active)
VALUES (
    '00000000-0000-0000-0011-000000000002', NULL, 'REPRESENTATION', 'Representación artística (sistema)',
$md$# CONTRATO DE REPRESENTACIÓN ARTÍSTICA

**Referencia:** {{contract.referenceNumber}}
**Lugar y fecha de firma:** {{contract.contractCity}}, {{contract.contractDate}}

## Partes contratantes

**Parte A (Agencia):** {{partyA.name}}, con NIF/CIF {{partyA.taxId}}, domicilio en {{partyA.address}} ({{partyA.country}}), representada por {{partyA.signatoryName}} ({{partyA.signatoryRole}}).

**Parte B (Artista):** {{partyB.name}}, con NIF/CIF {{partyB.taxId}}, domicilio en {{partyB.address}} ({{partyB.country}}), representada por {{partyB.signatoryName}} ({{partyB.signatoryRole}}).

## Objeto

La agencia asume la representación artística de **{{artist.name}}** para la gestión, negociación y contratación de actuaciones durante la vigencia del presente contrato.

## Condiciones económicas

Comisión de representación pactada sobre el importe bruto de cada contrato de actuación conseguido: ver apartado económico ({{financial.feeAmount}} {{financial.feeCurrency}} de referencia).

## Cláusulas

*(las cláusulas del catálogo aplicables a este tipo de contrato se anexan a continuación del presente cuerpo)*

## Firmas

En {{contract.contractCity}}, a {{contract.contractDate}}. Jurisdicción: {{contract.jurisdiction}}.

---

**PARTE A** — {{partyA.signatoryName}}, {{partyA.signatoryRole}}
Firma: _______________________

**PARTE B** — {{partyB.signatoryName}}, {{partyB.signatoryRole}}
Firma: _______________________
$md$,
    '[{"path":"partyA.name","label":"Nombre agencia","required":true},{"path":"partyB.name","label":"Nombre artista","required":true},{"path":"financial.feeAmount","label":"Comisión de referencia","required":false}]'::jsonb,
    '["00000000-0000-0000-0002-000000000001","00000000-0000-0000-0002-000000000002","00000000-0000-0000-0002-000000000003","00000000-0000-0000-0002-000000000004","00000000-0000-0000-0002-000000000005","00000000-0000-0000-0002-000000000006"]'::jsonb,
    1, true
);

INSERT INTO contract_templates (id, promoter_id, type, name, body_markdown, variables, default_clause_template_ids, version, active)
VALUES (
    '00000000-0000-0000-0011-000000000003', NULL, 'TICKETING', 'Venta de entradas (sistema)',
$md$# CONTRATO DE VENTA DE ENTRADAS

**Referencia:** {{contract.referenceNumber}}
**Lugar y fecha de firma:** {{contract.contractCity}}, {{contract.contractDate}}

## Partes contratantes

**Parte A (Promotor):** {{partyA.name}}, con NIF/CIF {{partyA.taxId}}, domicilio en {{partyA.address}} ({{partyA.country}}).

**Parte B (Ticketera/Empresa):** {{partyB.name}}, con NIF/CIF {{partyB.taxId}}, domicilio en {{partyB.address}} ({{partyB.country}}).

## Objeto

El presente contrato regula la venta de entradas para el evento **{{event.title}}**, celebrado en {{event.venue}} el {{event.eventDate}}, con un aforo total de {{event.capacityTotal}} entradas a un precio de {{event.priceAmount}} {{event.priceCurrency}}.

## Condiciones económicas

Ingresos netos a distribuir entre las partes según los porcentajes especificados en el apartado económico. Importe de referencia: {{financial.feeAmount}} {{financial.feeCurrency}}.

## Cláusulas

*(las cláusulas del catálogo aplicables a este tipo de contrato se anexan a continuación del presente cuerpo)*

## Firmas

En {{contract.contractCity}}, a {{contract.contractDate}}. Jurisdicción: {{contract.jurisdiction}}.

---

**PARTE A** — {{partyA.signatoryName}}, {{partyA.signatoryRole}}
Firma: _______________________

**PARTE B** — {{partyB.signatoryName}}, {{partyB.signatoryRole}}
Firma: _______________________
$md$,
    '[{"path":"event.title","label":"Evento","required":true},{"path":"event.capacityTotal","label":"Aforo","required":true},{"path":"event.priceAmount","label":"Precio de entrada","required":true}]'::jsonb,
    '["00000000-0000-0000-0003-000000000001","00000000-0000-0000-0003-000000000002","00000000-0000-0000-0003-000000000003","00000000-0000-0000-0003-000000000004","00000000-0000-0000-0003-000000000005"]'::jsonb,
    1, true
);

INSERT INTO contract_templates (id, promoter_id, type, name, body_markdown, variables, default_clause_template_ids, version, active)
VALUES (
    '00000000-0000-0000-0011-000000000004', NULL, 'PRIVATE_FESTIVAL', 'Festival privado (sistema)',
$md$# CONTRATO DE EVENTO PRIVADO

**Referencia:** {{contract.referenceNumber}}
**Lugar y fecha de firma:** {{contract.contractCity}}, {{contract.contractDate}}

## Partes contratantes

**Parte A (Contratante):** {{partyA.name}}, con NIF/CIF {{partyA.taxId}}, domicilio en {{partyA.address}} ({{partyA.country}}).

**Parte B (Promotor/Artista):** {{partyB.name}}, con NIF/CIF {{partyB.taxId}}, domicilio en {{partyB.address}} ({{partyB.country}}).

## Objeto

El presente contrato regula la organización y producción de un evento privado con la actuación de **{{artist.name}}**, celebrado en {{performance.venue}} el {{performance.eventDate}}.

## Detalles de la actuación

- Recinto: {{performance.venue}}
- Fecha: {{performance.eventDate}}
- Horario: {{performance.showTime}}
- Duración aproximada: {{performance.durationMinutes}} minutos

## Condiciones económicas

Honorarios globales pactados: **{{financial.feeAmount}} {{financial.feeCurrency}}**.

## Cláusulas

*(las cláusulas del catálogo aplicables a este tipo de contrato se anexan a continuación del presente cuerpo)*

## Firmas

En {{contract.contractCity}}, a {{contract.contractDate}}. Jurisdicción: {{contract.jurisdiction}}.

---

**PARTE A** — {{partyA.signatoryName}}, {{partyA.signatoryRole}}
Firma: _______________________

**PARTE B** — {{partyB.signatoryName}}, {{partyB.signatoryRole}}
Firma: _______________________
$md$,
    '[{"path":"partyA.name","label":"Contratante","required":true},{"path":"performance.venue","label":"Recinto","required":true},{"path":"financial.feeAmount","label":"Honorarios globales","required":true}]'::jsonb,
    '["00000000-0000-0000-0004-000000000001","00000000-0000-0000-0004-000000000002","00000000-0000-0000-0004-000000000003","00000000-0000-0000-0004-000000000004","00000000-0000-0000-0004-000000000005","00000000-0000-0000-0004-000000000006","00000000-0000-0000-0004-000000000007"]'::jsonb,
    1, true
);
