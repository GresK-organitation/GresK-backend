package com.gresk.modules.email;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * La API key de Anthropic (y el resto de credenciales del módulo) no debe
 * aparecer nunca en sentencias de log. Escanea el código fuente del módulo
 * buscando líneas log.*(...) que referencien claves o tokens.
 */
class ApiKeyLoggingTest {

    private static final Path MODULE_SOURCES = Path.of("src/main/java/com/gresk/modules/email");

    private static final Pattern LOG_WITH_SECRET = Pattern.compile(
            "log\\.(trace|debug|info|warn|error)\\([^;]*"
                    + "(apiKey|api[-_]key|ANTHROPIC_API_KEY|accessToken|refreshToken|getAccessToken|getRefreshToken|clientSecret)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    @Test
    void ningunaSentenciaDeLogDelModuloReferenciaCredenciales() throws IOException {
        try (Stream<Path> files = Files.walk(MODULE_SOURCES)) {
            List<String> violations = files
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(this::containsSecretInLogStatement)
                    .map(Path::toString)
                    .toList();

            assertTrue(violations.isEmpty(),
                    "Estos ficheros loguean credenciales (API keys / tokens OAuth): " + violations);
        }
    }

    private boolean containsSecretInLogStatement(Path file) {
        try {
            return LOG_WITH_SECRET.matcher(Files.readString(file)).find();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
