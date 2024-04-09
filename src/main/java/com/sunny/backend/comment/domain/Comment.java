package com.sunny.backend.comment.domain;

import static com.sunny.backend.common.CommonErrorCode.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import com.sunny.backend.common.BaseTime;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.user.domain.Users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicInsert
public class Comment extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "댓글 내용은 필수 입력값입니다.")
	@Column(name = "text", nullable = false)
	private String content;

	@ColumnDefault("FALSE")
	@Column(nullable = false)
	private Boolean isDeleted;

	@ColumnDefault("FALSE")
	@Column(nullable = false)
	private Boolean isPrivated;

	@ColumnDefault("FALSE")
	@Column(nullable = false)
	private Boolean Author;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "community_id")
	private Community community;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id")
	private Users users;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "parent_id")
	private Comment parent;

	@OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.REMOVE)
	private List<Comment> children = new ArrayList<>();

	public Comment(String content) {
		this.content = content;
	}

	public void changeIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void updateContent(String content) {
		this.content = content;
	}

	public static void validateCommentByUser(Long userId, Long commentId) {
		if (!userId.equals(commentId)) {
			throw new CustomException(NO_USER_PERMISSION);
		}
	}
}
