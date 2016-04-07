package com.baidu.unbiz.multiengine.exception;

/**
 * 业务异常，Just for test
 *
 * @author wangchongjie
 * @date 2015年11月23日
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -7375423850222016116L;

    public BusinessException(String msg) {
        super(msg);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
