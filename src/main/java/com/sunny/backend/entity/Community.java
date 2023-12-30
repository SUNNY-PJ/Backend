package com.sunny.backend.entity;



import com.sunny.backend.dto.request.community.CommunityRequest;
import com.sunny.backend.user.Users;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Community extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_id")
    private Long id;

    @Column
    private String title; //제목

    @Column
    private String contents; //내용

    @Column
    private String writer; //작성자

    //기본값 0으로 세팅
    @ColumnDefault("0")
    @Column
    private int view_cnt; //조회수

    @Column
    private String createdAt;
    @Column
    private String modifiedAt;


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
    @Builder.Default
    private List<Comment> commentList=new ArrayList<>();

    public void updateCommunity(CommunityRequest communityRequest){
        this.title=communityRequest.getTitle();
        this.contents=communityRequest.getContents();
        this.boardType=communityRequest.getType();

    }

    public void increaseView() {
        this.view_cnt+=1;
    }


    public void updateView() {
        this.view_cnt++;
    }


    public void updateCreatedAt(String createdAt) {
        this.createdAt=createdAt;
    }
    public void addPhoto(List<Photo> photoList) {
        this.photoList=photoList;
    }

}
