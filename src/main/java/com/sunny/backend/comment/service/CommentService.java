package com.sunny.backend.comment.service;

import static com.sunny.backend.comment.domain.Comment.validateCommentByUser;
import static com.sunny.backend.common.CommonErrorCode.*;

import com.sunny.backend.community.repository.CommunityRepository;
import com.sunny.backend.notification.domain.CommentNotification;
import com.sunny.backend.notification.domain.Notification;
import com.sunny.backend.notification.dto.request.NotificationPushRequest;
import com.sunny.backend.notification.dto.request.NotificationRequest;
import com.sunny.backend.notification.repository.CommentNotificationRepository;
import com.sunny.backend.notification.repository.NotificationRepository;
import com.sunny.backend.notification.service.NotificationService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunny.backend.common.response.CommonResponse;
import com.sunny.backend.common.CommonCustomException;
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
		boolean isAuthor=Objects.equals(currentUser.getId(),
				comment.getCommunity().getUsers().getId());
		CommentResponse commentResponse;
		if (isPrivate && !(currentUser.getId() == comment.getUsers().getId() ||
				currentUser.getId() == comment.getCommunity().getUsers().getId())) {
			commentResponse = new CommentResponse(comment.getId(), currentUser.getName(),
					"비밀 댓글입니다.", isAuthor,comment.getCreatedDate());

		} else {
			commentResponse = new CommentResponse(
					comment.getId(),
					comment.getUsers().getName(),
					comment.getContent(),
					isAuthor,
					comment.getCreatedDate()
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
		Community community = communityRepository.findById(communityId)
				.orElseThrow(() -> new CommonCustomException(COMMUNITY_NOT_FOUND));
		List<Comment> comments = commentRepository.findAllByCommunity_Id(communityId);
		List<CommentResponse> commentResponses = comments.stream()
				.filter(comment -> comment.getParent() == null)
				.map(comment -> mapCommentToResponse(comment, user))
				.toList();
		return responseService.getListResponse(HttpStatus.OK.value(), commentResponses, "댓글을 조회했습니다.");
	}
	@Transactional
	public ResponseEntity<CommonResponse.SingleResponse<CommentResponse>> createComment(
			CustomUserPrincipal customUserPrincipal, Long communityId, CommentRequest commentRequestDTO)
			throws IOException {
		Users user = customUserPrincipal.getUsers();
		Community community = communityRepository.findById(communityId)
				.orElseThrow(() -> new CommonCustomException(COMMUNITY_NOT_FOUND));
		Comment comment = commentRequestMapper.toEntity(commentRequestDTO);
		Comment parentComment = null;
		if (commentRequestDTO.getParentId() != null) {
			parentComment = commentRepository.findById(commentRequestDTO.getParentId())
					.orElseThrow(() -> new CommonCustomException(COMMENT_NOT_FOUND));
			if (parentComment.getParent() != null) {
				throw new CommonCustomException(REPLYING_NOT_ALLOWED);
			}
			comment.setParent(parentComment);
		}
		comment.setCommunity(community);
		comment.setContent(commentRequestDTO.getContent());
		comment.setUsers(user);
		boolean isPrivate = commentRequestDTO.getIsPrivated();
		comment.setIsPrivated(isPrivate);
		commentRepository.save(comment);
		boolean isAuthor=Objects.equals(customUserPrincipal.getUsers().getId(),
				comment.getCommunity().getUsers().getId());
		System.out.println(isAuthor);
		user.addComment(comment);
		if(community.getUsers().getId()!=customUserPrincipal.getUsers().getId()){
			if(commentRequestDTO.getParentId() == null) {
				sendNotifications(customUserPrincipal, comment, community);
			}
			else{
					replySendNotifications(customUserPrincipal,comment.getParent().getUsers(), comment, community);
			}
		}
		return responseService.getSingleResponse(HttpStatus.OK.value(),
				new CommentResponse(comment.getId(), comment.getUsers().getName(), comment.getContent(),isAuthor,
						comment.getCreatedDate()), "댓글을 등록했습니다.");
	}

	private void replySendNotifications(CustomUserPrincipal customUserPrincipal,Users users,
			Comment comment, Community community) throws IOException {
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
			Comment comment, Community community) throws IOException {
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
				.orElseThrow(() -> new CommonCustomException(COMMENT_NOT_FOUND));
		validateCommentByUser(customUserPrincipal.getUsers().getId(),comment.getUsers().getId());
		boolean isAuthor= Objects.equals(customUserPrincipal.getUsers().getId(),
				comment.getCommunity().getUsers().getId());
			if (comment.getChildren().size() != 0) {
				comment.changeIsDeleted(true);
				CommentResponse commentResponse=CommentResponse.convertCommentToDto(comment,isAuthor);
				return responseService.getSingleResponse(HttpStatus.OK.value(), commentResponse,"댓글을 삭제 하였습니다.");
			} else {
				commentRepository.delete(getDeletableAncestorComment(comment));
				return responseService.getSingleResponse(HttpStatus.OK.value(), null,"댓글을 삭제 하였습니다.");
		}

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

		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new CommonCustomException(COMMENT_NOT_FOUND));
		validateCommentByUser(customUserPrincipal.getUsers().getId(),comment.getUsers().getId());
		comment.updateContent(commentRequestDTO.getContent());
		boolean isPrivate = commentRequestDTO.getIsPrivated();
		boolean isAuthor=Objects.equals(customUserPrincipal.getUsers().getId(),
				comment.getCommunity().getUsers().getId());
		System.out.println(isAuthor);
		comment.setIsPrivated(isPrivate);
		return responseService.getSingleResponse(HttpStatus.OK.value(),
				new CommentResponse(comment.getId(), comment.getUsers().getName(), comment.getContent(),isAuthor,
						comment.getCreatedDate()), "댓글을 수정했습니다.");
	}


}