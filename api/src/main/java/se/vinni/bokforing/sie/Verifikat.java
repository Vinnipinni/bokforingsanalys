package se.vinni.bokforing.sie;

import java.time.LocalDate;
import java.util.List;

public record Verifikat(
        String serie,
        String nummer,
        LocalDate datum,
        String text,
        List<Transaktion> transaktioner) {
}