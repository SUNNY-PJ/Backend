package com.sunny.backend.entity;

import com.sunny.backend.user.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //경쟁 id

    @Column
    private String message; // 도발 메세지

    @Column
    private String output; // 결과

    @Column
    private LocalDate startDate; // 시작 기간

    @Column
    private LocalDate endDate; // 종료 기간

    @Column
    private Integer price; // 대결 금액

    @Column
    private String compensation; // 대결 보상

    @Column
    private Character approve;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

    @ManyToOne
    @JoinColumn(name = "friends_id")
    private Users friends;

}
