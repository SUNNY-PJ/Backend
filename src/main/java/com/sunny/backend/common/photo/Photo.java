package com.sunny.backend.common.photo;

import com.sunny.backend.community.domain.Community;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.parameters.P;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //community와 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY) //default 값이 EAGER (즉시로딩)이므로 LAZY(지연로딩)으로 설정
    @JoinColumn(name= "community_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Community community;

    @Column
    private String filename;

    @Column
    private String fileUrl;

    @Column
    private Long fileSize;

    private Photo(Community community, String filename, String fileUrl, Long fileSize) {
        this.community = community;
        this.filename = filename;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
    }

    public static Photo of(Community community, String filename, String fileUrl, Long fileSize) {
        return new Photo(community, filename, fileUrl, fileSize);
    }
}
