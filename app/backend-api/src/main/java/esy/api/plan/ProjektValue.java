package esy.api.plan;

import com.fasterxml.jackson.annotation.JsonProperty;
import esy.api.team.NutzerValue;
import esy.api.team.Sprache;
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
     * Projektsprache.
     */
    @Column(name = "sprache")
    @Enumerated(EnumType.ORDINAL)
    @Getter
    @JsonProperty
    private Sprache sprache;

    /**
     * Projektbesitzer.
     */
    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = true)
    @JoinColumn(
            name = "besitzer_id",
            referencedColumnName = "id")
    @Getter
    @JsonProperty
    private NutzerValue besitzer;

    /**
     * Projektmitglieder.
     */
    @ManyToMany(
            fetch = FetchType.LAZY)
    @JoinTable(
            name = "projekt_mitglied",
            joinColumns = @JoinColumn(
                    name = "projekt_id",
                    referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "nutzer_id",
                    referencedColumnName = "id"))
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

    ProjektValue() {
        super();
        this.name = "";
        this.aktiv = true;
        this.sprache = Sprache.DE;
        this.besitzer = null;
        this.allMitglied = new LinkedHashSet<>();
        this.allAufgabe = new LinkedHashSet<>();
    }

    ProjektValue(@NonNull final Long version, @NonNull final UUID id) {
        super(version, id);
        this.name = "";
        this.aktiv = true;
        this.sprache = Sprache.DE;
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
                this.sprache.equals(that.sprache) &&
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
    public ProjektValue withId(@NonNull final UUID id) {
        if (Objects.equals(getId(), id)) {
            return this;
        }
        final ProjektValue value = new ProjektValue(getVersion(), id);
        value.name = this.name;
        value.aktiv = this.aktiv;
        value.sprache = this.sprache;
        value.besitzer = this.besitzer;
        value.allMitglied = this.allMitglied;
        value.allAufgabe = this.allAufgabe;
        return value;
    }

    public ProjektValue setName(@NonNull final String name) {
        this.name = name;
        return this;
    }

    // is never null
    public ProjektValue setAktiv(final boolean aktiv) {
        this.aktiv = aktiv;
        return this;
    }

    public ProjektValue setSprache(@NonNull final Sprache sprache) {
        this.sprache = sprache;
        return this;
    }

    // can be null
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
