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

    public static Map<String, BigDecimal> resultatkonton(Map<String, BigDecimal> saldon) {
        return filtrera(saldon, '3', '4', '5', '6', '7', '8');
    }

    public static Map<String, BigDecimal> balanskonton(Map<String, BigDecimal> saldon) {
        return filtrera(saldon, '1', '2');
    }

    private static Map<String, BigDecimal> filtrera(Map<String, BigDecimal> saldon, char... klasser) {
        Map<String, BigDecimal> resultat = new HashMap<>();

        for (Map.Entry<String, BigDecimal> post : saldon.entrySet()) {
            char första = post.getKey().charAt(0);
            for (char klass : klasser) {
                if (första == klass) {
                    resultat.put(post.getKey(), post.getValue());
                    break;
                }
            }
        }
        return resultat;
    }
}