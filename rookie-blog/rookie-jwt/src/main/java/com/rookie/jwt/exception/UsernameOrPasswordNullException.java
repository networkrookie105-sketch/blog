package com.rookie.jwt.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author peishunwu
 * @ClassName UsernameOrPasswordNullException
 * @description: TODO
 * @date 2023年12月20日
 * @version: 1.0
 */
public class UsernameOrPasswordNullException extends AuthenticationException {
    public UsernameOrPasswordNullException(String message,Throwable cause) {
        super(message,cause);
    }
    public UsernameOrPasswordNullException(String msg) {
        super(msg);
    }

}
