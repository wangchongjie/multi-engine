package com.baidu.unbiz.multiengine.exception;

/**
 * ClassName: CodecException
 * Function: 编解码异常
 */
public class MultiEngineException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2396421433506179782L;

    /**
     * Creates a new instance of CodecException.
     */
    public MultiEngineException() {
        super();
    }

    /**
     * Creates a new instance of CodecException.
     *
     * @param arg0
     * @param arg1
     */
    public MultiEngineException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * Creates a new instance of CodecException.
     *
     * @param arg0
     */
    public MultiEngineException(String arg0) {
        super(arg0);
    }

    /**
     * Creates a new instance of CodecException.
     *
     * @param arg0
     */
    public MultiEngineException(Throwable arg0) {
        super(arg0);
    }

}
