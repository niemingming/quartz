
/**任务执行记录*/
create table job_exe_log (
                             id bigint(20) primary key auto_increment,
                             jobgroup varchar(64) not null comment '任务分组',
                             jobname varchar(128) not null comment '任务名称',
                             start_time datetime  comment '任务开始时间',
                             end_time datetime comment '结束时间',
                             status tinyint(1)  default 1 COMMENT '是否成功'
);
/**未执行任务记录*/
create table job_mis_log (
                             id bigint(20) primary key auto_increment,
                             jobgroup varchar(64) not null comment '任务分组',
                             jobname varchar(128) not null comment '任务名称',
                             create_time datetime  comment '任务开始时间'
);
