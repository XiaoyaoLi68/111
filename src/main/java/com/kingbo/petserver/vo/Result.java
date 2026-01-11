package com.kingbo.petserver.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.formula.functions.T;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private Boolean success; // 是否成功
    private String message; // 返回操作信息
    private T data; // 返回数据

    // 成功的静态方法
    public static <T> Result<T> success(T data) {
        Result<T> Result = new Result<>();
        Result.setSuccess(true);
        Result.setMessage("操作成功");
        Result.setData(data);
        return Result;
    }

    // 失败的静态方法
    public static <T> Result<T> failure(String message) {
        Result<T> Result = new Result<>();
        Result.setSuccess(false);
        Result.setMessage(message);
        Result.setData(null);
        return Result;
    }
}
