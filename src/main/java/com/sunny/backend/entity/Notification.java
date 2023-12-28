package com.sunny.backend.entity;

import com.sunny.backend.user.Users;
import lombok.*;
import org.checkerframework.checker.units.qual.N;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String DeviceToken; //사용자 디바이스 토큰

    @Column
    private Long userId;

}
