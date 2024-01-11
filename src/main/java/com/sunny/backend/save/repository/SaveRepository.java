package com.sunny.backend.save.repository;

import com.sunny.backend.entity.Notification;
import com.sunny.backend.save.domain.Save;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaveRepository extends JpaRepository<Save, Long> {

  Save findByUsers_Id(Long userId);

  default Save getById(Long id) {
    return findById(id)
        .orElseThrow(() -> new IllegalArgumentException("절약 목표가 존재하지 않습니다."));
  }

}
