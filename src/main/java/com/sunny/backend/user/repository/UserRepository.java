package com.sunny.backend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunny.backend.user.domain.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
	Optional<Users> findByEmail(String email);

	Optional<Users> findByNickname(String name);

	default Users getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
	}

	default Users getByEmail(String email) {
		return findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
	}

}
