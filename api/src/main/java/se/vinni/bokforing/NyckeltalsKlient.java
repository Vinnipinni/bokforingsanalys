package se.vinni.bokforing;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class NyckeltalsKlient {

    private final RestClient klient;

    public NyckeltalsKlient(@Value("${analys.url:http://localhost:8000}") String analysUrl) {
        this.klient = RestClient.create(analysUrl);
    }

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