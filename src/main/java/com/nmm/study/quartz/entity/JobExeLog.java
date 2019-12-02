package com.nmm.study.quartz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

@Data
@TableName("job_exe_log")
public class JobExeLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 任务分组
    */
    private String jobgroup;

    /**
    * 任务名称
    */
    private String jobname;

    /**
    * 任务开始时间
    */
    private LocalDateTime startTime;

    /**
    * 结束时间
    */
    private LocalDateTime endTime;

    /**
    * 是否成功
    */
    private Boolean status;
}