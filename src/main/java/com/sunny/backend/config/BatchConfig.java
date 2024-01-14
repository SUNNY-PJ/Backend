package com.sunny.backend.config;

import com.sunny.backend.save.domain.Save;
import com.sunny.backend.save.repository.SaveRepository;
import com.sunny.backend.save.service.SaveService;
import com.sunny.backend.security.userinfo.CustomUserPrincipal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;


@Component

public class BatchConfig extends QuartzJobBean {
  @Autowired
  private SaveRepository saveRepository;
  private static final Logger log = LoggerFactory.getLogger(BatchConfig.class);

  @Override
  protected void executeInternal(JobExecutionContext jobExecutionContext)
      throws JobExecutionException {
    log.info("save delete");
    LocalDate today=LocalDate.now();
          List<Save> save=saveRepository.findByEndDate(today);
          if(save.size()>0 && save!=null){
            for(Save saveGoal : save) {
              saveRepository.deleteById(saveGoal.getId());
            }
          }
  }
}
