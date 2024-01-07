package com.sunny.backend.repository.consumption;

import com.sunny.backend.entity.Consumption;
import com.sunny.backend.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ConsumptionRepository  extends JpaRepository<Consumption,Long>, ConsumptionCustomRepository {
    List<Consumption> findByUsersId(Long userId);
    List<Consumption> findByUsersIdAndDateField (Long userId, LocalDate datefield);

    default Consumption getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new IllegalArgumentException("지출 기록이 존재하지 않습니다."));
    }
}
