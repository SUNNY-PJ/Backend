package com.sunny.backend.common.config;

import com.sunny.backend.notification.domain.CommentNotification;
import com.sunny.backend.notification.repository.CommentNotificationRepository;
import com.sunny.backend.save.domain.Save;
import com.sunny.backend.save.repository.SaveRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;


@Component

public class BatchConfig extends QuartzJobBean {

  @Autowired
  private SaveRepository saveRepository;
  private static final Logger log = LoggerFactory.getLogger(BatchConfig.class);
  @Autowired
  private CommentNotificationRepository commentNotificationRepository;

  @Override
  protected void executeInternal(JobExecutionContext jobExecutionContext)
      throws JobExecutionException {
    LocalDate today = LocalDate.now();
    List<Save> save = saveRepository.findByEndDate(today);
    if (save.size() > 0) {
      for (Save saveGoal : save) {
        saveRepository.deleteById(saveGoal.getId());
      }
    }
    LocalDateTime thirtyDaysAgo = LocalDateTime.now().minus(1, ChronoUnit.DAYS);
    //30일 이후 check , 근데 자정 이후에 삭제 되도록 함 ?(LocalDateTime까지 맞춰야 됨 ? ?)
    List<CommentNotification> notifications = commentNotificationRepository.findByCreatedDateAfter(thirtyDaysAgo);
    System.out.println(notifications);

    for (CommentNotification record : notifications) {
      log.info("댓글 notification  delete");
     commentNotificationRepository.deleteById(record.getId());
    }
  }
}