package com.sunny.backend.entity;


import com.sunny.backend.user.Users;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicInsert //동적 삽입
public class Comment extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //댓글 id

    @Column(name="text", nullable = false)
    private String content; //댓글 내용 , 글자 수 제한 없는지? , notnull 설정

    @ColumnDefault("FALSE")
    @Column(nullable = false)
    private Boolean isDeleted; //댓글 삭제 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="community_id")
    private Community community; //게시글 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id") //유저 아아디
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent; //부모 댓글 (null이면 최상위 댓글)

    @OneToMany(mappedBy = "parent", orphanRemoval = true) // 부모 댓글 삭제 시 하위 댓글 모두 삭제
    private List<Comment> children = new ArrayList<>(); //자식 댓글


    //댓글 좋아요 기능?

    public Comment(String content) {
        this.content = content;
    }

    public void updateWriter(Users users) {
        this.users = users;
    }

    public void updateCommunity(Community community) {
        this.community = community;
    }

    public void updateParent(Comment comment) {
        this.parent = comment;
    }

    public void changeIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setContent(String content) {
        this.content=content;
    }
}
