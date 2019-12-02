package com.nmm.study.quartz.controller;

import com.alibaba.fastjson.JSONObject;
import com.nmm.study.quartz.dto.JobDTO;
import com.nmm.study.quartz.exception.ResultException;
import com.nmm.study.quartz.utils.ResultUtil;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.impl.matchers.GroupMatcher.anyJobGroup;

@RestController
@RequestMapping("/api/v1")
public class QuartzListController {
    @Autowired
    private Scheduler scheduler;
    /**
     * 查询当前系统的任务列表
     * @author nmm
     * @date 2019/11/29
     */
    @GetMapping("/quartzjobs")
    public ResponseEntity quartzjobs() throws SchedulerException {
        //获取所有的定时任务key
        Set<JobKey> jobKeys = scheduler.getJobKeys(anyJobGroup());
        //遍历任务
        List<JSONObject> res = jobKeys.stream().map(jobKey -> {
            JSONObject obj = new JSONObject();
            try {
                obj.put("schedule",scheduler.getSchedulerName());
                obj.put("group",jobKey.getGroup());
                obj.put("jobname",jobKey.getName());
                obj.put("execClass",scheduler.getJobDetail(jobKey).getJobClass());
                //我们采用单任务单触发器的规则，不考虑单任务多触发器规则
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                if (triggers.isEmpty()){
                    obj.put("jobstatus", Trigger.TriggerState.PAUSED.name());
                    obj.put("triggertype","");
                    obj.put("triggerrule","");
                    obj.put("repeatCount","");
                }else {
                    obj.put("jobstatus",scheduler.getTriggerState(triggers.get(0).getKey()).name());
                    obj.put("triggertype",triggers.get(0) instanceof CronTrigger ? "CRON":"SIMPLE");
                    obj.put("triggerrule", triggers.get(0) instanceof CronTrigger  ? ((CronTriggerImpl)triggers.get(0)).getCronExpression():((SimpleTriggerImpl)triggers.get(0)).getRepeatInterval());
                    obj.put("repeatCount",triggers.get(0) instanceof CronTrigger  ? "":((SimpleTriggerImpl)triggers.get(0)).getRepeatCount());
                }
                obj.put("param", Optional.ofNullable(scheduler.getJobDetail(jobKey).getJobDataMap()).orElse(new JobDataMap()));
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
            return obj;
        }).collect(Collectors.toList());

        return ResultUtil.success(res);
    }
    /**
     * 添加定时任务
     * @param jobDTO
     * @author nmm
     * @date 2019/11/29
     */
    @PostMapping("/jobs")
    public ResponseEntity addJob(@Validated JobDTO jobDTO){
        Class targetJob = null;
        try {
            targetJob = Class.forName(jobDTO.getExecClass());
        } catch (ClassNotFoundException e) {
            throw new ResultException("任务执行类不存在！",e);
        }
        //创建任务
        JobDetail jobDetail = newJob()
                .withIdentity(jobDTO.getJobname(),"springjob")
                .ofType(targetJob)
                .build();
        //创建触发器
        Trigger trigger = createTrigger(jobDTO);
        try {
            scheduler.scheduleJob(jobDetail,trigger);
        } catch (SchedulerException e) {
            throw new ResultException("执行任务失败！",e);
        }

        return ResultUtil.success();
    }
    /**
     * 创建触发器
     * @param jobDTO
     * @author nmm
     * @date 2019/12/2
     */
    private Trigger createTrigger(JobDTO jobDTO) {
        Trigger trigger = null;
        if ("CRON".equalsIgnoreCase(jobDTO.getTriggerType())){
            trigger = newTrigger()
                    .withSchedule(cronSchedule(jobDTO.getTriggerRule()))
                    .startNow()
                    .withIdentity(jobDTO.getJobname(),"springjob")
                    .build();
        }else {
            trigger = newTrigger()
                    .withIdentity(jobDTO.getJobname(),"springjob")
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(Integer.parseInt(jobDTO.getTriggerRule()))
                            .withRepeatCount(jobDTO.getRepeatCount())
                    ).build();
        }
        return trigger;
    }

    /**
     * 重设定时任务
     * @param jobDTO
     * @author nmm
     * @date 2019/12/2
     */
    @PutMapping("/jobs/reset")
    public ResponseEntity resetJob(@Validated JobDTO jobDTO){
        try {
            Trigger trigger = scheduler.getTriggersOfJob(JobKey.jobKey(jobDTO.getJobname(),"springjob")).stream()
                    .findFirst().orElse(null);
            Trigger newTrigger = createTrigger(jobDTO);
            if (trigger == null) {
                //发布任务即可
                newTrigger = newTrigger.getTriggerBuilder().forJob(JobKey.jobKey(jobDTO.getJobname(),"springjob")).build();
                scheduler.scheduleJob(newTrigger);
            }else {
                scheduler.rescheduleJob(trigger.getKey(),newTrigger);
            }
        } catch (SchedulerException e) {
            throw new ResultException("执行任务失败！",e);
        }
        return ResultUtil.success();
    }
    /**
     * 重新运行任务
     * @param param
     * @author nmm
     * @date 2019/12/2
     */
    @PutMapping("/jobs/run")
    public ResponseEntity runJob(@RequestParam Map param) {
        JobKey jobKey = validateJobInfo(param);
        try {
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            throw new ResultException("操作失败！" + e.getMessage(),e);
        }
        return ResultUtil.success();
    }
    /**
     * 校验任务信息，并返回jobkey
     * @param param
     * @author nmm
     * @date 2019/12/2
     */
    private JobKey validateJobInfo(Map param) {
        Assert.notNull(param.get("group"),"未传分组信息！");
        Assert.notNull(param.get("jobname"),"未传任务名称信息！");
        String group = param.get("group") + "";
        String jobname = param.get("jobname") + "";
        return JobKey.jobKey(jobname,group);
    }

    /**
     * 暂停任务
     * @param param
     * @author nmm
     * @date 2019/12/2
     */
    @PutMapping("/jobs/pause")
    public ResponseEntity pauseJob(@RequestParam Map param) {

        try {
            scheduler.pauseJob(validateJobInfo(param));
        } catch (SchedulerException e) {
            throw new ResultException("操作失败！" + e.getMessage(),e);
        }
        return ResultUtil.success();
    }
    /**
     * 删除任务
     * @param param
     * @author nmm
     * @date 2019/12/2
     */
    @DeleteMapping("/jobs")
    public ResponseEntity removeJob(@RequestParam Map param) {
        try {
            scheduler.deleteJob(validateJobInfo(param));
        } catch (SchedulerException e) {
            throw new ResultException("操作失败！" + e.getMessage(),e);
        }
        return ResultUtil.success();
    }
}
