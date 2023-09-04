package com.sunny.backend.entity;

import com.sunny.backend.user.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Scrap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users users;


    @ManyToOne(fetch = FetchType.LAZY) //default 값이 EAGER (즉시로딩)이므로 LAZY(지연로딩)으로 설정
    @JoinColumn(name= "community_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Community community;



}
