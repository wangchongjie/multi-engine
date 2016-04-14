package com.baidu.unbiz.multiengine.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.springframework.stereotype.Component;

import com.baidu.unbiz.multiengine.common.DisTaskPair;
import com.baidu.unbiz.multiengine.dto.TaskCommand;
import com.baidu.unbiz.multiengine.transport.client.SendFuture;
import com.baidu.unbiz.multiengine.transport.client.TaskClient;
import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.policy.ExecutePolicy;
import com.baidu.unbiz.multitask.task.SimpleParallelExePool;
import com.baidu.unbiz.multitask.task.thread.MultiResult;
import com.baidu.unbiz.multitask.task.thread.TaskContext;
import com.baidu.unbiz.multitask.task.thread.TaskManager;
import com.baidu.unbiz.multitask.task.thread.TaskWrapper;
import com.baidu.unbiz.multitask.task.thread.WorkUnit;

/**
 * Created by wangchongjie on 16/4/14.
 */
@Component
public class DistributedParallelExePool extends SimpleParallelExePool {

    private TaskClient taskClient;

    public MultiResult submit(Executor executor, ExecutePolicy policy, TaskPair... taskPairs) {
        List<TaskPair> localTaskPairs = new ArrayList<TaskPair>();
        List<TaskPair> disTaskPairs = new ArrayList<TaskPair>();

        for (TaskPair taskPair : taskPairs) {
            if (taskPair instanceof DisTaskPair) {
                disTaskPairs.add(taskPair);
            } else {
                localTaskPairs.add(taskPair);
            }
        }

        TaskContext context = TaskContext.newContext();
        List<TaskWrapper> fetchers = TaskWrapper.wrapperFetcher(container, context,
                (TaskPair[]) localTaskPairs.toArray(new TaskPair[]{}));

        WorkUnit workUnit = TaskManager.newWorkUnit(executor);
        context.copyAttachedthreadLocalValues();

        for (TaskWrapper fetcher : fetchers) {
            workUnit.submit(fetcher);
        }

        Map<String, SendFuture> futures = new HashMap<String, SendFuture>();

        for (TaskPair taskPair : disTaskPairs) {
            TaskCommand command = new TaskCommand(taskPair);
            futures.put(taskPair.field1 , taskClient.asynCall(command));
        }

        workUnit.waitForCompletion(policy.taskTimeout());

        for(Map.Entry<String, SendFuture> future : futures.entrySet()) {
            context.putResult(future.getKey(), future.getValue().get());
        }
        return context;
    }

    public TaskClient getTaskClient() {
        return taskClient;
    }

    public void setTaskClient(TaskClient taskClient) {
        this.taskClient = taskClient;
    }
}
