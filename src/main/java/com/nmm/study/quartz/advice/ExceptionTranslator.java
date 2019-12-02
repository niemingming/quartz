package com.nmm.study.quartz.advice;

import com.nmm.study.quartz.exception.ResultException;
import com.nmm.study.quartz.utils.ResultUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionTranslator {

    /**
     * 参数校验异常
     * @param ex
     * @author nmm
     * @date 2019/11/29
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity dealWithCheck(MethodArgumentNotValidException ex) {
        ex.printStackTrace();
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    return String.format("%s.%n",error.getDefaultMessage());
                }).collect(Collectors.joining());

        return ResultUtil.error(ResultUtil.METHOD_ARGUMENT_ERROR_CODE,msg);
    }
    /**
     * 校验参数异常
     * @param ex
     * @author nmm
     * @date 2019/12/2
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity dealWithArgumentCheck(IllegalArgumentException ex) {
        ex.printStackTrace();
        String msg = ex.getLocalizedMessage();
        return ResultUtil.error(ResultUtil.METHOD_ARGUMENT_ERROR_CODE,msg);
    }
    /**
     * 业务异常处理
     * @param re
     * @author nmm
     * @date 2019/11/29
     */
    @ExceptionHandler(ResultException.class)
    public ResponseEntity dealWithResultException(ResultException re) {
        re.printStackTrace();
        return ResultUtil.error(re.getErrorCode(),re.getMsg());
    }
}
