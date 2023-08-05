package com.sunny.backend.entity;

import com.sunny.backend.user.Users;
import jdk.jfr.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class Consumption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column
//    @NotNull
//    private String place; //장소 (의,식,주 카테고리)
    @Enumerated(EnumType.STRING)
    private Cons_Category category;

    @Column
    @NotNull
    private String name; //지출명

    @Column
    @NotNull
    private Long money; //지출 금액

    @Column
    @NotNull
    private Date dateField; //string으로 등록?

    //users 다대일 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "users_id")
    private Users users;

}

// 지출 통계 : query 값으로 카테고리 조회 후, 카테고리별로 모두 더한 뒤 dateField 개수로 나눔? -> 통계