package se.vinni.bokforing;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.vinni.bokforing.sie.Saldoberäkning;
import se.vinni.bokforing.sie.SieParser;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api")
public class ImportController {

    private final SieParser parser = new SieParser();
    private final NyckeltalsKlient nyckeltalsKlient;

    public ImportController(NyckeltalsKlient nyckeltalsKlient) {
        this.nyckeltalsKlient = nyckeltalsKlient;
    }

    @PostMapping("/import")
    public ResponseEntity<ImportResultat> importera(@RequestParam("fil") MultipartFile fil) throws IOException {
        if (fil.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String namn;
        try (InputStream in = fil.getInputStream()) {
            namn = parser.parseFöretagsnamn(in);
        }

        var konton = parseMed(fil, in -> parser.parseKonton(in));
        var verifikat = parseMed(fil, in -> parser.parseVerifikat(in));

        var saldon = Saldoberäkning.perKonto(verifikat);
        var resultatkonton = Saldoberäkning.resultatkonton(saldon);
        var balanskonton = Saldoberäkning.balanskonton(saldon);

        return ResponseEntity.ok(new ImportResultat(
                namn,
                konton,
                verifikat,
                resultatkonton,
                balanskonton,
                nyckeltalsKlient.hämta(resultatkonton, balanskonton)));
    }

    private <T> T parseMed(MultipartFile fil, ParserFunktion<T> f) throws IOException {
        try (InputStream in = fil.getInputStream()) {
            return f.apply(in);
        }
    }

    @FunctionalInterface
    private interface ParserFunktion<T> {
        T apply(InputStream in) throws IOException;
    }
}