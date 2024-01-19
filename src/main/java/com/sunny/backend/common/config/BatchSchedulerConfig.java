package com.sunny.backend.common.config;

import static org.quartz.JobBuilder.newJob;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class BatchSchedulerConfig {
  @Autowired
  private Scheduler scheduler;
  @PostConstruct
  public void runJob(){
    JobDetail jobDetail = buildJobDetail(BatchConfig.class, new HashMap());
    try{
      scheduler.scheduleJob(jobDetail, buildJobTrigger("0 0 00 * * ?"));
    } catch(SchedulerException e){
      e.printStackTrace();
    }
  }
  public Trigger buildJobTrigger(String scheduleExp){
    return TriggerBuilder.newTrigger()
        .withSchedule(CronScheduleBuilder.cronSchedule(scheduleExp)).build();
  }

  public JobDetail buildJobDetail(Class job, Map params){
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.putAll(params);
    return newJob(job).usingJobData(jobDataMap).build();
  }
}
