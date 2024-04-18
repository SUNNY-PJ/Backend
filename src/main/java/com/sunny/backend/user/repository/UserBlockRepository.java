package com.sunny.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunny.backend.user.domain.UsersBlock;
import com.sunny.backend.user.domain.Users;

@Repository
public interface UserBlockRepository extends JpaRepository<UsersBlock, Long> {
	void deleteByUsersAndBlockedUser(Users users, Users blockUsers);
}
