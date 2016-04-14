package com.baidu.unbiz.multiengine.common;

import com.baidu.unbiz.multitask.common.TaskPair;

/**
 * Created by wangchongjie on 16/4/14.
 */
public class DisTaskPair extends TaskPair {

    public DisTaskPair(String taskName, Object param) {
        this.field1 = taskName;
        this.field2 = param;
    }
}
