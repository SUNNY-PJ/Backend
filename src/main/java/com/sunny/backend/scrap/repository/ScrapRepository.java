package com.sunny.backend.scrap.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunny.backend.community.domain.Community;
import com.sunny.backend.scrap.domain.Scrap;
import com.sunny.backend.user.domain.Users;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
	List<Scrap> findAllByUsers_Id(Long id);

	Optional<Scrap> findByUsersAndCommunity(Users user, Community community);
}
