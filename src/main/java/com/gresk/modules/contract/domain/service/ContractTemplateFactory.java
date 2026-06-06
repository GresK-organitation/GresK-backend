package com.gresk.modules.contract.domain.service;

import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.domain.model.valueobject.ContractClause;
import com.gresk.modules.contract.domain.model.valueobject.ContractParty;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContractTemplateFactory {

    public Contract createFromTemplate(ContractType type, PromoterId promoterId,
                                        ContractParty partyA, String referenceNumber) {
        Contract contract = Contract.create(type, promoterId, partyA, referenceNumber);
        return contract.withClauses(buildClauses(type));
    }

    private List<ContractClause> buildClauses(ContractType type) {
        return switch (type) {
            case PERFORMANCE      -> performanceClauses();
            case REPRESENTATION   -> representationClauses();
            case TICKETING        -> ticketingClauses();
            case PRIVATE_FESTIVAL -> privateFestivalClauses();
        };
    }

    // ── PERFORMANCE ───────────────────────────────────────────────────────────

    private List<ContractClause> performanceClauses() {
        return List.of(
            new ContractClause(1, "Objeto del contrato",
                "Las partes contratantes acuerdan mediante el presente contrato la realización de una actuación en directo en las condiciones que a continuación se detallan."),
            new ContractClause(2, "Fecha, lugar y duración",
                "La actuación tendrá lugar en el recinto y fecha indicados en el apartado de detalles de la actuación. La duración aproximada del show será la especificada, sin incluir el tiempo de prueba de sonido."),
            new ContractClause(3, "Honorarios",
                "El importe pactado por la actuación es el reflejado en el apartado de condiciones económicas del presente contrato, expresado en la moneda indicada."),
            new ContractClause(4, "Forma de pago",
                "Los pagos se realizarán de acuerdo con el calendario de pagos especificado en el apartado económico. Todos los importes son netos, libres de comisiones bancarias."),
            new ContractClause(5, "Condiciones técnicas",
                "El promotor se compromete a facilitar los medios técnicos descritos en el rider técnico del artista, o equivalentes aprobados expresamente por el artista. El promotor asumirá los costes de producción técnica salvo pacto en contrario."),
            new ContractClause(6, "Cancelación por el artista",
                "En caso de cancelación por parte del artista con menos de 30 días de antelación a la fecha del evento, el artista deberá devolver las cantidades ya percibidas y podrá ser objeto de una penalización del 20% del total del caché pactado."),
            new ContractClause(7, "Cancelación por el promotor",
                "En caso de cancelación por parte del promotor con menos de 30 días de antelación, el promotor abonará al artista el 50% del total del caché pactado como indemnización, sin perjuicio de las cantidades ya entregadas."),
            new ContractClause(8, "Jurisdicción y ley aplicable",
                "Para cualquier controversia derivada del presente contrato, las partes se someten expresamente a la jurisdicción y competencia de los Juzgados y Tribunales indicados en el apartado de jurisdicción, con renuncia a cualquier otro fuero.")
        );
    }

    // ── REPRESENTATION ────────────────────────────────────────────────────────

    private List<ContractClause> representationClauses() {
        return List.of(
            new ContractClause(1, "Objeto",
                "La agencia de representación asume la representación artística del artista para la gestión, negociación y contratación de actuaciones en el territorio pactado y durante el periodo de vigencia del presente contrato."),
            new ContractClause(2, "Duración",
                "El presente contrato entrará en vigor en la fecha de su firma y tendrá una duración de doce (12) meses, renovándose tácitamente por periodos iguales salvo denuncia de cualquiera de las partes con 30 días de antelación."),
            new ContractClause(3, "Comisión de representación",
                "La agencia percibirá una comisión del quince por ciento (15%) sobre el importe bruto de todos los contratos de actuación conseguidos durante la vigencia de este acuerdo, incluidas las renovaciones y los contratos firmados durante los seis meses posteriores a la extinción."),
            new ContractClause(4, "Obligaciones de la agencia",
                "La agencia se compromete a: (a) gestionar activamente la agenda de actuaciones del artista; (b) negociar en las mejores condiciones posibles; (c) rendir cuentas detalladas mensualmente; (d) actuar siempre en interés del artista."),
            new ContractClause(5, "Obligaciones del artista",
                "El artista se compromete a: (a) canalizar toda negociación a través de la agencia; (b) comunicar con antelación suficiente cualquier compromiso previo; (c) mantener vigente su disponibilidad técnica y rider; (d) no establecer acuerdos de representación paralelos en el territorio cubierto."),
            new ContractClause(6, "Resolución del contrato",
                "El contrato podrá resolverse anticipadamente por incumplimiento grave de cualquiera de las partes, previa comunicación escrita con 15 días de antelación. La resolución no afectará a los derechos de comisión devengados sobre contratos ya firmados.")
        );
    }

    // ── TICKETING ─────────────────────────────────────────────────────────────

    private List<ContractClause> ticketingClauses() {
        return List.of(
            new ContractClause(1, "Objeto",
                "El presente contrato regula las condiciones de venta de entradas para el evento detallado, así como la distribución de los ingresos generados entre las partes."),
            new ContractClause(2, "Distribución de ingresos",
                "Los ingresos netos por venta de entradas, deducidos los gastos de plataforma y gestión acordados, se distribuirán entre las partes en los porcentajes especificados en el apartado económico."),
            new ContractClause(3, "Responsabilidad sobre el aforo",
                "El promotor es responsable de garantizar que la venta de entradas no supere la capacidad legal del recinto. La plataforma de venta informará en tiempo real del estado del aforo disponible."),
            new ContractClause(4, "Liquidación final",
                "La liquidación final de los ingresos por venta de entradas se realizará en un plazo máximo de diez (10) días hábiles tras la celebración del evento, previa presentación del justificante de realización del mismo."),
            new ContractClause(5, "Devoluciones y cancelaciones",
                "En caso de cancelación o aplazamiento del evento por causas imputables al promotor, se procederá al reembolso íntegro de las entradas vendidas. Los costes de gestión de devolución correrán a cargo del promotor.")
        );
    }

    // ── PRIVATE_FESTIVAL ──────────────────────────────────────────────────────

    private List<ContractClause> privateFestivalClauses() {
        return List.of(
            new ContractClause(1, "Objeto del contrato",
                "El presente contrato regula la organización y producción de un evento privado de entretenimiento en las condiciones económicas, técnicas y artísticas que se detallan a continuación."),
            new ContractClause(2, "Actuaciones y programación",
                "El promotor/artista se compromete a la realización de las actuaciones artísticas detalladas, con el equipo y la duración especificados. Cualquier modificación en el cartel deberá ser acordada por ambas partes con 15 días de antelación."),
            new ContractClause(3, "Condiciones del recinto",
                "El contratante facilitará el espacio adecuado para la celebración del evento, incluyendo el acceso con suficiente antelación para el montaje, la infraestructura eléctrica requerida y los elementos de seguridad exigibles por la normativa vigente."),
            new ContractClause(4, "Honorarios globales",
                "El importe total pactado por la producción artística del evento es el indicado en el apartado económico. Este importe incluye todos los costes artísticos, sin perjuicio de los gastos de producción técnica que se detallan en el rider."),
            new ContractClause(5, "Logística y producción técnica",
                "El promotor/contratante asumirá los gastos de alojamiento, manutención y desplazamiento del equipo artístico según las condiciones del rider. Los gastos técnicos de producción (sonido, iluminación, escenario) correrán por cuenta de la parte indicada en el apartado económico."),
            new ContractClause(6, "Cancelación y fuerza mayor",
                "Ambas partes quedan exoneradas de responsabilidad en caso de fuerza mayor debidamente acreditada (causas meteorológicas extremas, decisiones gubernamentales, emergencias sanitarias, etc.). En tal caso se procederá a la devolución de los importes ya abonados, deducidos los gastos irrecuperables acreditados."),
            new ContractClause(7, "Confidencialidad",
                "Ambas partes se comprometen a mantener en estricta confidencialidad todos los términos económicos del presente contrato, así como cualquier información sensible sobre el evento privado y sus asistentes, durante la vigencia del contrato y durante los dos años siguientes a su extinción.")
        );
    }
}
