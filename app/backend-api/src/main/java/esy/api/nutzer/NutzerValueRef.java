package esy.api.nutzer;

import esy.json.JsonJpaValueRef;
import esy.json.JsonMapper;
import lombok.NonNull;

import javax.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public final class NutzerValueRef extends JsonJpaValueRef<NutzerValueRef> {

    /**
     * Erzeugt eine Instanz mit der Referenz.
     */
    NutzerValueRef() {
        super();
    }

    /**
     * Erzeugt eine Instanz mit der Referenz.
     */
    public NutzerValueRef(@NonNull final UUID refId) {
        super(refId);
    }

    @Override
    public NutzerValueRef withRefId(@NonNull final UUID refId) {
        if (Objects.equals(getRefId(), refId)) {
            return this;
        }
        return new NutzerValueRef(refId);
    }

    @Override
    public String writeJson() {
        return new JsonMapper().writeJson(this);
    }

    public static NutzerValueRef parseJson(@NonNull final String json) {
        return new JsonMapper().parseJson(json, NutzerValueRef.class);
    }
}
