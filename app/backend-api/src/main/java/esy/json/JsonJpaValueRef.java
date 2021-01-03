package esy.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
public abstract class JsonJpaValueRef<SELF extends JsonJpaValueRef<?>> implements JsonWithRefId<SELF> {

    /**
     * Eindeutige ID der Daten.
     */
    @Column(name = "ref_id", nullable = false)
    @Getter
    @JsonProperty
    private final UUID refId;

    protected JsonJpaValueRef() {
         this.refId = null;
    }

    protected JsonJpaValueRef(@NonNull final UUID refId) {
        this.refId = refId;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.refId);
    }

    @Override
    public final boolean equals(final Object any) {
        if (this == any) {
            return true;
        }
        if (any == null) {
            return false;
        }
        if (!getClass().equals(any.getClass())) {
            return false;
        }
        final SELF that = (SELF) any;
        return Objects.equals(this.refId, that.getRefId());
    }

    /**
     * Erzeugt einen lesbaren Text für dieses Object mit einer
     * komma-separierten Liste aller Attributen dieses Objekts.
     *
     * @return Komma-separierte Liste
     */
    @Override
    public final String toString() {
        return "id:" + refId;
    }

    /**
     * Erzeugt einen lesbaren Text für dieses Object mit einer
     * JSON-Struktur aller Attribute dieser Instanz.
     *
     * @return JSON-Struktur
     */
    public abstract String writeJson();
}
