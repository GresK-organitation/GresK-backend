package com.gresk.modules.musicdna.domain.model;

import com.gresk.modules.musicdna.domain.port.out.MusicDnaSignals;
import com.gresk.modules.user.domain.model.UserId;

import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Perfil de 6 dimensiones de cómo un usuario vive la música en directo.
 * Agregado materializado: se recalcula por completo cada vez (nunca se
 * muta una dimensión aislada), ver {@link #calculate(UserId, MusicDnaSignals)}.
 */
public final class UserMusicDna {

    private static final String NO_DATA_SUMMARY = "Sin datos suficientes";

    private final UserMusicDnaId id;
    private final UserId         userId;
    private final DimensionScore intensidad;
    private final DimensionScore diversidad;
    private final DimensionScore criticidad;
    private final DimensionScore localismo;
    private final DimensionScore antiguedad;
    private final DimensionScore autenticidad;
    private final String         summaryPhrase;
    private final Instant        calculatedAt;

    private UserMusicDna(UserMusicDnaId id, UserId userId,
                          DimensionScore intensidad, DimensionScore diversidad, DimensionScore criticidad,
                          DimensionScore localismo, DimensionScore antiguedad, DimensionScore autenticidad,
                          String summaryPhrase, Instant calculatedAt) {
        this.id            = Objects.requireNonNull(id, "UserMusicDnaId is required");
        this.userId        = Objects.requireNonNull(userId, "UserId is required");
        this.intensidad    = Objects.requireNonNull(intensidad, "intensidad is required");
        this.diversidad    = Objects.requireNonNull(diversidad, "diversidad is required");
        this.criticidad    = Objects.requireNonNull(criticidad, "criticidad is required");
        this.localismo     = Objects.requireNonNull(localismo, "localismo is required");
        this.antiguedad    = Objects.requireNonNull(antiguedad, "antiguedad is required");
        this.autenticidad  = Objects.requireNonNull(autenticidad, "autenticidad is required");
        this.summaryPhrase = Objects.requireNonNull(summaryPhrase, "summaryPhrase is required");
        this.calculatedAt  = Objects.requireNonNull(calculatedAt, "calculatedAt is required");
    }

    /**
     * Calcula el ADN completo a partir de las señales agregadas del usuario.
     * Si el usuario no tiene ningún concierto documentado (review o journal
     * entry), devuelve un ADN "vacío" en vez de dividir por cero.
     */
    public static UserMusicDna calculate(UserId userId, MusicDnaSignals signals) {
        Objects.requireNonNull(signals, "MusicDnaSignals is required");

        if (signals.totalDocumented() == 0) {
            DimensionScore zero = DimensionScore.zero();
            return new UserMusicDna(UserMusicDnaId.generate(), userId,
                    zero, zero, zero, zero, zero, zero, NO_DATA_SUMMARY, Instant.now());
        }

        DimensionScore intensidad   = DimensionScore.fromRatio(MusicDnaDimension.INTENSIDAD,   MusicDnaFormulas.intensidad(signals));
        DimensionScore diversidad   = DimensionScore.fromRatio(MusicDnaDimension.DIVERSIDAD,   MusicDnaFormulas.diversidad(signals));
        DimensionScore criticidad   = DimensionScore.fromRatio(MusicDnaDimension.CRITICIDAD,   MusicDnaFormulas.criticidad(signals));
        DimensionScore localismo    = DimensionScore.fromRatio(MusicDnaDimension.LOCALISMO,    MusicDnaFormulas.localismo(signals));
        DimensionScore antiguedad   = DimensionScore.fromRatio(MusicDnaDimension.ANTIGUEDAD,   MusicDnaFormulas.antiguedadYears(signals));
        DimensionScore autenticidad = DimensionScore.fromRatio(MusicDnaDimension.AUTENTICIDAD, MusicDnaFormulas.autenticidad(signals));

        String summaryPhrase = buildSummaryPhrase(intensidad, diversidad, criticidad, localismo, antiguedad, autenticidad);

        return new UserMusicDna(UserMusicDnaId.generate(), userId,
                intensidad, diversidad, criticidad, localismo, antiguedad, autenticidad,
                summaryPhrase, Instant.now());
    }

    public static UserMusicDna reconstitute(UserMusicDnaId id, UserId userId,
                                             DimensionScore intensidad, DimensionScore diversidad, DimensionScore criticidad,
                                             DimensionScore localismo, DimensionScore antiguedad, DimensionScore autenticidad,
                                             String summaryPhrase, Instant calculatedAt) {
        return new UserMusicDna(id, userId, intensidad, diversidad, criticidad,
                localismo, antiguedad, autenticidad, summaryPhrase, calculatedAt);
    }

    private static String buildSummaryPhrase(DimensionScore... scores) {
        return Stream.of(scores)
                .sorted(Comparator.comparing(DimensionScore::score).reversed())
                .limit(3)
                .map(DimensionScore::label)
                .collect(Collectors.joining(" · "));
    }

    public UserMusicDnaId getId()            { return id; }
    public UserId         getUserId()        { return userId; }
    public DimensionScore getIntensidad()    { return intensidad; }
    public DimensionScore getDiversidad()    { return diversidad; }
    public DimensionScore getCriticidad()    { return criticidad; }
    public DimensionScore getLocalismo()     { return localismo; }
    public DimensionScore getAntiguedad()    { return antiguedad; }
    public DimensionScore getAutenticidad()  { return autenticidad; }
    public String         getSummaryPhrase() { return summaryPhrase; }
    public Instant         getCalculatedAt() { return calculatedAt; }
}
