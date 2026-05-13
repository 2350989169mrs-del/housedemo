package com.jinsi.housedemo.util;

import java.util.HashMap;
import java.util.Map;

public class ResultData {
    private int errorCode;
    private String msg;
    private Map<String,Object> result;

    public ResultData(String msg){
        this.errorCode = 0;
        this.msg = msg;
    }

    public ResultData(int errorCode, String msg){
        this.errorCode = errorCode;
        this.msg = msg;
    }
    public ResultData(String name,Object data, String msg){
        Map<String,Object> map=new HashMap<>();
        map.put(name,data);
        this.errorCode = 0;
        this.msg = msg;
        this.result = map;
    }
    public ResultData(String[] names,Object[] datas, String msg){
        Map<String,Object> map=new HashMap<>();
        for(int i=0;i<datas.length;i++){
            map.put(names[i], datas[i]);
        }
        this.errorCode = 0;
        this.msg = msg;
        this.result = map;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }
}
