package com.nmm.study.quartz.exception;

import com.nmm.study.quartz.utils.ResultUtil;
import lombok.Data;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 业务返回异常，用于统一处理返回结果
 * @author nmm
 * @date 2019/11/29
 */
@Data
public class ResultException extends RuntimeException {

    private String errorCode;
    private String msg;
    public ResultException(Exception e){
        this(ResultUtil.SERVER_ERROR_CODE,null,e);
    }

    public ResultException(String msg,Exception e) {
        this(ResultUtil.SERVER_ERROR_CODE,msg,e);
    }

    public ResultException(String serverErrorCode,String msg,Exception e) {
        super(msg,e);
        this.errorCode = serverErrorCode;
        this.msg = msg;
    }
}
