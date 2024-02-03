package com.sunny.backend.comment.service;

import static com.sunny.backend.comment.domain.Comment.validateCommentByUser;
import static com.sunny.backend.comment.exception.CommentErrorCode.*;
import static com.sunny.backend.comment.exception.CommentErrorCode.REPLYING_NOT_ALLOWED;

import com.sunny.backend.common.exception.CustomException;
import com.sunny.backend.community.repository.CommunityRepository;
import com.sunny.backend.notification.domain.CommentNotification;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.notification.dto.request.NotificationPushRequest;
import com.sunny.backend.notification.repository.CommentNotificationRepository;
import com.sunny.backend.notification.repository.NotificationRepository;
import com.sunny.backend.notification.service.NotificationService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.response.ResponseService;
import com.sunny.backend.comment.dto.request.CommentRequest;
import com.sunny.backend.comment.dto.request.CommentRequestMapper;
import com.sunny.backend.comment.dto.response.CommentResponse;
import com.sunny.backend.comment.domain.Comment;

import com.sunny.backend.comment.repository.CommentRepository;
import com.sunny.backend.community.domain.Community;
import com.sunny.backend.auth.jwt.CustomUserPrincipal;
import com.sunny.backend.user.domain.Users;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final CommunityRepository communityRepository;
	private final CommentRequestMapper commentRequestMapper;
	private final ResponseService responseService;
	private final NotificationService notificationService;
	private final CommentNotificationRepository commentNotificationRepository;
	private final NotificationRepository notificationRepository;

	private CommentResponse mapCommentToResponse(Comment comment, Users currentUser) {
		boolean isPrivate = comment.getIsPrivated();
		CommentResponse commentResponse;
		if (isPrivate && !(currentUser.getId() == comment.getUsers().getId() ||
				currentUser.getId() == comment.getCommunity().getUsers().getId())) {
			commentResponse = new CommentResponse(comment.getId(),comment.getUsers().getId(), currentUser.getName(),
					"비밀 댓글입니다.",comment.getCreatedDate(),comment.getAuthor());

		} else {
			commentResponse = new CommentResponse(
					comment.getId(),
					comment.getUsers().getId(),
					comment.getUsers().getName(),
					comment.getContent(),
					comment.getCreatedDate(),
					comment.getAuthor()
			);
		}
		commentResponse.setChildren(comment.getChildren()
				.stream()
				.map(childComment -> mapCommentToResponse(childComment, currentUser))
				.toList()
		);
		return commentResponse;
	}
	@Transactional
	public ResponseEntity<CommonResponse.ListResponse<CommentResponse>> getCommentList(
			CustomUserPrincipal customUserPrincipal, Long communityId) {
		Users user = customUserPrincipal.getUsers();
		List<Comment> comments = commentRepository.findAllByCommunity_Id(communityId);
		List<Comment> DeletedComments = comments.stream()
				.filter(comment -> !comment.getIsDeleted())
				.toList();
		List<CommentResponse> commentResponses = Stream.concat(
				DeletedComments.stream().map(comment -> mapCommentToResponse(comment, user)),
				comments.stream().filter(Comment::getIsDeleted).map(CommentResponse::convertCommentToDto)
		).toList();
		return responseService.getListResponse(HttpStatus.OK.value(), commentResponses, "댓글을 조회했습니다.");
	}
	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CommentResponse>> createComment(
			CustomUserPrincipal customUserPrincipal, Long communityId, CommentRequest commentRequestDTO) {
		Users user = customUserPrincipal.getUsers();
		Community community = communityRepository.getById(communityId);
		Comment comment = commentRequestMapper.toEntity(commentRequestDTO);
		Comment parentComment;
		if (commentRequestDTO.getParentId() != null) {
			parentComment = commentRepository.getById(commentRequestDTO.getParentId());
			if (parentComment.getParent() != null) {
				throw new CustomException(REPLYING_NOT_ALLOWED);
			}
			comment.setParent(parentComment);
		}
		comment.setCommunity(community);
		comment.setContent(commentRequestDTO.getContent());
		comment.setUsers(user);
		boolean isPrivate = commentRequestDTO.getIsPrivated();
		comment.setIsPrivated(isPrivate);
		boolean isAuthor=Objects.equals(customUserPrincipal.getUsers().getId(),
				comment.getCommunity().getUsers().getId());

		comment.setAuthor(isAuthor);
		commentRepository.save(comment);

		user.addComment(comment);
		if(community.getUsers().getId()!=customUserPrincipal.getUsers().getId()){
			if(commentRequestDTO.getParentId() == null) {
				sendNotifications(customUserPrincipal, comment, community);
			}
			else{
				System.out.println("replySendNotification response success");
					replySendNotifications(customUserPrincipal,comment.getParent().getUsers(), comment, community);
			}
		}
		return responseService.getSingleResponse(HttpStatus.OK.value(),
				new CommentResponse(comment.getId(),comment.getUsers().getId(), comment.getUsers().getName(), comment.getContent(),
						comment.getCreatedDate(), isAuthor),"댓글을 등록했습니다.");
	}

	private void replySendNotifications(CustomUserPrincipal customUserPrincipal,Users users,
			Comment comment, Community community) {
		Long postAuthor=users.getId();
		List<Notification> notificationList=notificationRepository.findByUsers_Id(users.getId());
		String body = comment.getContent();
		String title="[SUNNY] "+customUserPrincipal.getName();
		String bodyTitle="새로운 답글이 달렸어요";
		CommentNotification commentNotification=CommentNotification.builder()
				.users(users)
				.community(community)
				.comment(comment)
				.parent_id(comment.getParent())
				.title("새로운 답글이 달렸어요.")
				.build();
		commentNotificationRepository.save(commentNotification);
		if(notificationList.size()!=0) {
			NotificationPushRequest notificationPushRequest = new NotificationPushRequest(
					postAuthor,
					bodyTitle,
					body
			);
				notificationService.sendNotificationToFriends(title,notificationPushRequest);
			}
	}
	private void sendNotifications(CustomUserPrincipal customUserPrincipal,
			Comment comment, Community community) {
		Long postAuthor=community.getUsers().getId();
		List<Notification> notificationList=notificationRepository.findByUsers_Id(postAuthor);
		String body = comment.getContent();
		String title="[SUNNY] "+customUserPrincipal.getName();
		String bodyTitle="새로운 댓글이 달렸어요.";
		CommentNotification commentNotification=CommentNotification.builder()
				.users(community.getUsers())
				.community(community)
				.comment(comment)
				.parent_id(comment.getParent())
				.title("새로운 댓글이 달렸어요.")
				.build();
		commentNotificationRepository.save(commentNotification);
		if(notificationList.size()!=0) {
			NotificationPushRequest notificationPushRequest = new NotificationPushRequest(
					postAuthor,
					bodyTitle,
					body
			);
				notificationService.sendNotificationToFriends(title,notificationPushRequest);
		}
	}

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CommentResponse>> deleteComment(
			CustomUserPrincipal customUserPrincipal, Long commentId) {
		Comment comment = commentRepository.findCommentByIdWithParent(commentId)
				.orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
		validateCommentByUser(customUserPrincipal.getUsers().getId(),comment.getUsers().getId());
		comment.changeIsDeleted(true);
		CommentResponse commentResponse=CommentResponse.convertCommentToDto(comment);
		return responseService.getSingleResponse(HttpStatus.OK.value(), commentResponse,"댓글을 삭제 하였습니다.");
	}

	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CommentResponse>> updateComment(
			CustomUserPrincipal customUserPrincipal, Long commentId, CommentRequest commentRequestDTO) {
		Comment comment = commentRepository.getById(commentId);
		validateCommentByUser(customUserPrincipal.getUsers().getId(),comment.getUsers().getId());
		comment.updateContent(commentRequestDTO.getContent());
		boolean isPrivate = commentRequestDTO.getIsPrivated();
		boolean isAuthor=Objects.equals(customUserPrincipal.getUsers().getId(),
				comment.getCommunity().getUsers().getId());
		comment.setIsPrivated(isPrivate);
		return responseService.getSingleResponse(HttpStatus.OK.value(),
				new CommentResponse(comment.getId(),comment.getUsers().getId(), comment.getUsers().getName(), comment.getContent(),
						comment.getCreatedDate(), isAuthor),"댓글을 수정했습니다.");
	}
}