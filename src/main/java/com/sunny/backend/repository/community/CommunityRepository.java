package com.sunny.backend.repository.community;

import com.sunny.backend.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community,Long>,CommunityCustomRepository {
}
