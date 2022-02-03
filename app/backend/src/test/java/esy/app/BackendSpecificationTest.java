package esy.app;

import esy.api.info.Version;
import esy.api.team.Nutzer;
import esy.api.plan.Aufgabe;
import esy.api.plan.Projekt;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("fast")
public class BackendSpecificationTest {

    private <T> Path restApiFor(final Class<T> clazz) {
        return Paths.get(String.format("src/main/java/%sRestApi.adoc",
                clazz.getName()
                        .replace("esy.api.", "esy/app/")
                        .replaceAll("\\.", "/")));
    }

    @Test
    void service() {
        // check existence of all adoc files for doc/service.adoc
        assertTrue(Files.exists(restApiFor(Aufgabe.class)));
        assertTrue(Files.exists(restApiFor(Nutzer.class)));
        assertTrue(Files.exists(restApiFor(Projekt.class)));
        assertTrue(Files.exists(restApiFor(Version.class)));
    }
}
