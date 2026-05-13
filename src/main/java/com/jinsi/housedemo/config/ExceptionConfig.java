package com.jinsi.housedemo.config;

import com.jinsi.housedemo.util.ErrorType;
import com.jinsi.housedemo.util.MyException;
import com.jinsi.housedemo.util.ResultData;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class ExceptionConfig {
    @ExceptionHandler(MyException.class)
    public Object myExceptionHanler(MyException e){
        return new ResultData(e.getCode(),e.getMessage());
    }
    @ExceptionHandler(SQLException.class)
    public Object sqlExceptionHandler(SQLException e){
        e.printStackTrace();
        return new ResultData(ErrorType.SQL_ERROR,"系统错误，执行数据操作时发生异常");
    }
    @ExceptionHandler(Exception.class)
    public Object exceptionHandler(Exception e){
        e.printStackTrace();
        return new ResultData(ErrorType.SYSTEM_ERROR,"系统错误，请联系管理员");
    }
    Object o = new Object();

}
