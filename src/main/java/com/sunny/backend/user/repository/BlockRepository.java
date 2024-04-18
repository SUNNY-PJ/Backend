package com.sunny.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunny.backend.user.domain.Block;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
}
