package com.sunny.backend.save.repository;

import com.sunny.backend.save.domain.Save;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaveRepository extends JpaRepository<Save, Long> {

  Save findByUsers_Id(Long userId);

  List<Save> findByEndDate(LocalDate localDate);

  default Save getById(Long id) {
    return findById(id)
        .orElseThrow(() -> new IllegalArgumentException("절약 목표가 존재하지 않습니다."));
  }

}
