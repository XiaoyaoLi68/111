package com.kingbo.petserver.exception;
/*
    我们自定义的异常信息,出现业务层次的异常的时候,可以抛出这个异常...
 */
public class ShopCheckException extends RuntimeException {
    // 自定义错误信息
    private String message;

    // 有参构造 传递我们自己定义的错误信息
    public ShopCheckException(String message) {
        super(message);
        this.message = message;
    }

    // 创建一个静态方法 快速抛出异常
    public static void throwPetException(String message) {
        throw new ShopCheckException(message);
    }
}