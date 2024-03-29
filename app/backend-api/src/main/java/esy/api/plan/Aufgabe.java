package esy.api.plan;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import esy.api.CardsplusEntity;
import esy.json.JsonJpaEntity;
import esy.json.JsonMapper;
import lombok.Getter;
import lombok.NonNull;

import jakarta.persistence.*;
import java.util.*;

/**
 * Aufgabe in einem Projekt.
 */
@CardsplusEntity
@Entity
@Table(name = "aufgabe", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"})
})
public final class Aufgabe extends JsonJpaEntity<Aufgabe> {

    /**
     * Beschreibung der Aufgabe.
     */
    @Column(name = "text")
    @Getter
    @JsonProperty
    private String text;

    /**
     * Aufgabe ist aktiv bzw. in Arbeit.
     */
    @Column(name = "aktiv")
    @Getter
    @JsonProperty
    private boolean aktiv;

    /**
     * Aufgabe ist immer einem Projekt zugeordnet.
     */
    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JoinColumn(name = "projekt_id", referencedColumnName = "id")
    @Getter
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Projekt projekt;

    Aufgabe() {
        super();
        this.text = "";
        this.aktiv = true;
        this.projekt = null;
    }

    Aufgabe(@NonNull final Long version, @NonNull final UUID id) {
        super(version, id);
        this.text = "";
        this.aktiv = true;
        this.projekt = null;
    }

    @Override
    public String toString() {
        return super.toString() + ",text='" + text + "'";
    }

    @Override
    public boolean isEqual(final Aufgabe that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        return this.text.equals(that.text) &&
                this.aktiv == that.aktiv &&
                Objects.equals(this.projekt, that.projekt);
    }

    @Override
    public Aufgabe verify() {
        if (text.isBlank()) {
            throw new IllegalArgumentException("text is blank");
        }
        return this;
    }

    @Override
    public Aufgabe withId(@NonNull final UUID id) {
        if (Objects.equals(getId(), id)) {
            return this;
        }
        final Aufgabe value = new Aufgabe(getVersion(), id);
        value.text = this.text;
        value.aktiv = this.aktiv;
        value.projekt = this.projekt;
        return value;
    }

    @JsonAnyGetter
    private Map<String, Object> extraJson() {
        final Map<String, Object> allExtra = new HashMap<>();
        allExtra.put("version", getVersion());
        allExtra.put("projektItem", ProjektItem.fromValue(projekt));
        return allExtra;
    }

    @JsonIgnore
    public Aufgabe setProjekt(final Projekt projekt) {
        this.projekt = projekt;
        return this;
    }

    @Override
    public String writeJson() {
        return new JsonMapper().writeJson(this);
    }

    public static Aufgabe parseJson(@NonNull final String json) {
        return new JsonMapper().parseJson(json, Aufgabe.class);
    }
}
