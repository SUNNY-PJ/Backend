package com.sunny.backend.entity;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    public Photo(String fileUrl,Community community){
        this.fileUrl=fileUrl;
        this.community=community;
    }

}
