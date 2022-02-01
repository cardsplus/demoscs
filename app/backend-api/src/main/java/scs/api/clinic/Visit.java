package scs.api.clinic;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import esy.json.JsonJpaValueBase;
import esy.json.JsonMapper;
import lombok.Getter;
import lombok.NonNull;
import scs.api.owner.Pet;
import scs.api.owner.PetItem;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "visit", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"})
})
public final class Visit extends JsonJpaValueBase<Visit> {

    @Column(name = "date")
    @Getter
    @JsonProperty
    private LocalDate date;

    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = true)
    @JoinColumn(name = "pet_id", referencedColumnName = "id")
    @Getter
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Pet pet;

    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = true)
    @JoinColumn(name = "vet_id", referencedColumnName = "id")
    @Getter
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Vet vet;

    Visit() {
        super();
        this.date = LocalDate.of(2000, 1, 1);
        this.pet = null;
        this.vet = null;
    }

    Visit(@NonNull final Long version, @NonNull final UUID id) {
        super(version, id);
        this.date = LocalDate.of(2000, 1, 1);
        this.pet = null;
        this.vet = null;
    }

    @Override
    public String toString() {
        return super.toString() + ",date='" + date + "'";
    }

    @Override
    public boolean isEqual(final Visit that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        return this.date.equals(that.date) &&
                Objects.equals(this.pet, that.pet) &&
                Objects.equals(this.vet, that.vet);
    }

    @Override
    public Visit verify() {
        return this;
    }

    @Override
    public Visit withId(@NonNull final UUID id) {
        if (Objects.equals(getId(), id)) {
            return this;
        }
        final Visit value = new Visit(getVersion(), id);
        value.date = this.date;
        value.pet = this.pet;
        value.vet = this.vet;
        return value;
    }

    @JsonAnyGetter
    private Map<String, Object> extraJson() {
        final Map<String, Object> allExtra = new HashMap<>();
        allExtra.put("version", getVersion());
        allExtra.put("petItem", PetItem.fromValue(pet));
        allExtra.put("vetItem", VetItem.fromValue(vet));
        return allExtra;
    }

    @Override
    public String writeJson() {
        return new JsonMapper().writeJson(this);
    }

    public static Visit parseJson(@NonNull final String json) {
        return new JsonMapper().parseJson(json, Visit.class);
    }
}
