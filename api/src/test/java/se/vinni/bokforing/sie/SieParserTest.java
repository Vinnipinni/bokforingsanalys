package se.vinni.bokforing.sie;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @Test
    void läserVerifikatMedTransaktioner() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/sie4-exempel.se")) {
            List<Verifikat> verifikat = new SieParser().parseVerifikat(in);

            Verifikat v = verifikat.stream()
                    .filter(x -> x.serie().equals("A") && x.nummer().equals("5"))
                    .findFirst()
                    .orElseThrow();

            assertEquals(LocalDate.of(2021, 1, 12), v.datum());
            assertEquals("Uttag till kassa", v.text());
            assertEquals(2, v.transaktioner().size());
            assertEquals("1910", v.transaktioner().get(0).konto());
            assertEquals(new BigDecimal("2000.00"), v.transaktioner().get(0).belopp());
            assertEquals(new BigDecimal("-2000.00"), v.transaktioner().get(1).belopp());
        }
    }

    @Test
    void behandlarDimensionsfältSomEttFält() {
        String[] fält = SieParser.delaUppFält("#TRANS 3041 {1 Nord 6 0001} -52625.00");
        assertArrayEquals(
                new String[]{"#TRANS", "3041", "1 Nord 6 0001", "-52625.00"},
                fält);
    }

    @Test
    void allaVerifikatBalanserar() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/sie4-exempel.se")) {
            List<Verifikat> verifikat = new SieParser().parseVerifikat(in);
            assertFalse(verifikat.isEmpty());

            for (Verifikat v : verifikat) {
                BigDecimal summa = v.transaktioner().stream()
                        .map(Transaktion::belopp)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                assertEquals(0, summa.compareTo(BigDecimal.ZERO),
                        "Verifikat " + v.serie() + v.nummer() + " balanserar inte: " + summa);
            }
        }
    }

    @Test
    void beräknarSaldoPerKonto() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/sie4-exempel.se")) {
            List<Verifikat> verifikat = new SieParser().parseVerifikat(in);
            Map<String, BigDecimal> saldon = Saldoberäkning.perKonto(verifikat);

            BigDecimal kassa = saldon.get("1910");
            assertNotNull(kassa);

            BigDecimal totalt = saldon.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            assertEquals(0, totalt.compareTo(BigDecimal.ZERO),
                    "Summan av alla saldon ska vara noll: " + totalt);
        }
    }
}