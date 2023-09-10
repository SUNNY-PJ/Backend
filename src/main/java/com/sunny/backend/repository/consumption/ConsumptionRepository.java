package com.sunny.backend.repository.consumption;

import com.sunny.backend.entity.Consumption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ConsumptionRepository  extends JpaRepository<Consumption,Long>, ConsumptionCustomRepository {
    List<Consumption> findByUsersId(Long userId);
}
