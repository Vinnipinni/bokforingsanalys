package se.vinni.bokforing.sie;

import org.junit.jupiter.api.Test;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Map;

class SieParserTest {

    @Test
    void läserFöretagsnamnMedSvenskaTecken() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/sie4-exempel.se")) {
            String namn = new SieParser().parseFöretagsnamn(in);
            assertEquals("Övningsbolaget AB", namn);
        }
    }  
  @Test
    void läserKontoplan() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/sie4-exempel.se")) {
            Map<String, String> konton = new SieParser().parseKonton(in);
            assertEquals("Hyresrätt", konton.get("1060"));
            assertEquals("Ack nedskrivn hyresrätt", konton.get("1068"));
        }
    }
}