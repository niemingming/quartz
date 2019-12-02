package com.nmm.study.quartz.listener;

import com.nmm.study.quartz.entity.JobExeLog;
import com.nmm.study.quartz.service.JobExeLogService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.listeners.JobListenerSupport;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 定时任务监听，注意，定时任务是单线程执行，任务监听我们可以定义一个实例
 * @author nmm
 * @date 2019/12/2
 */
@Component
public class JobListener extends JobListenerSupport implements InitializingBean {
    @Resource
    private JobExeLogService jobExeLogService;
    @Autowired
    private Scheduler scheduler;

    @Override
    public String getName() {
        return "joblistener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {

        try{
            //定时任务开始执行
            JobExeLog log = new JobExeLog();
            JobKey key = context.getJobDetail().getKey();
            log.setJobgroup(key.getGroup());
            log.setJobname(key.getName());
            log.setStartTime(LocalDateTime.now());
            jobExeLogService.save(log);

            context.getJobDetail().getJobDataMap().put("logId",log.getId());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        try{
            Long logid = context.getJobDetail().getJobDataMap().getLong("logId");
            JobExeLog log = jobExeLogService.getById(logid);
            log.setEndTime(LocalDateTime.now());
            if (jobException != null) {
                log.setStatus(false);
            }
            jobExeLogService.updateById(log);
        }catch ( Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduler.getListenerManager().addJobListener(this);
    }
}
