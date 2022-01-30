package esy.app.info;

import esy.api.info.EnumItem;
import esy.api.info.EnumValue;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@BasePathAwareController
@RequiredArgsConstructor
public class EnumValueRestController {

    private final EnumValueRepository enumValueRepository;

    @GetMapping("/enum/{art}")
    public ResponseEntity<CollectionModel<EnumItem>> enumOf(@PathVariable final String art) {
        var allEnum = enumValueRepository.findAll(art)
                .stream()
                .map(EnumItem::new)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(allEnum));
    }

    @PostMapping("/enum/{art}")
    public ResponseEntity<EnumItem> createOf(@PathVariable final String art, @RequestBody final EnumItem body) {
        var value = enumValueRepository.save(
                EnumValue.parseJson("{}")
                        .setArt(art)
                        .setCode(body.getCode())
                        .setName(body.getName())
                        .setText(body.getText()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new EnumItem(value));
    }

    @PutMapping("/enum/{art}/{code}")
    public ResponseEntity<EnumItem> updateOf(@PathVariable final String art, @PathVariable final Long code, @RequestBody final EnumItem body) {
        var value = enumValueRepository.save(
                enumValueRepository.findByCode(art, code)
                        .orElseThrow(() ->
                                new DataRetrievalFailureException("EnumValue(" + art + ", " + code + ") not found"))
                        .setName(body.getName())
                        .setText(body.getText()));
        return ResponseEntity.status(HttpStatus.OK).body(new EnumItem(value));
    }
}
