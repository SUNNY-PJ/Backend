package com.sunny.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunny.backend.entity.friends.ApproveType;
import com.sunny.backend.entity.friends.Friends;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long> {
	List<Friends> findByUsers_IdAndApproveNot(Long userIdm, ApproveType approve);
	Optional<Friends> findByFriendsSnAndUsers_Id(Long friendsSn, Long userId);
	Friends findByUsers_IdAndFriends_Id(Long userId, Long friendsId);
}
