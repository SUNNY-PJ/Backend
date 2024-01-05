package com.sunny.backend.friends.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunny.backend.friends.domain.FriendStatus;
import com.sunny.backend.friends.domain.Friends;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long>, FriendsRepositoryCustom {
	Optional<Friends> findByUsers_IdAndFriend_Id(Long userId, Long friendsSn);

	List<Friends> findByUsers_IdAndStatus(Long userId, FriendStatus friendStatus);

	default Friends getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new IllegalArgumentException("친구가 존재하지 않습니다."));
	}
}
