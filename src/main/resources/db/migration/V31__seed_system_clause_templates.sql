-- Migra las cláusulas hasta ahora hardcodeadas en ContractTemplateFactory al catálogo
-- reutilizable clause_templates (promoter_id NULL = plantilla de sistema). El texto legal
-- se preserva verbatim; solo se le añade categoría y código estable.

-- ── PERFORMANCE (8) ──────────────────────────────────────────────────────────
INSERT INTO clause_templates (id, promoter_id, code, category, title, content_template, applicable_types, jurisdiction_scope, system_default, version, active)
VALUES
('00000000-0000-0000-0001-000000000001', NULL, 'SYS-PERFORMANCE-01', 'CUSTOM', 'Objeto del contrato',
 'Las partes contratantes acuerdan mediante el presente contrato la realización de una actuación en directo en las condiciones que a continuación se detallan.',
 '["PERFORMANCE"]', 'ES', true, 1, true),
('00000000-0000-0000-0001-000000000002', NULL, 'SYS-PERFORMANCE-02', 'CUSTOM', 'Fecha, lugar y duración',
 'La actuación tendrá lugar en el recinto y fecha indicados en el apartado de detalles de la actuación. La duración aproximada del show será la especificada, sin incluir el tiempo de prueba de sonido.',
 '["PERFORMANCE"]', 'ES', true, 1, true),
('00000000-0000-0000-0001-000000000003', NULL, 'SYS-PERFORMANCE-03', 'PAYMENT', 'Honorarios',
 'El importe pactado por la actuación es el reflejado en el apartado de condiciones económicas del presente contrato, expresado en la moneda indicada.',
 '["PERFORMANCE"]', 'ES', true, 1, true),
('00000000-0000-0000-0001-000000000004', NULL, 'SYS-PERFORMANCE-04', 'PAYMENT', 'Forma de pago',
 'Los pagos se realizarán de acuerdo con el calendario de pagos especificado en el apartado económico. Todos los importes son netos, libres de comisiones bancarias.',
 '["PERFORMANCE"]', 'ES', true, 1, true),
('00000000-0000-0000-0001-000000000005', NULL, 'SYS-PERFORMANCE-05', 'RIDER_ANNEX', 'Condiciones técnicas',
 'El promotor se compromete a facilitar los medios técnicos descritos en el rider técnico del artista, o equivalentes aprobados expresamente por el artista. El promotor asumirá los costes de producción técnica salvo pacto en contrario.',
 '["PERFORMANCE"]', 'ES', true, 1, true),
('00000000-0000-0000-0001-000000000006', NULL, 'SYS-PERFORMANCE-06', 'CANCELLATION', 'Cancelación por el artista',
 'En caso de cancelación por parte del artista con menos de 30 días de antelación a la fecha del evento, el artista deberá devolver las cantidades ya percibidas y podrá ser objeto de una penalización del 20% del total del caché pactado.',
 '["PERFORMANCE"]', 'ES', true, 1, true),
('00000000-0000-0000-0001-000000000007', NULL, 'SYS-PERFORMANCE-07', 'CANCELLATION', 'Cancelación por el promotor',
 'En caso de cancelación por parte del promotor con menos de 30 días de antelación, el promotor abonará al artista el 50% del total del caché pactado como indemnización, sin perjuicio de las cantidades ya entregadas.',
 '["PERFORMANCE"]', 'ES', true, 1, true),
('00000000-0000-0000-0001-000000000008', NULL, 'SYS-PERFORMANCE-08', 'CUSTOM', 'Jurisdicción y ley aplicable',
 'Para cualquier controversia derivada del presente contrato, las partes se someten expresamente a la jurisdicción y competencia de los Juzgados y Tribunales indicados en el apartado de jurisdicción, con renuncia a cualquier otro fuero.',
 '["PERFORMANCE"]', 'ES', true, 1, true);

-- ── REPRESENTATION (6) ───────────────────────────────────────────────────────
INSERT INTO clause_templates (id, promoter_id, code, category, title, content_template, applicable_types, jurisdiction_scope, system_default, version, active)
VALUES
('00000000-0000-0000-0002-000000000001', NULL, 'SYS-REPRESENTATION-01', 'CUSTOM', 'Objeto',
 'La agencia de representación asume la representación artística del artista para la gestión, negociación y contratación de actuaciones en el territorio pactado y durante el periodo de vigencia del presente contrato.',
 '["REPRESENTATION"]', 'ES', true, 1, true),
('00000000-0000-0000-0002-000000000002', NULL, 'SYS-REPRESENTATION-02', 'CUSTOM', 'Duración',
 'El presente contrato entrará en vigor en la fecha de su firma y tendrá una duración de doce (12) meses, renovándose tácitamente por periodos iguales salvo denuncia de cualquiera de las partes con 30 días de antelación.',
 '["REPRESENTATION"]', 'ES', true, 1, true),
('00000000-0000-0000-0002-000000000003', NULL, 'SYS-REPRESENTATION-03', 'PAYMENT', 'Comisión de representación',
 'La agencia percibirá una comisión del quince por ciento (15%) sobre el importe bruto de todos los contratos de actuación conseguidos durante la vigencia de este acuerdo, incluidas las renovaciones y los contratos firmados durante los seis meses posteriores a la extinción.',
 '["REPRESENTATION"]', 'ES', true, 1, true),
('00000000-0000-0000-0002-000000000004', NULL, 'SYS-REPRESENTATION-04', 'CUSTOM', 'Obligaciones de la agencia',
 'La agencia se compromete a: (a) gestionar activamente la agenda de actuaciones del artista; (b) negociar en las mejores condiciones posibles; (c) rendir cuentas detalladas mensualmente; (d) actuar siempre en interés del artista.',
 '["REPRESENTATION"]', 'ES', true, 1, true),
('00000000-0000-0000-0002-000000000005', NULL, 'SYS-REPRESENTATION-05', 'CUSTOM', 'Obligaciones del artista',
 'El artista se compromete a: (a) canalizar toda negociación a través de la agencia; (b) comunicar con antelación suficiente cualquier compromiso previo; (c) mantener vigente su disponibilidad técnica y rider; (d) no establecer acuerdos de representación paralelos en el territorio cubierto.',
 '["REPRESENTATION"]', 'ES', true, 1, true),
('00000000-0000-0000-0002-000000000006', NULL, 'SYS-REPRESENTATION-06', 'CANCELLATION', 'Resolución del contrato',
 'El contrato podrá resolverse anticipadamente por incumplimiento grave de cualquiera de las partes, previa comunicación escrita con 15 días de antelación. La resolución no afectará a los derechos de comisión devengados sobre contratos ya firmados.',
 '["REPRESENTATION"]', 'ES', true, 1, true);

-- ── TICKETING (5) ────────────────────────────────────────────────────────────
INSERT INTO clause_templates (id, promoter_id, code, category, title, content_template, applicable_types, jurisdiction_scope, system_default, version, active)
VALUES
('00000000-0000-0000-0003-000000000001', NULL, 'SYS-TICKETING-01', 'CUSTOM', 'Objeto',
 'El presente contrato regula las condiciones de venta de entradas para el evento detallado, así como la distribución de los ingresos generados entre las partes.',
 '["TICKETING"]', 'ES', true, 1, true),
('00000000-0000-0000-0003-000000000002', NULL, 'SYS-TICKETING-02', 'PAYMENT', 'Distribución de ingresos',
 'Los ingresos netos por venta de entradas, deducidos los gastos de plataforma y gestión acordados, se distribuirán entre las partes en los porcentajes especificados en el apartado económico.',
 '["TICKETING"]', 'ES', true, 1, true),
('00000000-0000-0000-0003-000000000003', NULL, 'SYS-TICKETING-03', 'CUSTOM', 'Responsabilidad sobre el aforo',
 'El promotor es responsable de garantizar que la venta de entradas no supere la capacidad legal del recinto. La plataforma de venta informará en tiempo real del estado del aforo disponible.',
 '["TICKETING"]', 'ES', true, 1, true),
('00000000-0000-0000-0003-000000000004', NULL, 'SYS-TICKETING-04', 'PAYMENT', 'Liquidación final',
 'La liquidación final de los ingresos por venta de entradas se realizará en un plazo máximo de diez (10) días hábiles tras la celebración del evento, previa presentación del justificante de realización del mismo.',
 '["TICKETING"]', 'ES', true, 1, true),
('00000000-0000-0000-0003-000000000005', NULL, 'SYS-TICKETING-05', 'CANCELLATION', 'Devoluciones y cancelaciones',
 'En caso de cancelación o aplazamiento del evento por causas imputables al promotor, se procederá al reembolso íntegro de las entradas vendidas. Los costes de gestión de devolución correrán a cargo del promotor.',
 '["TICKETING"]', 'ES', true, 1, true);

-- ── PRIVATE_FESTIVAL (7) ─────────────────────────────────────────────────────
INSERT INTO clause_templates (id, promoter_id, code, category, title, content_template, applicable_types, jurisdiction_scope, system_default, version, active)
VALUES
('00000000-0000-0000-0004-000000000001', NULL, 'SYS-PRIVATE_FESTIVAL-01', 'CUSTOM', 'Objeto del contrato',
 'El presente contrato regula la organización y producción de un evento privado de entretenimiento en las condiciones económicas, técnicas y artísticas que se detallan a continuación.',
 '["PRIVATE_FESTIVAL"]', 'ES', true, 1, true),
('00000000-0000-0000-0004-000000000002', NULL, 'SYS-PRIVATE_FESTIVAL-02', 'CUSTOM', 'Actuaciones y programación',
 'El promotor/artista se compromete a la realización de las actuaciones artísticas detalladas, con el equipo y la duración especificados. Cualquier modificación en el cartel deberá ser acordada por ambas partes con 15 días de antelación.',
 '["PRIVATE_FESTIVAL"]', 'ES', true, 1, true),
('00000000-0000-0000-0004-000000000003', NULL, 'SYS-PRIVATE_FESTIVAL-03', 'CUSTOM', 'Condiciones del recinto',
 'El contratante facilitará el espacio adecuado para la celebración del evento, incluyendo el acceso con suficiente antelación para el montaje, la infraestructura eléctrica requerida y los elementos de seguridad exigibles por la normativa vigente.',
 '["PRIVATE_FESTIVAL"]', 'ES', true, 1, true),
('00000000-0000-0000-0004-000000000004', NULL, 'SYS-PRIVATE_FESTIVAL-04', 'PAYMENT', 'Honorarios globales',
 'El importe total pactado por la producción artística del evento es el indicado en el apartado económico. Este importe incluye todos los costes artísticos, sin perjuicio de los gastos de producción técnica que se detallan en el rider.',
 '["PRIVATE_FESTIVAL"]', 'ES', true, 1, true),
('00000000-0000-0000-0004-000000000005', NULL, 'SYS-PRIVATE_FESTIVAL-05', 'RIDER_ANNEX', 'Logística y producción técnica',
 'El promotor/contratante asumirá los gastos de alojamiento, manutención y desplazamiento del equipo artístico según las condiciones del rider. Los gastos técnicos de producción (sonido, iluminación, escenario) correrán por cuenta de la parte indicada en el apartado económico.',
 '["PRIVATE_FESTIVAL"]', 'ES', true, 1, true),
('00000000-0000-0000-0004-000000000006', NULL, 'SYS-PRIVATE_FESTIVAL-06', 'FORCE_MAJEURE', 'Cancelación y fuerza mayor',
 'Ambas partes quedan exoneradas de responsabilidad en caso de fuerza mayor debidamente acreditada (causas meteorológicas extremas, decisiones gubernamentales, emergencias sanitarias, etc.). En tal caso se procederá a la devolución de los importes ya abonados, deducidos los gastos irrecuperables acreditados.',
 '["PRIVATE_FESTIVAL"]', 'ES', true, 1, true),
('00000000-0000-0000-0004-000000000007', NULL, 'SYS-PRIVATE_FESTIVAL-07', 'CONFIDENTIALITY', 'Confidencialidad',
 'Ambas partes se comprometen a mantener en estricta confidencialidad todos los términos económicos del presente contrato, así como cualquier información sensible sobre el evento privado y sus asistentes, durante la vigencia del contrato y durante los dos años siguientes a su extinción.',
 '["PRIVATE_FESTIVAL"]', 'ES', true, 1, true);
