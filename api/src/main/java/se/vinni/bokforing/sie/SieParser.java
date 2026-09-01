package se.vinni.bokforing.sie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

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

    public static String[] delaUppFält(String rad) {
        List<String> fält = new ArrayList<>();
        StringBuilder aktuellt = new StringBuilder();
        boolean iCitat = false;

        for (char c : rad.toCharArray()) {
            if (c == '"') {
                iCitat = !iCitat;
            } else if (c == ' ' && !iCitat) {
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
}
