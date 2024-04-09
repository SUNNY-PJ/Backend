package com.sunny.backend.consumption.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunny.backend.consumption.domain.Consumption;
import com.sunny.backend.consumption.domain.SpendType;

public interface ConsumptionRepository extends JpaRepository<Consumption, Long>,
	ConsumptionCustomRepository {
	List<Consumption> findByUsersId(Long userId);

	List<Consumption> findByUsersIdAndDateField(Long userId, LocalDate datefield);

	List<Consumption> findByUsersIdAndCategory(Long userId, SpendType spendType);

	default Consumption getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new IllegalArgumentException("지출 기록이 존재하지 않습니다."));
	}
}
