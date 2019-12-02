package com.nmm.study.quartz.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.ResponseEntity;

public class ResultUtil {

    public static final String SERVER_ERROR_CODE = "500";

    public static final String METHOD_ARGUMENT_ERROR_CODE = "400";//参数异常


    /**
     * 成功接口
     * @author nmm
     * @date 2019/11/29
     */
    public static ResponseEntity success(){
        return success(null);
    }

    public static ResponseEntity success(Object data) {

        JSONObject res = new JSONObject();
        res.put("success",true);
        res.put("data",data);

        return ResponseEntity.ok(res);

    }

    public static ResponseEntity error() {
        return error(SERVER_ERROR_CODE,null);
    }

    public static ResponseEntity error(String errorCode, String msg) {
        JSONObject res = new JSONObject();
        res.put("success",false);
        res.put("errorCode",errorCode);
        res.put("msg",msg);

        return ResponseEntity.ok(res);
    }
}
