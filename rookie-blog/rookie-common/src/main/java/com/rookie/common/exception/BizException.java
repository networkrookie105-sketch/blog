package com.rookie.common.exception;

import lombok.Data;

/**
 * @author peishunwu
 * @ClassName BizException
 * @description: TODO
 * @date 2023年11月25日
 * @version: 1.0
 */
@Data
public class BizException extends RuntimeException{
    //异常码
    private String errorCode;
    //错误信息
    private String errorMessage;

    public BizException(String code, String message) {
        super(message);
        this.errorCode = code;
        this.errorMessage = message;
    }

    public BizException(BaseExceptionInterface baseExceptionInterface){
        this.errorCode = baseExceptionInterface.getErrorCode();
        this.errorMessage = baseExceptionInterface.getErrorMessage();
    }


}
