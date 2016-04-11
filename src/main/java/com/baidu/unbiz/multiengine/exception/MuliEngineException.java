package com.baidu.unbiz.multiengine.exception;

/**
 * ClassName: CodecException <br/>
 * Function: 编解码异常
 */
public class MuliEngineException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2396421433506179782L;

    /**
     * Creates a new instance of CodecException.
     */
    public MuliEngineException() {
        super();
    }

    /**
     * Creates a new instance of CodecException.
     *
     * @param arg0
     * @param arg1
     */
    public MuliEngineException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * Creates a new instance of CodecException.
     *
     * @param arg0
     */
    public MuliEngineException(String arg0) {
        super(arg0);
    }

    /**
     * Creates a new instance of CodecException.
     *
     * @param arg0
     */
    public MuliEngineException(Throwable arg0) {
        super(arg0);
    }

}
