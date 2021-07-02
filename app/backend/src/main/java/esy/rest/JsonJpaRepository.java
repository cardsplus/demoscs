package esy.rest;

import esy.json.JsonJpaValueBase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JsonJpaRepository<V extends JsonJpaValueBase<V>> extends JpaRepository<V, UUID> {
}
