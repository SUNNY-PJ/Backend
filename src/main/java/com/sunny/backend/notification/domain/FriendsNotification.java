package com.sunny.backend.notification.domain;

import com.sunny.backend.common.BaseTime;
import com.sunny.backend.user.domain.Users;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class FriendsNotification extends BaseTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name= "users_id")
  private Users users;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_friend_id")
  private Users friend;

  @Column
  private String title;

  @Column
  private String body;

  @Column
  private LocalDateTime createdAt;
}
