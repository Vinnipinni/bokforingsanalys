package se.vinni.bokforing.sie;

import java.math.BigDecimal;

public record Transaktion(String konto, BigDecimal belopp) {
}