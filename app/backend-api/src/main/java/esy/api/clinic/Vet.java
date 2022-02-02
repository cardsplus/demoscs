package esy.api.clinic;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import esy.json.JsonJpaValueBase;
import esy.json.JsonMapper;
import lombok.Getter;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "vet", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"}),
        @UniqueConstraint(columnNames = {"name"})
})
public final class Vet extends JsonJpaValueBase<Vet> {

    @Column(name = "name")
    @Getter
    @JsonProperty
    private String name;

    Vet() {
        super();
        this.name = "";
    }

    Vet(@NonNull final Long version, @NonNull final UUID id) {
        super(version, id);
        this.name = "";
    }

    @Override
    public String toString() {
        return super.toString() + ",name='" + name + "'";
    }

    @Override
    public boolean isEqual(final Vet that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        return this.name.equals(that.name);
    }

    @Override
    public Vet verify() {
        // Check if name is valid
        if (name.isBlank()) {
            throw new IllegalArgumentException("name is blank");
        }
        return this;
    }

    @Override
    public Vet withId(@NonNull final UUID id) {
        if (Objects.equals(getId(), id)) {
            return this;
        }
        final Vet value = new Vet(getVersion(), id);
        value.name = this.name;
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

    public static Vet parseJson(@NonNull final String json) {
        return new JsonMapper().parseJson(json, Vet.class);
    }
}
