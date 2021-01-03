package esy.json;

import lombok.NonNull;

import java.util.UUID;

public interface JsonWithDataId<S> {

    /**
     * Erzeugt eine neues Instanz mit neuer UUID. Alle
     * anderen Eigenschaften bleiben identisch. Das
     * gilt vor allem für alle Arten von Collections,
     * d.h. keine neuen Collections mit den gleichen
     * Elementen!
     *
     * @param dataId Neue UUID
     * @return Neue Instanz
     */
    S withDataId(@NonNull final UUID dataId);
}
