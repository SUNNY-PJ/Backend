package com.sunny.backend.common.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.sunny.backend.notification.domain.CommentNotification;
import com.sunny.backend.notification.repository.CommentNotificationRepository;
import com.sunny.backend.save.repository.SaveRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BatchConfig extends QuartzJobBean {

	@Autowired
	private SaveRepository saveRepository;
	@Autowired
	private CommentNotificationRepository commentNotificationRepository;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext)
		throws JobExecutionException {
		LocalDate today = LocalDate.now();
		//    List<Save> save = saveRepository.findByEndDate(today);
		//    if (save.size() > 0) {
		//      for (Save saveGoal : save) {
		//        saveRepository.deleteById(saveGoal.getId());
		//      }
		//    }
		LocalDateTime thirtyDaysAgo = LocalDateTime.now().minus(1, ChronoUnit.DAYS);
		//30일 이후 check , 근데 자정 이후에 삭제 되도록 함 ?(LocalDateTime까지 맞춰야 됨 ? ?)
		List<CommentNotification> notifications = commentNotificationRepository.findByCreatedDateAfter(thirtyDaysAgo);
		System.out.println(notifications);
		for (CommentNotification record : notifications) {
			log.info("댓글 notification delete");
			commentNotificationRepository.deleteById(record.getId());
		}
	}
}