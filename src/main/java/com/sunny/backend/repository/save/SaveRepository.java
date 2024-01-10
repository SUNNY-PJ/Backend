package com.sunny.backend.repository.save;

import com.sunny.backend.entity.Consumption;
import com.sunny.backend.entity.Save;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaveRepository extends JpaRepository<Save, Long> {

  default Save getById(Long id) {
    return findById(id)
        .orElseThrow(() -> new IllegalArgumentException("절약 목표가 존재하지 않습니다."));
  }

}
