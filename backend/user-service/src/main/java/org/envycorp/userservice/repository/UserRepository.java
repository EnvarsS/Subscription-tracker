package org.envycorp.userservice.repository;

import org.envycorp.userservice.model.DTO.UserResponseDTO;
import org.envycorp.userservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User,UUID> {
}
