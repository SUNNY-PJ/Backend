package com.sunny.backend.repository.friends;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunny.backend.entity.friends.Friends;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long>, FriendsRepositoryCustom {
	Optional<Friends> findByFriendsSnAndUsers_Id(Long friendsSn, Long userId);
	Optional<Friends> findByUsers_IdAndFriendsSn(Long userId, Long friendsSn);
}
