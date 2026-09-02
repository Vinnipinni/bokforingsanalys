package se.vinni.bokforing;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class NyckeltalsKlient {

    private final RestClient klient = RestClient.create("http://localhost:8000");

    public Map<String, BigDecimal> hämta(
            Map<String, BigDecimal> resultatkonton,
            Map<String, BigDecimal> balanskonton) {

        try {
            return klient.post()
                    .uri("/nyckeltal")
                    .body(Map.of(
                            "resultatkonton", resultatkonton,
                            "balanskonton", balanskonton))
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            return Map.of();
        }
    }
}