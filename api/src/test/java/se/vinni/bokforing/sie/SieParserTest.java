package se.vinni.bokforing.sie;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    void delarUppFältMedCiteradeVärden() {
        String[] fält = SieParser.delaUppFält("#VER A 5 20210112 \"Uttag till kassa\" 20210310");
        assertArrayEquals(
                new String[]{"#VER", "A", "5", "20210112", "Uttag till kassa", "20210310"},
                fält);
    }
}