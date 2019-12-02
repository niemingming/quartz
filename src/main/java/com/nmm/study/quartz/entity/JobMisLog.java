package com.nmm.study.quartz.entity;

import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("job_mis_log")
public class JobMisLog {

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
    private LocalDateTime createTime;
    /**
     * 上次触发时间
     */
    private LocalDateTime fireTime;
}