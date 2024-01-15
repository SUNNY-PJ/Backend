package com.sunny.backend.scrap.repository;

import com.sunny.backend.entity.Community;
import com.sunny.backend.scrap.domain.Scrap;
import com.sunny.backend.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScrapRepository extends JpaRepository<Scrap,Long> {
    List<Scrap> findAllByUsers_Id(Long id);

    Scrap findByUsersAndCommunity(Users user, Community community);
}
