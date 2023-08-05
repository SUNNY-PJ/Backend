package com.sunny.backend.repository.consumption;

import com.sunny.backend.entity.Community;
import com.sunny.backend.entity.Consumption;
import com.sunny.backend.repository.community.CommunityCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumptionRepository  extends JpaRepository<Consumption,Long>,ConsumptionCustomRepository {
}
