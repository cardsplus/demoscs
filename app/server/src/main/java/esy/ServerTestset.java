package esy;

import esy.api.info.EnumValue;
import esy.app.info.EnumValueRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import scs.api.clinic.Vet;
import scs.api.clinic.Visit;
import scs.api.owner.Owner;
import scs.api.owner.Pet;
import scs.app.clinic.VetRepository;
import scs.app.clinic.VisitRepository;
import scs.app.owner.OwnerRepository;
import scs.app.owner.PetRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;

@Slf4j
@Component
public class ServerTestset implements CommandLineRunner {

    @Autowired
    private EnumRepository enumRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Override
    @Transactional
    public void run(final String... args) throws Exception {
        if (ownerRepository.count() != 0) {
            return;
        }

        final Map<String, EnumValue> allEnumSkill = createAllEnumSkill();
        allEnumSkill.values().forEach(e -> log.info("CREATED [{}]", e));

        final Map<String, Owner> allOwner = createAllOwner();
        allOwner.values().forEach(e -> log.info("CREATED [{}]", e));

        final Map<String, Pet> allPet = createAllPet(allOwner);
        allPet.values().forEach(e -> log.info("CREATED [{}]", e));

        final Map<String, Vet> allVet = createAllVet();
        allVet.values().forEach(e -> log.info("CREATED [{}]", e));

        final List<Visit> allVisit = createAllVisit(allPet, allVet);
        allVisit.forEach(e -> log.info("CREATED [{}]", e));
    }

    @Transactional
    private Map<String, EnumValue> createAllEnumSkill() {
        return Stream.of(
                        Enum.parseJson("{" +
                                "\"code\": 0," +
                                "\"name\": \"radiology\"," +
                                "\"text\": \"Radiology is the medical discipline that uses medical imaging to diagnose and treat diseases within the bodies of animals and humans\"" +
                                "}"),
                        Enum.parseJson("{" +
                                "\"code\": 1," +
                                "\"name\": \"dentistry\"," +
                                "\"text\": \"Dentistry is a branch of medicine that consists of the study, diagnosis, prevention, and treatment of diseases, disorders, and conditions of the oral cavity (the mouth).\"" +
                                "}"),
                        Enum.parseJson("{" +
                                "\"code\": 2," +
                                "\"name\": \"surgery\"," +
                                "\"text\": \"Surgery he branch of medical practice that treats injuries, diseases, and deformities by the physical removal, repair, or readjustment of organs and tissues.\"" +
                                "}"))
                .map(e -> e.setArt("skill"))
                .map(enumValueRepository::save)
                .collect(Collectors.toMap(EnumValue::getName, identity()));
    }

    @Transactional
    private Map<String, Owner> createAllOwner() {
        return Stream.of(
                        Owner.parseJson("{" +
                                "\"name\": \"Thomas Mann\"" +
                                "}"),
                        Owner.parseJson("{" +
                                "\"name\": \"Stefan Zweig\"" +
                                "}"),
                        Owner.parseJson("{" +
                                "\"name\": \"Wolfgang A. Mozart\"" +
                                "}"),
                        Owner.parseJson("{" +
                                "\"name\": \"Arthur Conan Doyle\"" +
                                "}"))
                .map(ownerRepository::save)
                .collect(Collectors.toMap(Owner::getName, identity()));
    }

    @Transactional
    private Map<String, Pet> createAllPet(final Map<String, Owner> allOwner) {
        return Stream.of(
                        Pet.parseJson("{" +
                                        "\"name\": \"Tom\"" +
                                        "}")
                                .setOwner(allOwner.get("Thomas Mann")),
                        Pet.parseJson("{" +
                                        "\"name\": \"Odi\"" +
                                        "}")
                                .setOwner(allOwner.get("Thomas Mann")),
                        Pet.parseJson("{" +
                                        "\"name\": \"Fox\"" +
                                        "}")
                                .setOwner(allOwner.get("Stefan Zweig")))
                .map(petRepository::save)
                .collect(Collectors.toMap(Pet::getName, identity()));
    }

    @Transactional
    private Map<String, Vet> createAllVet() {
        return Stream.of(
                        Vet.parseJson("{" +
                                "\"name\": \"Graham Chapman\"" +
                                "}"),
                        Vet.parseJson("{" +
                                "\"name\": \"John Cleese\"" +
                                "}"),
                        Vet.parseJson("{" +
                                "\"name\": \"Terry Gilliam\"" +
                                "}"),
                        Vet.parseJson("{" +
                                "\"name\": \"Eric Idle\"" +
                                "}"),
                        Vet.parseJson("{" +
                                "\"name\": \"Terry Jones\"" +
                                "}"))
                .map(vetRepository::save)
                .collect(Collectors.toMap(Vet::getName, identity()));
    }

    @Transactional
    private List<Visit> createAllVisit(Map<String, Pet> allPet, Map<String, Vet> allVet) {
        return Stream.of(
                        Visit.parseJson("{\"date\": \"2021-04-21\"}")
                                .setPet(allPet.get("Tom"))
                                .setVet(allVet.get("Graham Chapman")),
                        Visit.parseJson("{\"date\": \"2021-04-21\"}")
                                .setPet(allPet.get("Odi"))
                                .setVet(allVet.get("Graham Chapman")),
                        Visit.parseJson("{\"date\": \"2021-04-22\"}")
                                .setPet(allPet.get("Odi"))
                                .setVet(allVet.get("John Cleese")),
                        Visit.parseJson("{\"date\": \"2021-04-23\"}")
                                .setPet(allPet.get("Odi"))
                                .setVet(allVet.get("Terry Gilliam")),
                        Visit.parseJson("{\"date\": \"2021-04-24\"}")
                                .setPet(allPet.get("Odi"))
                                .setVet(allVet.get("Eric Idle")),
                        Visit.parseJson("{\"date\": \"2021-04-24\"}")
                                .setPet(allPet.get("Fox"))
                                .setVet(allVet.get("Terry Jones"))
                )
                .map(visitRepository::save)
                .collect(Collectors.toList());
    }
}
