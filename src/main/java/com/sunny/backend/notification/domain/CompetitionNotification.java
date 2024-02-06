package com.sunny.backend.notification.domain;

import com.fasterxml.jackson.databind.ser.Serializers.Base;
import com.sunny.backend.common.BaseTime;
import com.sunny.backend.competition.domain.Competition;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CompetitionNotification extends BaseTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) //상대방꺼
  @JoinColumn(name= "users_id")
  private Users users;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name= "competition_id")
  private Competition competition; //대결 id

  @Column
  private String title; //제목
  @Column
  private String body; //내용

  @Column
  private String name; //신청자 이름


  @Column
  private LocalDateTime createdAt;

}
