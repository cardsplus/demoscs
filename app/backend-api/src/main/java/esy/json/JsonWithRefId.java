package esy.json;

import lombok.NonNull;

import java.util.UUID;

public interface JsonWithRefId<S> {

    /**
     * Erzeugt eine neues Instanz mit neuer UUID. Alle
     * anderen Eigenschaften bleiben identisch. Das
     * gilt vor allem für alle Arten von Collections,
     * d.h. keine neuen Collections mit den gleichen
     * Elementen!
     *
     * @param refId Neue UUID
     * @return Neue Instanz
     */
    S withRefId(@NonNull final UUID refId);
}
