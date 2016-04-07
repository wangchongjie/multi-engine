package com.baidu.unbiz.multiengine.dto;

import com.baidu.unbiz.multitask.task.Params;

/**
 * Created by wangchongjie on 16/4/5.
 */
public class TaskCommand {

    private Object params;

    private String taskBean;

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

    public String getTaskBean() {
        return taskBean;
    }

    public void setTaskBean(String taskBean) {
        this.taskBean = taskBean;
    }

}
