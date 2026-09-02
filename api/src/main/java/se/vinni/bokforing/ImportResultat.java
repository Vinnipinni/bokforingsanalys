package se.vinni.bokforing;

import se.vinni.bokforing.sie.Verifikat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ImportResultat(
        String företagsnamn,
        Map<String, String> konton,
        List<Verifikat> verifikat,
        Map<String, BigDecimal> resultatkonton,
        Map<String, BigDecimal> balanskonton,
        Map<String, BigDecimal> nyckeltal) {
}