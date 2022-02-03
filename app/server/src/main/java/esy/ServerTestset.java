package esy;


import esy.api.info.EnumValue;
import esy.api.plan.Aufgabe;
import esy.api.plan.Projekt;
import esy.api.team.Nutzer;
import esy.app.info.EnumValueRepository;
import esy.app.plan.AufgabeRepository;
import esy.app.plan.ProjektRepository;
import esy.app.team.NutzerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;

@Slf4j
@Component
public class ServerTestset implements CommandLineRunner {

    static final List<String> allLoremIpsum = List.of(
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.",
            "At vero eos et accusam et justo duo dolores et ea rebum.",
            "Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.",
            "Magni accusantium labore et id quis provident.",
            "Consectetur impedit quisquam qui deserunt non rerum consequuntur eius.",
            "Quia atque aliquam sunt impedit voluptatum rerum assumenda nisi.",
            "Cupiditate quos possimus corporis quisquam exercitationem beatae."
    );

    @Autowired
    private AufgabeRepository aufgabeRepository;

    @Autowired
    private EnumValueRepository enumValueRepository;

    @Autowired
    private NutzerRepository nutzerRepository;

    @Autowired
    private ProjektRepository projektRepository;

    @Override
    @Transactional
    public void run(final String... args) throws Exception {
        if (nutzerRepository.count() != 0) {
            return;
        }

        final Map<String, EnumValue> allEnumSprache = createAllEnumSprache();
        allEnumSprache.values().forEach(e -> log.info("CREATED [{}]", e));

        final Map<String, Nutzer> allNutzer = createAllNutzer();
        allNutzer.values().forEach(e -> log.info("CREATED [{}]", e));

        final Map<String, Projekt> allProjekt = createAllProjekt(allNutzer);
        allProjekt.values().forEach(e -> log.info("CREATED [{}]", e));

        final List<Aufgabe> allAufgabe = createAllAufgabe(allProjekt);
        allAufgabe.forEach(e -> log.info("CREATED [{}]", e));
    }

    @Transactional
    private Map<String, EnumValue> createAllEnumSprache() {
        return Stream.of(
                        EnumValue.parseJson("{" +
                                "\"code\": 0," +
                                "\"name\": \"DE\"," +
                                "\"text\": \"Deutsch\"" +
                                "}"),
                        EnumValue.parseJson("{" +
                                "\"code\": 1," +
                                "\"name\": \"EN\"," +
                                "\"text\": \"Englisch\"" +
                                "}"),
                        EnumValue.parseJson("{" +
                                "\"code\": 2," +
                                "\"name\": \"IT\"," +
                                "\"text\": \"Italienisch\"" +
                                "}"),
                        EnumValue.parseJson("{" +
                                "\"code\": 3," +
                                "\"name\": \"FR\"," +
                                "\"text\": \"Französisch\"" +
                                "}"))
                .map(e -> e.setArt("sprache"))
                .map(enumValueRepository::save)
                .collect(Collectors.toMap(EnumValue::getName, identity()));
    }

    @Transactional
    private Map<String, Nutzer> createAllNutzer() {
        return Stream.of(
                        Nutzer.parseJson("{" +
                                        "\"mail\": \"bruckbauer@gmx.at" + "\"," +
                                        "\"name\": \"Robert Bruckbauer\"," +
                                        "\"allSprache\": [\"DE\", \"EN\"]," +
                                        "\"aktiv\": \"true\"" +
                                        "}"),
                        Nutzer.parseJson("{" +
                                        "\"mail\": \"brombertje@gmail.com" + "\"," +
                                        "\"name\": \"Bertram Bär\"," +
                                        "\"allSprache\": [\"DE\", \"EN\"]," +
                                        "\"aktiv\": \"true\"" +
                                        "}"),
                        Nutzer.parseJson("{" +
                                        "\"mail\": \"max.mustermann@firma.de" + "\"," +
                                        "\"name\": \"Max Mustermann\"," +
                                        "\"allSprache\": [\"DE\", \"EN\"]," +
                                        "\"aktiv\": \"true\"" +
                                        "}"),
                        Nutzer.parseJson("{" +
                                        "\"mail\": \"mia.musterfrau@firma.de" + "\"," +
                                        "\"name\": \"Mia Musterfrau\"," +
                                        "\"allSprache\": [\"DE\", \"IT\"]," +
                                        "\"aktiv\": \"true\"" +
                                        "}"),
                        Nutzer.parseJson("{" +
                                        "\"mail\": \"szweig@gmail.com" + "\"," +
                                        "\"name\": \"Stefan Zweig\"," +
                                        "\"allSprache\": [\"DE\"]," +
                                        "\"aktiv\": \"false\"" +
                                        "}"),
                        Nutzer.parseJson("{" +
                                        "\"mail\": \"mozart@gmail.com" + "\"," +
                                        "\"name\": \"Wolfgang A. Mozart\"," +
                                        "\"allSprache\": [\"DE\"]," +
                                        "\"aktiv\": \"false\"" +
                                        "}"),
                        Nutzer.parseJson("{" +
                                        "\"mail\": \"doyle@gmail.com" + "\"," +
                                        "\"name\": \"Arthur Conan Doyle\"," +
                                        "\"allSprache\": [\"EN\"]," +
                                        "\"aktiv\": \"false\"" +
                                        "}"))
                .map(nutzerRepository::save)
                .collect(Collectors.toMap(Nutzer::getMail, identity()));
    }

    @Transactional
    private Map<String, Projekt> createAllProjekt(final Map<String, Nutzer> allNutzer) {
        return Stream.of(
                        Projekt.parseJson("{" +
                                        "\"name\": \"Projekt Alpha\"," +
                                        "\"sprache\": \"DE\"," +
                                        "\"aktiv\": \"true\"" +
                                        "}")
                                .setBesitzer(allNutzer.get("max.mustermann@firma.de"))
                                .addMitglied(allNutzer.get("max.mustermann@firma.de")),
                        Projekt.parseJson("{" +
                                        "\"name\": \"Projekt Beta\"," +
                                        "\"sprache\": \"DE\"," +
                                        "\"aktiv\": \"true\"" +
                                        "}")
                                .setBesitzer(allNutzer.get("mia.musterfrau@firma.de"))
                                .addMitglied(allNutzer.get("mia.musterfrau@firma.de"))
                                .addMitglied(allNutzer.get("max.mustermann@firma.de"))
                                .addMitglied(allNutzer.get("szweig@gmail.com"))
                                .addMitglied(allNutzer.get("mozart@gmail.com"))
                                .addMitglied(allNutzer.get("doyle@gmail.com")),
                        Projekt.parseJson("{" +
                                        "\"name\": \"Projekt Gamma\"," +
                                        "\"sprache\": \"EN\"," +
                                        "\"aktiv\": \"true\"" +
                                        "}")
                                .setBesitzer(allNutzer.get("bruckbauer@gmx.at")))
                .map(projektRepository::save)
                .collect(Collectors.toMap(Projekt::getName, identity()));
    }

    private Aufgabe createLoremIpsumAufgabe(final int index) {
        final String text = allLoremIpsum.get(index);
        return Aufgabe.parseJson("{" +
                        "\"text\": \"" + text + "\"," +
                        "\"aktiv\": \"true\"" +
                        "}");
    }

    @Transactional
    private List<Aufgabe> createAllAufgabe(final Map<String, Projekt> allProjekt) {
        return Stream.of(
                        createLoremIpsumAufgabe(0)
                                .setProjekt(allProjekt.get("Projekt Alpha")),
                        createLoremIpsumAufgabe(1)
                                .setProjekt(allProjekt.get("Projekt Alpha")),
                        createLoremIpsumAufgabe(2)
                                .setProjekt(allProjekt.get("Projekt Alpha")),
                        createLoremIpsumAufgabe(3)
                                .setProjekt(allProjekt.get("Projekt Alpha")),
                        createLoremIpsumAufgabe(4)
                                .setProjekt(allProjekt.get("Projekt Alpha")),
                        createLoremIpsumAufgabe(5)
                                .setProjekt(allProjekt.get("Projekt Alpha")),
                        createLoremIpsumAufgabe(6)
                                .setProjekt(allProjekt.get("Projekt Alpha")),
                        Aufgabe.parseJson("{" +
                                        "\"text\": \"" +
                                        "Bis 100:\\n123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789\\n" +
                                        "Bis 200:\\n123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789\\n" +
                                        "Bis 300:\\n123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789\\n" +
                                        "Bis 400:\\n123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789\\n" +
                                        "Bis 500:\\n123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789\\n" +
                                        "Bis 512:\\n1234567\\n" +
                                        "\"," +
                                        "\"aktiv\": \"true\"" +
                                        "}")
                                .setProjekt(allProjekt.get("Projekt Beta")))
                .map(aufgabeRepository::save)
                .collect(Collectors.toList());
    }
}
