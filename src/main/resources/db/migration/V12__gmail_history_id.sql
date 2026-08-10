-- ─────────────────────────────────────────────────────────────────────────
-- V12: sincronización incremental de Gmail
--
-- last_history_id guarda el historyId de Gmail de la última sincronización
-- correcta. Las notificaciones Pub/Sub solo indican "hay cambios": la
-- descarga real usa history.list(startHistoryId = last_history_id).
-- ─────────────────────────────────────────────────────────────────────────

ALTER TABLE promoter_gmail_tokens
    ADD COLUMN last_history_id BIGINT;
