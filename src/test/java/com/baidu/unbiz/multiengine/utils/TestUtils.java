package com.baidu.unbiz.multiengine.utils;

/**
 * Created by baidu on 16/4/18.
 */
public class TestUtils {
    public static final long VERY_LONG_TIME = 2000 * 1000;

    public static void dumySleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // do nothing
        }
    }
}
