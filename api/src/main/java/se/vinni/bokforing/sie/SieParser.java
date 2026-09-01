package se.vinni.bokforing.sie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
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
                    String[] delar = rad.split(" ", 3);
                    if (delar.length == 3) {
                        String nummer = delar[1];
                        String namn = delar[2].trim();
                        if (namn.startsWith("\"") && namn.endsWith("\"")) {
                            namn = namn.substring(1, namn.length() - 1);
                        }
                        konton.put(nummer, namn);
                    }
                }
            }
        }
        return konton;
    }
}