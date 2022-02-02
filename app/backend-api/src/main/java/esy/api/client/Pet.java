package esy.api.client;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import esy.json.JsonJpaValueBase;
import esy.json.JsonMapper;
import lombok.Getter;
import lombok.NonNull;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "pet", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"}),
        @UniqueConstraint(columnNames = {"name","owner_id"})
})
public final class Pet extends JsonJpaValueBase<Pet> {

    @Column(name = "name")
    @Getter
    @JsonProperty
    private String name;

    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    @Getter
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Owner owner;

    Pet() {
        super();
        this.name = "";
        this.owner = null;
    }

    Pet(@NonNull final Long version, @NonNull final UUID id) {
        super(version, id);
        this.name = "";
        this.owner = null;
    }

    @Override
    public String toString() {
        return super.toString() + ",name='" + name + "'";
    }

    @Override
    public boolean isEqual(final Pet that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        return this.name.equals(that.name) &&
                Objects.equals(this.owner, that.owner);
    }

    @Override
    public Pet verify() {
        // Check if name is valid
        if (name.isBlank()) {
            throw new IllegalArgumentException("name is blank");
        }
        return this;
    }

    @Override
    public Pet withId(@NonNull final UUID id) {
        if (Objects.equals(getId(), id)) {
            return this;
        }
        final Pet value = new Pet(getVersion(), id);
        value.name = this.name;
        value.owner = this.owner;
        return value;
    }

    @JsonAnyGetter
    private Map<String, Object> extraJson() {
        final Map<String, Object> allExtra = new HashMap<>();
        allExtra.put("version", getVersion());
        allExtra.put("ownerItem", OwnerItem.fromValue(owner));
        return allExtra;
    }

    @JsonIgnore
    public Pet setOwner(@NonNull final Owner owner) {
        this.owner = owner;
        return this;
    }

    @Override
    public String writeJson() {
        return new JsonMapper().writeJson(this);
    }

    public static Pet parseJson(@NonNull final String json) {
        return new JsonMapper().parseJson(json, Pet.class);
    }
}
