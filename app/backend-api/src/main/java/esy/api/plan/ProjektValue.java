package esy.api.plan;

import com.fasterxml.jackson.annotation.JsonProperty;
import esy.api.nutzer.NutzerValue;
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
    @Column(name = "name")
    @Getter
    @JsonProperty
    private String name;

    /**
     * Projekt ist aktiv.
     */
    @Column(name = "aktiv")
    @Getter
    @JsonProperty
    private boolean aktiv;

    /**
     * Projektbesitzer.
     */
    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = true)
    @JoinColumn(name = "besitzer_id", referencedColumnName = "id")
    @Getter
    @JsonProperty
    private NutzerValue besitzer;

    /**
     * Projektmitglieder.
     */
    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable(
            name = "projekt_mitglied",
            joinColumns = @JoinColumn(name = "projekt_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "nutzer_id", referencedColumnName = "id"))
    @Getter
    @JsonProperty
    private Set<NutzerValue> allMitglied;

    /**
     * Projektbezogene Aufgaben.
     */
    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "projekt"
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
        value.besitzer = this.besitzer;
        value.allMitglied = this.allMitglied;
        value.allAufgabe = this.allAufgabe;
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

    public ProjektValue setBesitzer(final NutzerValue besitzer) {
        this.besitzer = besitzer;
        return this;
    }

    public ProjektValue addMitglied(@NonNull final NutzerValue mitglied) {
        allMitglied.add(mitglied);
        return this;
    }

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
