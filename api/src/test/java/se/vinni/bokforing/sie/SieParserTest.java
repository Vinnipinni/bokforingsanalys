package se.vinni.bokforing.sie;

import org.junit.jupiter.api.Test;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SieParserTest {

    @Test
    void läserFöretagsnamnMedSvenskaTecken() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/sie4-exempel.se")) {
            String namn = new SieParser().parseFöretagsnamn(in);
            assertEquals("Övningsbolaget AB", namn);
        }
    }
}