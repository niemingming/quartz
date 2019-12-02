package com.nmm.study.quartz.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class JobDTO {
    @NotEmpty(message = "任务名称不能为空！")
    private String jobname;
    @NotEmpty(message = "触发器类型不能为空！")
    private String triggerType;
    @NotEmpty(message = "触发器规则不能为空！")
    private String triggerRule;
    @NotEmpty(message = "任务执行类不能为空！")
    private String execClass;

    private Integer repeatCount;
}
