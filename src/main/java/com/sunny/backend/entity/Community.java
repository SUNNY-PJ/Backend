package com.sunny.backend.entity;


import com.sunny.backend.user.Users;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Community extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title; //제목

    @Column
    private String contents; //내용

    @Column
    private String writer; //작성자

    @Column(columnDefinition = "integer default 0", nullable = false) //기본값 0으로 세팅
    private int view_cnt; //조회수


    //users 다대일 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "users_id")
    private Users users;

    //photo 일대다 관계 매핑
    @OneToMany(mappedBy = "community")
    @Builder.Default
    private List<Photo> photoList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @OneToMany(mappedBy = "community")
    private List<Comment> commentList=new ArrayList<>();

    public void writePhoto(Photo photo){
        photoList.add(photo);
        photo.setCommunity(this);

    }

}
