package esy.api.plan;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import esy.json.JsonJpaValueBase;
import esy.json.JsonMapper;
import lombok.Getter;
import lombok.NonNull;

import javax.persistence.*;
import java.util.*;

/**
 * Value-Objekt für eine Aufgabe.
 */
@Entity
@Table(name = "aufgabe", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"})
})
public final class AufgabeValue extends JsonJpaValueBase<AufgabeValue> {

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
    private ProjektValue projekt;

    /**
     * Erzeugt eine Instanz mit Standardwerten. Die
     * Instanz ist nicht gültig, d.h. der Aufruf von
     * {@link #verify()} ist nicht erfolgreich.
     */
    AufgabeValue() {
        super();
        this.text = "";
        this.aktiv = true;
        this.projekt = null;
    }

    /**
     * Erzeugt eine Instanz mit Standardwerten. Die
     * Instanz ist nicht gültig, d.h. der Aufruf von
     * {@link #verify()} ist nicht erfolgreich.
     */
    AufgabeValue(@NonNull final Long version, @NonNull final UUID id) {
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
    public boolean isEqual(final AufgabeValue that) {
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
    public AufgabeValue verify() {
        if (text.isBlank()) {
            throw new IllegalArgumentException("text is blank");
        }
        return this;
    }

    @Override
    public AufgabeValue withId(@NonNull final UUID id) {
        if (Objects.equals(getId(), id)) {
            return this;
        }
        final AufgabeValue value = new AufgabeValue(getVersion(), id);
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

    public AufgabeValue setText(@NonNull final String text) {
        this.text = text;
        return this;
    }

    public AufgabeValue setAktiv(final boolean aktiv) {
        this.aktiv = aktiv;
        return this;
    }

    public AufgabeValue setProjekt(final ProjektValue projekt) {
        this.projekt = projekt;
        return this;
    }

    @Override
    public String writeJson() {
        return new JsonMapper().writeJson(this);
    }

    public static AufgabeValue parseJson(@NonNull final String json) {
        return new JsonMapper().parseJson(json, AufgabeValue.class);
    }
}
