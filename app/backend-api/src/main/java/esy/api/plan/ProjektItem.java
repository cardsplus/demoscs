package esy.api.plan;

import com.fasterxml.jackson.annotation.JsonProperty;
import esy.json.JsonJpaItem;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.util.UUID;

@Embeddable
@EqualsAndHashCode
public class ProjektItem implements JsonJpaItem<UUID> {

    /**
     * Eindeutige UUID des Projekts.
     */
    @Transient
    @Getter
    @JsonProperty
    private final UUID value;

    /**
     * Eindeutiger Name des Projekts.
     */
    @Column(name = "text")
    @Getter
    @JsonProperty
    private final String text;

    ProjektItem() {
        this.value = null;
        this.text = "";
    }

    ProjektItem(@NonNull final Projekt value) {
        this.value = value.getId();
        this.text = value.getName();
    }

    @Override
    public String toString() {
        return text;
    }

    public static ProjektItem fromValue(final Projekt value) {
        if (value != null) {
            return new ProjektItem(value);
        } else {
            return new ProjektItem();
        }
    }
}
