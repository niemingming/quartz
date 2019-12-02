package com.nmm.study.quartz.listener;

import com.nmm.study.quartz.entity.JobMisLog;
import com.nmm.study.quartz.service.JobMisLogService;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;

/**
 * 触发器监听
 * @author nmm
 * @date 2019/12/2
 */
@Component
public class TriggerListener extends TriggerListenerSupport implements InitializingBean {
    @Resource
    private JobMisLogService misLogService;
    @Autowired
    private Scheduler scheduler;

    @Override
    public String getName() {
        return "triggerlistener";
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        try {
            JobMisLog log = new JobMisLog();
            log.setJobgroup(trigger.getJobKey().getGroup());
            log.setJobname(trigger.getJobKey().getName());
            log.setCreateTime(LocalDateTime.now());
            log.setFireTime(LocalDateTime.ofInstant(trigger.getPreviousFireTime().toInstant(), TimeZone.getDefault().toZoneId()));
            misLogService.save(log);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduler.getListenerManager().addTriggerListener(this);
    }
}
