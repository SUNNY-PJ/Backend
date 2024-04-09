package com.sunny.backend.common.photo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.sunny.backend.community.domain.Community;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Photo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	//community와 다대일 관계
	@ManyToOne(fetch = FetchType.LAZY) //default 값이 EAGER (즉시로딩)이므로 LAZY(지연로딩)으로 설정
	@JoinColumn(name = "community_id")
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
