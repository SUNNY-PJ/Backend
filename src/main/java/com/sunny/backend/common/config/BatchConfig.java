package com.sunny.backend.common.config;

import com.sunny.backend.save.domain.Save;
import com.sunny.backend.save.repository.SaveRepository;

import java.time.LocalDate;
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

  @Override
  protected void executeInternal(JobExecutionContext jobExecutionContext)
      throws JobExecutionException {
    log.info("save delete");
    LocalDate today=LocalDate.now();
          List<Save> save=saveRepository.findByEndDate(today);
          if(save.size()>0){
            for(Save saveGoal : save) {
              saveRepository.deleteById(saveGoal.getId());
            }
          }
      }
}
