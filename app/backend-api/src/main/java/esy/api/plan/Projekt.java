package esy.api.plan;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import esy.api.CardsplusEntity;
import esy.api.team.Nutzer;
import esy.api.team.NutzerItem;
import esy.json.JsonJpaEntity;
import esy.json.JsonMapper;
import lombok.Getter;
import lombok.NonNull;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Projekt mit Aufgaben.
 */
@CardsplusEntity
@Entity
@Table(name = "projekt", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"}),
        @UniqueConstraint(columnNames = {"name"})
})
public final class Projekt extends JsonJpaEntity<Projekt> {

    /**
     * Eindeutiger Name des Projekts.
     */
    @Column(name = "name")
    @Getter
    @JsonProperty
    private String name;

    /**
     * Projekt ist aktiv?
     */
    @Column(name = "aktiv")
    @Getter
    @JsonProperty
    private boolean aktiv;

    /**
     * Projektsprache.
     */
    @Column(name = "sprache")
    @Getter
    @JsonProperty
    private String sprache;

    /**
     * Projektbesitzer.
     */
    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = true)
    @JoinColumn(name = "besitzer_id", referencedColumnName = "id")
    @Getter
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Nutzer besitzer;

    /**
     * Projektmitglieder.
     */
    @ManyToMany(
            fetch = FetchType.EAGER)
    @JoinTable(
            name = "projekt_mitglied",
            joinColumns = @JoinColumn(name = "projekt_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "nutzer_id", referencedColumnName = "id"))
    @Getter
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Nutzer> allMitglied;

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
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Aufgabe> allAufgabe;

    Projekt() {
        super();
        this.name = "";
        this.aktiv = true;
        this.sprache = "";
        this.besitzer = null;
        this.allMitglied = new LinkedHashSet<>();
        this.allAufgabe = new LinkedHashSet<>();
    }

    Projekt(@NonNull final Long version, @NonNull final UUID id) {
        super(version, id);
        this.name = "";
        this.aktiv = true;
        this.sprache = "";
        this.besitzer = null;
        this.allMitglied = new LinkedHashSet<>();
        this.allAufgabe = new LinkedHashSet<>();
    }

    @Override
    public String toString() {
        return super.toString() + ",name='" + name;
    }

    @Override
    public boolean isEqual(final Projekt that) {
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
    public Projekt verify() {
        if (name.isBlank()) {
            throw new IllegalArgumentException("name is blank");
        }
        return this;
    }

    @Override
    public Projekt withId(@NonNull final UUID id) {
        if (Objects.equals(getId(), id)) {
            return this;
        }
        final Projekt value = new Projekt(getVersion(), id);
        value.name = this.name;
        value.aktiv = this.aktiv;
        value.sprache = this.sprache;
        value.besitzer = this.besitzer;
        value.allMitglied = this.allMitglied;
        value.allAufgabe = this.allAufgabe;
        return value;
    }

    @JsonAnyGetter
    private Map<String, Object> extraJson() {
        final Map<String, Object> allExtra = new HashMap<>();
        allExtra.put("version", getVersion());
        allExtra.put("besitzerItem", NutzerItem.fromValue(besitzer));
        allExtra.put("allMitgliedItem", allMitglied.stream()
                .map(NutzerItem::fromValue)
                .collect(Collectors.toSet()));
        return allExtra;
    }

    @JsonIgnore
    public Projekt setBesitzer(final Nutzer besitzer) {
        this.besitzer = besitzer;
        return this;
    }

    @JsonIgnore
    public Projekt addMitglied(@NonNull final Nutzer mitglied) {
        allMitglied.add(mitglied);
        return this;
    }

    @JsonIgnore
    public Projekt addAufgabe(@NonNull final Aufgabe aufgabe) {
        allAufgabe.add(aufgabe);
        return this;
    }

    @Override
    public String writeJson() {
        return new JsonMapper().writeJson(this);
    }

    public static Projekt parseJson(@NonNull final String json) {
        return new JsonMapper().parseJson(json, Projekt.class);
    }
}
