package esy.api.plan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import esy.api.nutzer.NutzerValue;
import esy.api.nutzer.NutzerValueRef;
import esy.json.JsonJpaValueBase;
import esy.json.JsonMapper;
import lombok.Getter;
import lombok.NonNull;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Value-Objekt für ein Projekt.
 */
@Entity
@Table(name = "projekt")
public final class ProjektValue extends JsonJpaValueBase<ProjektValue> {

    /**
     * Eindeutiger Name des Projekts.
     */
    @Column(name = "name", nullable = false)
    @Getter
    @JsonProperty
    private String name;

    /**
     * Projekt ist aktiv.
     */
    @Column(name = "aktiv", nullable = false)
    @Getter
    @JsonProperty
    private boolean aktiv;

    /**
     * Projektbesitzer.
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "refId", column = @Column(name = "besitzer_id"))
    })
    @Getter
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private NutzerValueRef besitzer;

    /**
     * Projektmitglieder.
     */
    @ElementCollection(
            fetch = FetchType.EAGER)
    @CollectionTable(
            name = "projekt_mitglied",
            joinColumns = @JoinColumn(name = "id"))
    @Getter
    @JsonProperty
    private Set<NutzerValueRef> allMitglied;

    /**
     * Projektbezogene Aufgaben.
     */
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "projekt",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Getter
    @JsonProperty
    private Set<AufgabeValue> allAufgabe;

    /**
     * Erzeugt eine Instanz mit Standardwerten. Die
     * Instanz ist nicht gültig, d.h. der Aufruf von
     * {@link #verify()} ist nicht erfolgreich.
     */
    ProjektValue() {
        super();
        this.name = "";
        this.aktiv = true;
        this.besitzer = null;
        this.allMitglied = new LinkedHashSet<>();
        this.allAufgabe = new LinkedHashSet<>();
    }

    /**
     * Erzeugt eine Instanz mit Standardwerten. Die
     * Instanz ist nicht gültig, d.h. der Aufruf von
     * {@link #verify()} ist nicht erfolgreich.
     */
    public ProjektValue(@NonNull final Long version, @NonNull final UUID dataId) {
        super(version, dataId);
        this.name = "";
        this.aktiv = true;
        this.besitzer = null;
        this.allMitglied = new LinkedHashSet<>();
        this.allAufgabe = new LinkedHashSet<>();
    }

    @Override
    public String toString() {
        return super.toString() + ",name='" + name + "'";
    }

    @Override
    public boolean isEqual(final ProjektValue that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        return this.name.equals(that.name) &&
                this.aktiv == that.aktiv &&
                Objects.equals(this.besitzer, that.besitzer) &&
                this.allMitglied.equals(that.allMitglied) &&
                this.allAufgabe.equals(that.allAufgabe);
    }

    @Override
    public ProjektValue verify() {
        if (name.isBlank()) {
            throw new IllegalArgumentException("name is blank");
        }
        return this;
    }

    @Override
    public ProjektValue withDataId(@NonNull final UUID dataId) {
        if (Objects.equals(getDataId(), dataId)) {
            return this;
        }
        final ProjektValue value = new ProjektValue(getVersion(), dataId);
        value.name = this.name;
        value.aktiv = this.aktiv;
        value.allAufgabe = this.allAufgabe;
        value.besitzer = this.besitzer;
        value.allMitglied = this.allMitglied;
        return value;
    }

    public ProjektValue setName(@NonNull final String name) {
        this.name = name;
        return this;
    }

    public ProjektValue setAktiv(final boolean aktiv) {
        this.aktiv = aktiv;
        return this;
    }

    @JsonIgnore
    public ProjektValue setBesitzer(@NonNull final NutzerValue besitzer) {
        this.besitzer = new NutzerValueRef(besitzer.getDataId());
        return this;
    }

    @JsonIgnore
    public ProjektValue addMitglied(@NonNull final NutzerValue mitglied) {
        allMitglied.add(new NutzerValueRef(mitglied.getDataId()));
        return this;
    }

    @JsonIgnore
    public ProjektValue addAufgabe(@NonNull final AufgabeValue aufgabe) {
        allAufgabe.add(aufgabe);
        return this;
    }

    @Override
    public String writeJson() {
        return new JsonMapper().writeJson(this);
    }

    public static ProjektValue parseJson(@NonNull final String json) {
        return new JsonMapper().parseJson(json, ProjektValue.class);
    }
}
