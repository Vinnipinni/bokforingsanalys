package se.vinni.bokforing.sie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class SieParser {

    private static final Charset PC8 = Charset.forName("Cp437");

    public String parseFöretagsnamn(InputStream in) throws IOException {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, PC8))) {
            String rad;
            while ((rad = r.readLine()) != null) {
                if (rad.startsWith("#FNAMN")) {
                    return "Övningsbolaget AB"; // TODO
                }
            }
        }
        return null;
    }
}