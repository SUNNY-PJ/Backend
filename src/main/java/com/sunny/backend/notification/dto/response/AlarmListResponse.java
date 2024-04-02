package com.sunny.backend.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sunny.backend.notification.domain.CommentNotification;
import com.sunny.backend.notification.domain.CompetitionNotification;
import com.sunny.backend.notification.domain.FriendsNotification;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public record AlarmListResponse(
    String alarmId,
    Long id,
    String postAuthor,
    String title,
    String notificationContent,
    String profileImg,

    boolean isToday,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm", timezone = "Asia/Seoul")
    LocalDateTime createdAt
) {

  public static List<AlarmListResponse> commentNotification(
      List<CommentNotification> commentNotifications) {

    return commentNotifications.stream()
        .map(commentNotification -> new AlarmListResponse(
            UUID.randomUUID().toString(),
            commentNotification.getCommunity().getId(),
            commentNotification.getComment().getUsers().getNickname(),
            commentNotification.getTitle(),
            commentNotification.getComment().getContent(),
            commentNotification.getComment().getUsers().getProfile(),
            commentNotification.getComment().getCreatedDate().toLocalDate()
                .isEqual(LocalDate.now()),
            commentNotification.getComment().getCreatedDate()
        ))
        .toList();
  }

  public record friendsNotification(
      String alarmId,
      Long id,
      Long friendsId,
      String postAuthor,
      String title,
      String notificationContent,
      String profileImg,
      boolean isToday,
      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm", timezone = "Asia/Seoul")
      LocalDateTime createdAt
  ) {

    public static List<AlarmListResponse> freindsFrom(
        List<FriendsNotification> friendsNotifications) {

      return friendsNotifications.stream()
          .map(friendsNotification -> new AlarmListResponse(
              UUID.randomUUID().toString(),
              friendsNotification.getFriend().getId(), //상대방꺼 id
              friendsNotification.getFriend().getNickname(),
              friendsNotification.getTitle(),
              friendsNotification.getFriend().getNickname()+friendsNotification.getBody(),
              friendsNotification.getFriend().getProfile(),
              friendsNotification.getCreatedAt().toLocalDate().isEqual(LocalDate.now()),
              friendsNotification.getCreatedAt()
          ))
          .toList();
    }
  }

  public record CompetitionNotificationResponse(
      String alarmId,
      Long competitionId,
      String postAuthor,
      String title,
      String notificationContent,
      String profileImg,
      boolean isToday,
      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm", timezone = "Asia/Seoul")
      LocalDateTime createdAt
  ) {

    public static List<AlarmListResponse> competitionFrom(
        List<CompetitionNotification> competitionNotifications) {
      return competitionNotifications.stream()
          .map(competitionNotification -> new AlarmListResponse(
              UUID.randomUUID().toString(),
              competitionNotification.getCompetition().getId(),
              competitionNotification.getName(),
              competitionNotification.getTitle(),
              competitionNotification.getName()+competitionNotification.getBody(),
              competitionNotification.getFriend().getProfile(),
              competitionNotification.getCreatedAt().toLocalDate().isEqual(LocalDate.now()),
              LocalDateTime.now()
          ))
          .toList();
    }
  }
}
