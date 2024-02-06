package com.sunny.backend.community.domain;


import static com.sunny.backend.common.CommonErrorCode.*;

import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.community.dto.request.CommunityRequest;
import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.common.photo.Photo;
import com.sunny.backend.user.domain.Users;
import javax.validation.constraints.NotNull;
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
public class Community {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_id")
    private Long id;
    @Column
    private String title;
    @Column
    private String contents;
    @ColumnDefault("0")
    @Column
    private int viewCnt;
    @Column
    private LocalDateTime createdAt;
    @Column
    private LocalDateTime modifiedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "users_id")
    private Users users;
    @OneToMany(mappedBy = "community")
    @Builder.Default
    private List<Photo> photoList = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    @NotNull(message = "올바른 카테고리 값을 입력해야합니다.")
    private BoardType boardType;
    @OneToMany(mappedBy = "community",orphanRemoval = true)
    @Builder.Default
    private List<Comment> commentList=new ArrayList<>();

    public int getCommentSize() {
        return commentList.size();
    }

    public void updateCommunity(CommunityRequest communityRequest){
        this.title=communityRequest.getTitle();
        this.contents=communityRequest.getContents();
        this.boardType=communityRequest.getType();
    }

    public void increaseView() {
        this.viewCnt+=1;
    }

    public void updateView() {
        this.viewCnt++;
    }

    public boolean hasNotBeenModified(LocalDateTime createdAt,LocalDateTime modifiedAt ) {
        return !createdAt.isEqual(modifiedAt);
    }
    public void updateModifiedAt(LocalDateTime modifiedAt ) {
        this.modifiedAt=modifiedAt;
    }

    public void addPhoto(List<Photo> photoList) {
        this.photoList=photoList;
    }

    public static void validateCommunityByUser(Long userId, Long tokenUserId) {
        if(!userId.equals(tokenUserId)) {
            throw new CustomException(NO_USER_PERMISSION);
        }
    }
}
