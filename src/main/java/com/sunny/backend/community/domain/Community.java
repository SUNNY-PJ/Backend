package com.sunny.backend.community.domain;

import static com.sunny.backend.common.CommonErrorCode.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.ColumnDefault;

import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.common.photo.Photo;
import com.sunny.backend.community.dto.request.CommunityRequest;
import com.sunny.backend.user.domain.Users;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Community {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "community_id")
	private Long id;

	@Column
	@Size(min = 1, max = 35)
	private String title;

	@Column
	private String contents;

	@ColumnDefault("0")
	@Column
	private int viewCnt;

	@Enumerated(EnumType.STRING)
	@NotNull(message = "올바른 카테고리 값을 입력해야합니다.")
	private BoardType boardType;

	@Column
	private LocalDateTime createdAt;

	@Column
	private LocalDateTime modifiedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id")
	private Users users;

	@OneToMany(mappedBy = "community")
	private final List<Photo> photos = new ArrayList<>();

	@OneToMany(mappedBy = "community", orphanRemoval = true)
	private final List<Comment> comments = new ArrayList<>();

	private Community(String title, String contents, BoardType boardType, Users users) {
		this.title = title;
		this.contents = contents;
		this.viewCnt = 0;
		this.boardType = boardType;
		this.createdAt = LocalDateTime.now();
		this.modifiedAt = LocalDateTime.now();
		this.users = users;
	}

	public static Community of(
		String title,
		String contents,
		BoardType boardType,
		Users users) {
		return new Community(title, contents, boardType, users);
	}

	public int getCommentSize() {
		return comments.size();
	}

	public void updateCommunity(CommunityRequest communityRequest) {
		this.title = communityRequest.getTitle();
		this.contents = communityRequest.getContents();
		this.boardType = communityRequest.getType();
		this.modifiedAt = LocalDateTime.now();
	}

	public void increaseView() {
		this.viewCnt += 1;
	}

	public boolean isAuthor(Long userId) {
		return users.getId().equals(userId);
	}

	public boolean hasNotBeenModified() {
		return !this.createdAt.isEqual(this.modifiedAt);
	}

	public void clearPhoto() {
		this.photos.clear();
	}

	public void addPhotos(List<Photo> photos) {
		this.photos.addAll(photos);
	}

	public void validateByUserId(Long tokenUserId) {
		if (!users.getId().equals(tokenUserId)) {
			throw new CustomException(NO_USER_PERMISSION);
		}
	}
}
