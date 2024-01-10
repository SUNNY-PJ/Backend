package com.sunny.backend.entity;



import static com.sunny.backend.common.CommonErrorCode.*;

import com.sunny.backend.common.CommonCustomException;
import com.sunny.backend.dto.request.community.CommunityRequest;
import com.sunny.backend.user.Users;
import javax.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

import java.time.LocalDateTime;
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
    @NotBlank(message = "제목은 필수 입력값입니다.")
    private String title; //제목

    @Column
    @NotBlank(message = "내용은 필수 입력값입니다.")
    private String contents; //내용
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

    @OneToMany(mappedBy = "community", cascade = CascadeType.REMOVE)
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


    public void updateModifiedAt(LocalDateTime updatedAt) {
        this.setUpdatedDate(updatedAt);
    }
    public void addPhoto(List<Photo> photoList) {
        this.photoList=photoList;
    }

    public static void validateCommunityByUser(Long userId, Long tokenUserId) {
        if(!userId.equals(tokenUserId)) {
            throw new CommonCustomException(NO_USER_PERMISSION);
        }
    }
}
