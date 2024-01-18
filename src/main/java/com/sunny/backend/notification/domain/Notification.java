package com.sunny.backend.notification.domain;

import com.sunny.backend.user.Users;
import lombok.*;

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
    private String DeviceToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "users_id")
    private Users users;

}
