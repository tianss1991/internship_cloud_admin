package com.above.exception;


/**
 * @Description: 操作数据库异常抛出
 * @Author: LZH
 * @Date: 2022/7/12 14:11
 */
public class OptionDateBaseException extends Exception{

    /**
     * 异常信息
     */
    public String message;

    public OptionDateBaseException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
