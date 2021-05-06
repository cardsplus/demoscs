package esy.api.plan;

import com.fasterxml.jackson.annotation.JsonProperty;
import esy.json.JsonJpaValueBase;
import esy.json.JsonMapper;
import lombok.Getter;
import lombok.NonNull;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

/**
 * Value-Objekt für eine Aufgabe.
 */
@Entity
@Table(name = "aufgabe")
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
    @JoinColumn(
            name = "projekt_id",
            referencedColumnName = "id")
    @Getter
    @JsonProperty
    private ProjektValue projekt;

    AufgabeValue() {
        super();
        this.text = "";
        this.aktiv = true;
        this.projekt = null;
    }

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

    public AufgabeValue setText(@NonNull final String text) {
        this.text = text;
        return this;
    }

    // is never null
    public AufgabeValue setAktiv(final boolean aktiv) {
        this.aktiv = aktiv;
        return this;
    }

    // can be null
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
