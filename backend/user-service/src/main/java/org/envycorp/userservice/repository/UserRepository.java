package org.envycorp.userservice.repository;

import org.envycorp.userservice.model.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserProfile,Long> {
}
