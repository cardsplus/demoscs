package esy.api.team;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import esy.json.JsonJpaEntity;
import esy.json.JsonMapper;
import lombok.Getter;
import lombok.NonNull;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "nutzer", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"}),
        @UniqueConstraint(columnNames = {"mail"})
})
public final class Nutzer extends JsonJpaEntity<Nutzer> {

    /**
     * Eindeutige E-Mail-Adresse des Nutzers.
     */
    @Column(name = "mail")
    @Getter
    @JsonProperty
    private String mail;

    /**
     * Name des Nutzers.
     */
    @Column(name = "name")
    @Getter
    @JsonProperty
    private String name;

    /**
     * Nutzer ist aktiv?
     */
    @Column(name = "aktiv")
    @Getter
    @JsonProperty
    private boolean aktiv;

    /**
     * Sprachen, die der Nutzer beherrscht.
     */
    @ElementCollection(
            fetch = FetchType.EAGER)
    @CollectionTable(
            name = "nutzer_sprache",
            joinColumns = @JoinColumn(name = "id"))
    @Column(name = "sprache")
    @OrderBy
    @Getter
    @JsonProperty
    private SortedSet<String> allSprache;

    Nutzer() {
        super();
        this.mail = "";
        this.name = "";
        this.aktiv = true;
        this.allSprache = new TreeSet<>();
    }

    Nutzer(@NonNull final Long version, @NonNull final UUID id) {
        super(version, id);
        this.mail = "";
        this.name = "";
        this.aktiv = true;
        this.allSprache = new TreeSet<>();
    }

    @Override
    public String toString() {
        return super.toString() + ",mail='" + mail + "'";
    }

    @Override
    public boolean isEqual(final Nutzer that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        return this.mail.equals(that.mail) &&
                this.name.equals(that.name) &&
                this.aktiv == that.aktiv &&
                this.allSprache.equals(that.allSprache);
    }

    @Override
    public Nutzer verify() {
        if (mail.isBlank()) {
            throw new IllegalArgumentException("mail is blank");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("name is blank");
        }
        return this;
    }

    @Override
    public Nutzer withId(@NonNull final UUID id) {
        if (Objects.equals(getId(), id)) {
            return this;
        }
        final Nutzer value = new Nutzer(getVersion(), id);
        value.mail = this.mail;
        value.name = this.name;
        value.aktiv = this.aktiv;
        value.allSprache = this.allSprache;
        return value;
    }

    @JsonAnyGetter
    private Map<String, Object> extraJson() {
        final Map<String, Object> allExtra = new HashMap<>();
        allExtra.put("version", getVersion());
        return allExtra;
    }

    @Override
    public String writeJson() {
        return new JsonMapper().writeJson(this);
    }

    public static Nutzer parseJson(@NonNull final String json) {
        return new JsonMapper().parseJson(json, Nutzer.class);
    }
}
