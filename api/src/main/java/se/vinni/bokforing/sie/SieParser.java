package se.vinni.bokforing.sie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SieParser {

    private static final Charset PC8 = Charset.forName("Cp437");

    public String parseFöretagsnamn(InputStream in) throws IOException {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, PC8))) {
            String rad;
            while ((rad = r.readLine()) != null) {
                if (rad.startsWith("#FNAMN")) {
                    int start = rad.indexOf('"');
                    int slut = rad.lastIndexOf('"');
                    if (start >= 0 && slut > start) {
                        return rad.substring(start + 1, slut);
                    }
                }
            }
        }
        return null;
    }

    public Map<String, String> parseKonton(InputStream in) throws IOException {
        Map<String, String> konton = new HashMap<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, PC8))) {
            String rad;
            while ((rad = r.readLine()) != null) {
                if (rad.startsWith("#KONTO ")) {
                    String[] fält = delaUppFält(rad);
                    if (fält.length >= 3) {
                        konton.put(fält[1], fält[2]);
                    }
                }
            }
        }
        return konton;
    }

    public Map<String, BigDecimal> parseIngåendeBalans(InputStream in) throws IOException {
        Map<String, BigDecimal> ib = new HashMap<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, PC8))) {
            String rad;
            while ((rad = r.readLine()) != null) {
                if (rad.startsWith("#IB ")) {
                    String[] fält = delaUppFält(rad);
                    // fält: #IB, årsindex, konto, belopp
                    if (fält.length >= 4 && fält[1].equals("0")) {
                        ib.put(fält[2], new BigDecimal(fält[3]));
                    }
                }
            }
        }
        return ib;
    }

    public static String[] delaUppFält(String rad) {
        List<String> fält = new ArrayList<>();
        StringBuilder aktuellt = new StringBuilder();
        boolean iCitat = false;
        boolean iMåsvinge = false;

        for (char c : rad.toCharArray()) {
            if (c == '"') {
                iCitat = !iCitat;
            } else if (c == '{' && !iCitat) {
                iMåsvinge = true;
            } else if (c == '}' && !iCitat) {
                iMåsvinge = false;
                fält.add(aktuellt.toString());
                aktuellt.setLength(0);
            } else if (c == ' ' && !iCitat && !iMåsvinge) {
                if (aktuellt.length() > 0) {
                    fält.add(aktuellt.toString());
                    aktuellt.setLength(0);
                }
            } else {
                aktuellt.append(c);
            }
        }
        if (aktuellt.length() > 0) {
            fält.add(aktuellt.toString());
        }
        return fält.toArray(new String[0]);
    }

    public List<Verifikat> parseVerifikat(InputStream in) throws IOException {
        List<Verifikat> verifikat = new ArrayList<>();

        String serie = null;
        String nummer = null;
        LocalDate datum = null;
        String text = null;
        List<Transaktion> transaktioner = new ArrayList<>();
        boolean iVerifikat = false;

        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, PC8))) {
            String rad;
            while ((rad = r.readLine()) != null) {
                rad = rad.trim();

                if (rad.startsWith("#VER ")) {
                    String[] fält = delaUppFält(rad);
                    serie = fält[1];
                    nummer = fält[2];
                    datum = LocalDate.parse(fält[3], DateTimeFormatter.BASIC_ISO_DATE);
                    text = fält.length > 4 ? fält[4] : "";
                    transaktioner = new ArrayList<>();

                } else if (rad.equals("{")) {
                    iVerifikat = true;

                } else if (rad.equals("}")) {
                    iVerifikat = false;
                    verifikat.add(new Verifikat(serie, nummer, datum, text, transaktioner));

                } else if (iVerifikat && rad.startsWith("#TRANS ")) {
                    String[] fält = delaUppFält(rad);
                    transaktioner.add(new Transaktion(fält[1], new BigDecimal(fält[3])));
                }
            }
        }
        return verifikat;
    }
}