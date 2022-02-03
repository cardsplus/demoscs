package esy.api.team;

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
public class NutzerItem implements JsonJpaItem<UUID> {

    /**
     * Eindeutige UUID des Nutzers.
     */
    @Transient
    @Getter
    @JsonProperty
    private final UUID value;

    /**
     * Vollständige E-Mail-Adresse des Nutzers.
     */
    @Column(name = "text")
    @Getter
    @JsonProperty
    private final String text;

    NutzerItem() {
        this.value = null;
        this.text = "";
    }

    NutzerItem(@NonNull final Nutzer value) {
        this.value = value.getId();
        this.text = value.getName() + " <" + value.getMail() + ">";
    }

    @Override
    public String toString() {
        return text;
    }

    public static NutzerItem fromValue(final Nutzer value) {
        if (value != null) {
            return new NutzerItem(value);
        } else {
            return new NutzerItem();
        }
    }
}
