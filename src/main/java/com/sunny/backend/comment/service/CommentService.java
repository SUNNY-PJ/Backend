package com.sunny.backend.comment.service;

import static com.sunny.backend.comment.domain.Comment.*;
import static com.sunny.backend.comment.dto.response.CommentResponse.*;
import static com.sunny.backend.comment.exception.CommentErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.comment.domain.Comment;
import com.sunny.backend.comment.dto.request.CommentRequest;
import com.sunny.backend.comment.dto.request.CommentRequestMapper;
import com.sunny.backend.comment.dto.response.CommentResponse;
import com.sunny.backend.comment.repository.CommentRepository;
import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.community.repository.CommunityRepository;
import com.sunny.backend.notification.domain.CommentNotification;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.notification.dto.request.NotificationPushRequest;
import com.sunny.backend.notification.repository.CommentNotificationRepository;
import com.sunny.backend.notification.repository.NotificationRepository;
import com.sunny.backend.notification.service.NotificationService;
import com.sunny.backend.user.domain.Users;
import com.sunny.backend.user.domain.UsersBlock;
import com.sunny.backend.user.repository.BlockRepository;
import com.sunny.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

	private final CommentRepository commentRepository;
	private final CommunityRepository communityRepository;
	private final CommentRequestMapper commentRequestMapper;
	private final ResponseService responseService;
	private final NotificationService notificationService;
	private final CommentNotificationRepository commentNotificationRepository;
	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	private final BlockRepository blockRepository;

	public CommentResponse mapCommentToResponse(Comment comment, Users currentUser) {
		CommentResponse commentResponse;
		// 사용자가 차단한 유저들을 알기 위한 blockList
		List<UsersBlock> blockList = blockRepository.findAllByUsers_Id(currentUser.getId());
		//사용자가 차단 당한 경우
		List<UsersBlock> userBlockList = blockRepository.findAllByBlockedUser_Id(currentUser.getId());

		if (comment.getUsers() != null && comment.getUsers().getId() != null) {
			//사용자가 차단한 경우
			boolean isCommentBlocked = blockList.stream()
				.anyMatch(block -> block.getBlockedUser().getId().equals(comment.getUsers().getId()));
			//사용자가 차단 당한 경우를 알기 위한 값
			boolean isUserBlocked = userBlockList.stream()
				.anyMatch(block -> block.getUsers().getId().equals(comment.getUsers().getId()));
			boolean isPrivate = comment.getIsPrivated();
			boolean commentAuthor = currentUser.getId().equals(comment.getUsers().getId());
			String writer = comment.getUsers().getNickname();

			String content = comment.getContent();
			if (comment.getParent() != null) {
				Users parentUser = comment.getParent().getUsers();
				if (parentUser != null && parentUser.getNickname() != null) {
					content = "@" + parentUser.getNickname() + " " + content;
				}
			}

			if (isCommentBlocked) {
				log.info(String.valueOf(isCommentBlocked));
				writer = "차단한 사용자";
				content = "해당 사용자가 차단되어 댓글을 볼 수 없습니다.";
				commentResponse = new CommentResponse(
					comment.getId(),
					comment.getUsers().getId(),
					writer,
					content,
					null,
					null,
					comment.getAuthor(),
					commentAuthor,
					false,
					comment.getIsPrivated(),
					false,
					isCommentBlocked
				);
			}
			if (isUserBlocked) {
				writer = "(알 수 없음)";
				content = "댓글을 확인할 수 없습니다.";
				commentResponse = new CommentResponse(
					comment.getId(),
					comment.getUsers().getId(),
					writer,
					content,
					null,
					null,
					comment.getAuthor(),
					commentAuthor,
					false,
					comment.getIsPrivated(),
					false,
					isUserBlocked
				);
			} else if (isPrivate && !(currentUser.getId().equals(comment.getUsers().getId()) ||
				currentUser.getId().equals(comment.getCommunity().getUsers().getId()))) {
				// Set response for private comment
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
					false,
					isCommentBlocked
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
						false,
						isCommentBlocked
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
	public ResponseEntity<CommonResponse.ListResponse<CommentResponse>> getCommentList(
		CustomUserPrincipal customUserPrincipal, Long communityId) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		List<Comment> comments = commentRepository.findAllByCommunity_Id(communityId);
		System.out.println(comments);
		List<CommentResponse> commentResponses = comments.stream()
			.filter(comment -> comment.getParent() == null)
			.map(comment -> mapCommentToResponse(comment, users))
			.toList();
		return responseService.getListResponse(HttpStatus.OK.value(), commentResponses, "댓글을 조회했습니다.");
	}

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CommentResponse>> createComment(
		CustomUserPrincipal customUserPrincipal, Long communityId, CommentRequest commentRequestDTO) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		Community community = communityRepository.getById(communityId);
		Comment comment = commentRequestMapper.toEntity(commentRequestDTO);
		Comment parentComment = null;
		String title;
		String bodyTitle;
		String content = commentRequestDTO.getContent();
		if (commentRequestDTO.getParentId() != null) {
			parentComment = commentRepository.getById(commentRequestDTO.getParentId());
			content = removeUserTag(content, parentComment);
			comment.setContent(content);
			if (parentComment.getParent() != null) {
				throw new CustomException(REPLYING_NOT_ALLOWED);
			}
			comment.setParent(parentComment);
		} else {
			comment.setContent(commentRequestDTO.getContent());
		}
		comment.setCommunity(community);
		comment.setUsers(users);
		boolean isPrivate = commentRequestDTO.getIsPrivated();
		comment.setIsPrivated(isPrivate);
		boolean isAuthor = Objects.equals(users.getId(),
			comment.getCommunity().getUsers().getId());
		comment.setAuthor(isAuthor);
		comment.changeIsDeleted(false);
		commentRepository.save(comment);
		users.addComment(comment);
		boolean commentAuthor = users.getId().equals(comment.getUsers().getId());
		if (!community.getUsers().getId().equals(users.getId())) {
			if (commentRequestDTO.getParentId() == null) {
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
		return responseService.getSingleResponse(HttpStatus.OK.value(),
			new CommentResponse(comment.getId(), comment.getUsers().getId(), comment.getUsers().getNickname(),
				addUserTag(comment), comment.getCreatedDate(), comment.getUsers().getProfile(), comment.getAuthor(),
				commentAuthor, false, comment.getIsPrivated(), false, false), "댓글을 등록했습니다.");
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
			.createdAt(LocalDateTime.now())
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
	public ResponseEntity<CommonResponse.SingleResponse<CommentResponse>> deleteComment(
		CustomUserPrincipal customUserPrincipal, Long commentId) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		Comment comment = commentRepository.getById(commentId);
		validateCommentByUser(users.getId(), comment.getUsers().getId());
		comment.changeIsDeleted(true);
		CommentResponse commentResponse = leaveCommentToDto(users, comment);
		return responseService.getSingleResponse(HttpStatus.OK.value(), commentResponse, "댓글을 삭제 하였습니다.");
	}

	private Comment getDeletableAncestorComment(Comment comment) {
		Comment parent = comment.getParent();
		if (parent != null && parent.getChildren().size() == 1 && parent.getIsDeleted())
			return getDeletableAncestorComment(parent);
		return comment;
	}

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CommentResponse>> updateComment(
		CustomUserPrincipal customUserPrincipal, Long commentId, CommentRequest commentRequestDTO) {
		Users users = userRepository.getById(customUserPrincipal.getId());
		Comment comment = commentRepository.getById(commentId);
		validateCommentByUser(users.getId(), comment.getUsers().getId());
		comment.updateContent(commentRequestDTO.getContent());
		boolean isPrivate = commentRequestDTO.getIsPrivated();
		boolean commentAuthor = users.getId().equals(comment.getUsers().getId());
		comment.setIsPrivated(isPrivate);
		return responseService.getSingleResponse(HttpStatus.OK.value(),
			new CommentResponse(comment.getId(), comment.getUsers().getId(), comment.getUsers().getNickname(),
				comment.getContent(),
				comment.getCreatedDate(), comment.getUsers().getProfile(), comment.getAuthor(), commentAuthor, false,
				comment.getIsPrivated(), false, false), "댓글을 수정했습니다.");
	}
}