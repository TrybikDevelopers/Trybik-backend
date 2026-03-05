package org.pkwmtt.moderator.repositories;

import org.pkwmtt.moderator.entities.Moderator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ModeratorRepository extends JpaRepository<Moderator, UUID> {
}

