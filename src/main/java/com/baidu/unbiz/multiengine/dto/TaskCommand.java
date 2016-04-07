package com.baidu.unbiz.multiengine.dto;

import com.baidu.unbiz.multitask.task.Params;

/**
 * Created by wangchongjie on 16/4/5.
 */
public class TaskCommand {

    private Params params;

    private String taskBean;

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public String getTaskBean() {
        return taskBean;
    }

    public void setTaskBean(String taskBean) {
        this.taskBean = taskBean;
    }
}
