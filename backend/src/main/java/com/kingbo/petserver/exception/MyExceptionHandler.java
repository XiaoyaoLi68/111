package com.kingbo.petserver.exception;



import com.kingbo.petserver.vo.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyExceptionHandler {

    // 1: 这个方法,专门处理自定义异常
    @ExceptionHandler(PethomeException.class)
    public Result doPetPhomeException(PethomeException pethomeException){
        // 面向异常对象,获取异常信息,并封装一个操作失败的结果
        pethomeException.printStackTrace(); // 给后端程序员看的原始异常信息
        // 下面的返回值是给前端看到结果
        return Result.failure(pethomeException.getMessage());
    }

    // 2: 这个方法,专门处理其他异常
    @ExceptionHandler(Exception.class)
    public Result doException(Exception exception){
        // 面向异常对象,获取异常信息,并封装一个操作失败的结果
        exception.printStackTrace(); // 给后端程序员看的原始异常信息
        // 下面的返回值是给前端看到结果
        return Result.failure(exception.getMessage());
    }

}

