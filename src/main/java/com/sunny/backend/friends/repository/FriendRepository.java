package com.sunny.backend.friends.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunny.backend.friends.domain.Friend;
import com.sunny.backend.friends.domain.Status;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long>, FriendCustomRepository {
	Optional<Friend> findByUsers_IdAndUserFriend_Id(Long userId, Long userFriendId);

	List<Friend> findByUsers_Id(Long userId);
	void deleteByUsersId(Long userId);

	default Friend getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new IllegalArgumentException("친구가 존재하지 않습니다."));
	}
}
