package com.baidu.unbiz.multiengine.task;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.policy.ExecutePolicy;
import com.baidu.unbiz.multitask.task.Params;

/**
 * Created by wangchongjie on 16/4/5.
 */
public class TaskCommand {

    private Object params;

    private String taskBean;

    private ExecutePolicy policy;

    public TaskCommand() {}

    public TaskCommand(TaskPair taskPair) {
        this.taskBean = taskPair.field1;
        this.params = taskPair.field2;
    }

    public TaskCommand(TaskPair taskPair, ExecutePolicy policy) {
        this(taskPair);
        this.policy = policy;
    }

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

    public ExecutePolicy getPolicy() {
        return policy;
    }

    public void setPolicy(ExecutePolicy policy) {
        this.policy = policy;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
