package com.sunny.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunny.backend.entity.Friends;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long> {
	List<Friends> findByUsers_IdAndApprove(Long userIdm, Character approve);
	Friends findByUsers_IdAndFriends_Id(Long userId, Long friendsId);
}
