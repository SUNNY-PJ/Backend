package com.sunny.backend.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel
public class MyPageable {
    @ApiModelProperty(value = "페이지 번호(0..N)")
    private Integer page;

    @ApiModelProperty(value = "페이지 크기")
    private Integer size;

    @ApiModelProperty(value = "정렬(ASC|DESC)")
    private List<String> sort;
}
