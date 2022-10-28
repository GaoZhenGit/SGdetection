package com.gzpi.detection.bean;


public class BaseResponse {
    public int code;
    public String msg;
    public static BaseResponse success() {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.code = 0;
        baseResponse.msg = "success";
        return baseResponse;
    }

    public static BaseResponse fail(String msg) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.code = -1;
        baseResponse.msg = msg;
        return baseResponse;
    }
}
