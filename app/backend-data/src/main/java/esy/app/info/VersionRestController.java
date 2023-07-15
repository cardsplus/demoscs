package esy.app.info;

import esy.api.info.Version;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class VersionRestController {

    private final VersionRepository repository;

    @GetMapping(
            value = "/version",
            produces = "application/json"
    )
    public ResponseEntity<Version> json() {
        final Version version = repository.find();
        return ResponseEntity.ok().body(version);
    }

    @GetMapping(
            value = {"/version", "version.adoc"},
            produces = "text/asciidoc;charset=UTF-8"
    )
    public ResponseEntity<String> adoc() {
        final Version version = repository.find();
        final String adoc = version + "\n";
        return ResponseEntity.ok().body(adoc);
    }

    @GetMapping(
            value = {"/version", "version.html"},
            produces = "text/html;charset=UTF-8"
    )
    public ResponseEntity<String> html() {
        final Version version = repository.find();
        final String html = "<span id=\"version\">" + version + "</span><br/>\n";
        return ResponseEntity.ok().body(html);
    }
}
