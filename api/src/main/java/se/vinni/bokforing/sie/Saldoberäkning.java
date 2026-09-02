package se.vinni.bokforing.sie;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Saldoberäkning {

    public static Map<String, BigDecimal> perKonto(List<Verifikat> verifikat) {
        Map<String, BigDecimal> saldon = new HashMap<>();

        for (Verifikat v : verifikat) {
            for (Transaktion t : v.transaktioner()) {
                saldon.merge(t.konto(), t.belopp(), BigDecimal::add);
            }
        }
        return saldon;
    }
}