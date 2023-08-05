package com.sunny.backend.entity;

import lombok.*;

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

//    @Id
//    @GeneratedValue
//    @Column(name = "photo_id", columnDefinition = "BINARY(16)") //UUID로 설정하는 것이 좋을까?
//    private UUID id;


    //community와 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY) //default 값이 EAGER (즉시로딩)이므로 LAZY(지연로딩)으로 설정
    @JoinColumn(name= "community_id")
    private Community community;

    @Column
    private String filename;
    @Column
    private String savedFileName;

    @Column
    private Long fileSize;
    public Photo(String filename,Community community){
        this.filename=filename;
        this.community=community;
    }

}
