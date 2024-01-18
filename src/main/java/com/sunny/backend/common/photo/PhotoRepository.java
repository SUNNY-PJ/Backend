package com.sunny.backend.common.photo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo,Long> {
    List<Photo> findByCommunityId(Long communityId);


    Optional<Photo> deleteByCommunityId(Long communityId);

}
