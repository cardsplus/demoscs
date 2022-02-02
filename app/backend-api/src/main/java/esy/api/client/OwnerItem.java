package esy.api.client;

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
public class OwnerItem implements JsonJpaItem<UUID> {

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

    private OwnerItem() {
        this.value = null;
        this.text = "";
    }

    private OwnerItem(@NonNull final Owner value) {
        this.value = value.getId();
        this.text = value.getName();
    }

    @Override
    public String toString() {
        return text;
    }

    public static OwnerItem fromValue(final Owner value) {
        if (value != null) {
            return new OwnerItem(value);
        } else {
            return new OwnerItem();
        }
    }
}
