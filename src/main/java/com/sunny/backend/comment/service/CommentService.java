package com.sunny.backend.comment.service;

import static com.sunny.backend.comment.domain.Comment.*;
import static com.sunny.backend.comment.dto.response.CommentResponse.*;
import static com.sunny.backend.comment.exception.CommentErrorCode.*;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.comment.dto.request.CommentRequest;
import com.sunny.backend.comment.dto.request.CommentRequestMapper;
import com.sunny.backend.comment.dto.response.CommentResponse;
import com.sunny.backend.comment.repository.CommentRepository;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.community.repository.CommunityRepository;
import com.sunny.backend.notification.domain.CommentNotification;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.notification.dto.request.NotificationPushRequest;
import com.sunny.backend.notification.repository.CommentNotificationRepository;
import com.sunny.backend.notification.repository.NotificationRepository;
import com.sunny.backend.notification.service.NotificationService;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final CommunityRepository communityRepository;
	private final CommentRequestMapper commentRequestMapper;
	private final NotificationService notificationService;
	private final CommentNotificationRepository commentNotificationRepository;
	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;

	public CommentResponse mapCommentToResponse(Comment comment, Users currentUser) {
		CommentResponse commentResponse;
		if (comment.getUsers() != null && comment.getUsers().getId() != null) {

			boolean isPrivate = comment.getIsPrivated();
			boolean commentAuthor = currentUser.getId().equals(comment.getUsers().getId());
			String writer = comment.getUsers().getNickname();

			String content = comment.getContent();
			if (comment.getParent() != null) {
				// Check if parent comment exists
				Users parentUser = comment.getParent().getUsers();
				if (parentUser != null && parentUser.getNickname() != null) {
					content = "@" + parentUser.getNickname() + " " + content;
				}
			}

			if (isPrivate && !(currentUser.getId().equals(comment.getUsers().getId()) ||
				currentUser.getId().equals(comment.getCommunity().getUsers().getId()))) {

				commentResponse = new CommentResponse(
					comment.getId(),
					comment.getUsers().getId(),
					writer,
					"비밀 댓글입니다.",
					comment.getCreatedDate(),
					comment.getUsers().getProfile(),
					comment.getAuthor(),
					commentAuthor,
					false,
					comment.getIsPrivated(),
					false
				);
			} else {
				if (comment.getIsDeleted()) {
					commentResponse = convertCommentToDto(currentUser, comment);
				} else {
					commentResponse = new CommentResponse(
						comment.getId(),
						comment.getUsers().getId(),
						writer,
						content,
						comment.getCreatedDate(),
						comment.getUsers().getProfile(),
						comment.getAuthor(),
						commentAuthor,
						false,
						comment.getIsPrivated(),
						false
					);
				}
			}
		} else {
			commentResponse = leaveCommentToDto(currentUser, comment);
		}
		commentResponse.setChildren(
			comment.getChildren().stream()
				.map(childComment -> mapCommentToResponse(childComment, currentUser))
				.toList()
		);

		return commentResponse;
	}

	@Transactional
	public List<CommentResponse> getCommentList(CustomUserPrincipal customUserPrincipal, Long communityId) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		List<Comment> comments = commentRepository.findAllByCommunity_Id(communityId);
		List<CommentResponse> commentResponses = comments.stream()
			.filter(comment -> comment.getParent() == null)
			.map(comment -> mapCommentToResponse(comment, users))
			.toList();
		return commentResponses;
	}

	@Transactional
	public CommentResponse createComment(CustomUserPrincipal customUserPrincipal, Long communityId,
		CommentRequest commentRequest) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		Community community = communityRepository.getById(communityId);
		Comment comment = commentRequestMapper.toEntity(commentRequest);
		Comment parentComment = null;
		String title;
		String bodyTitle;
		String content = commentRequest.getContent();
		if (commentRequest.getParentId() != null) {
			parentComment = commentRepository.getById(commentRequest.getParentId());
			content = removeUserTag(content, parentComment);
			comment.setContent(content);
			if (parentComment.getParent() != null) {
				throw new CustomException(REPLYING_NOT_ALLOWED);
			}
			comment.setParent(parentComment);
		} else {
			comment.setContent(commentRequest.getContent());
		}
		comment.setCommunity(community);
		comment.setUsers(users);
		boolean isPrivate = commentRequest.getIsPrivated();
		comment.setIsPrivated(isPrivate);
		boolean isAuthor = Objects.equals(users.getId(),
			comment.getCommunity().getUsers().getId());
		comment.setAuthor(isAuthor);
		comment.changeIsDeleted(false);
		commentRepository.save(comment);
		users.addComment(comment);
		boolean commentAuthor = users.getId().equals(comment.getUsers().getId());
		if (!community.getUsers().getId().equals(users.getId())) {
			if (commentRequest.getParentId() == null) {
				title = "[SUNNY] " + users.getNickname();
				bodyTitle = "새로운 댓글이 달렸어요.";
				createCommentNotification(comment.getCommunity().getUsers(), comment, bodyTitle);
				sendNotifications(comment, title, bodyTitle);
			} else if (!comment.getParent().getUsers().getId().equals(users.getId())) {
				title = "[SUNNY] " + users.getNickname();
				bodyTitle = "새로운 답글이 달렸어요";
				createCommentNotification(comment.getParent().getUsers(), comment, bodyTitle);
				replySendNotifications(comment.getParent().getUsers(), comment, title, bodyTitle);
			}
		}
		return new CommentResponse(comment.getId(), comment.getUsers().getId(), comment.getUsers().getNickname(),
			addUserTag(comment), comment.getCreatedDate(), comment.getUsers().getProfile(), comment.getAuthor(),
			commentAuthor, false, comment.getIsPrivated(), false);
	}

	private String removeUserTag(String content, Comment parent) {
		return content.replaceFirst("@" + parent.getUsers().getNickname() + "\\s", "");
	}

	private String addUserTag(Comment comment) {
		if (comment.getParent() != null && comment.getParent().getUsers() != null) {
			String parentNickname = comment.getParent().getUsers().getNickname();
			return "@" + parentNickname + " " + comment.getContent();
		} else {
			return comment.getContent();
		}
	}

	private void createCommentNotification(Users users, Comment comment, String bodyTitle) {
		CommentNotification commentNotification = CommentNotification.builder()
			.users(users)
			.community(comment.getCommunity())
			.comment(comment)
			.parent_id(comment.getParent())
			.title(bodyTitle)
			.build();
		commentNotificationRepository.save(commentNotification);

	}

	private void replySendNotifications(Users users, Comment comment, String title, String bodyTitle) {
		Long postAuthor = users.getId();
		List<Notification> notificationList = notificationRepository.findByUsers_Id(users.getId());
		String body = comment.getContent();
		if (notificationList.size() != 0) {
			NotificationPushRequest notificationPushRequest = new NotificationPushRequest(
				postAuthor,
				bodyTitle,
				body
			);
			notificationService.commentSendNotificationToFriends(title, notificationPushRequest);
		}
	}

	private void sendNotifications(Comment comment, String title, String bodyTitle) {
		Long postAuthor = comment.getCommunity().getUsers().getId();
		List<Notification> notificationList = notificationRepository.findByUsers_Id(postAuthor);
		String body = comment.getContent();
		if (notificationList.size() != 0) {
			NotificationPushRequest notificationPushRequest = new NotificationPushRequest(
				postAuthor,
				bodyTitle,
				body
			);
			notificationService.commentSendNotificationToFriends(title, notificationPushRequest);
		}
	}

	@Transactional
	public CommentResponse deleteComment(CustomUserPrincipal customUserPrincipal, Long commentId) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		Comment comment = commentRepository.getById(commentId);
		validateCommentByUser(users.getId(), comment.getUsers().getId());
		comment.changeIsDeleted(true);
		return convertCommentToDto(users, comment);
	}

	private Comment getDeletableAncestorComment(Comment comment) {
		Comment parent = comment.getParent();
		if (parent != null && parent.getChildren().size() == 1 && parent.getIsDeleted())
			return getDeletableAncestorComment(parent);
		return comment;
	}

	@Transactional
	public CommentResponse updateComment(CustomUserPrincipal customUserPrincipal, Long commentId,
		CommentRequest commentRequest) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		Comment comment = commentRepository.getById(commentId);
		validateCommentByUser(users.getId(), comment.getUsers().getId());
		comment.updateContent(commentRequest.getContent());
		boolean isPrivate = commentRequest.getIsPrivated();
		boolean commentAuthor = users.getId().equals(comment.getUsers().getId());
		comment.setIsPrivated(isPrivate);
		return new CommentResponse(comment.getId(), comment.getUsers().getId(), comment.getUsers().getNickname(),
			comment.getContent(),
			comment.getCreatedDate(), comment.getUsers().getProfile(), comment.getAuthor(), commentAuthor, false,
			comment.getIsPrivated(), false);
	}
}