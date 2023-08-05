package com.sunny.backend.repository.consumption;

import com.sunny.backend.dto.response.CommentResponse;
import com.sunny.backend.dto.response.ConsumptionResponse;
import com.sunny.backend.entity.Comment;
import com.sunny.backend.entity.Consumption;

import java.util.List;
import java.util.Optional;

public interface ConsumptionCustomRepository {

    List<ConsumptionResponse> findByUsersId(Long id);
}
