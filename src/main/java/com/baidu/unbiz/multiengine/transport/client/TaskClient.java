package com.baidu.unbiz.multiengine.transport.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.unbiz.multiengine.exception.MultiEngineException;
import com.baidu.unbiz.multiengine.task.TaskCommand;
import com.baidu.unbiz.multiengine.transport.dto.Signal;

import io.netty.channel.Channel;

public final class TaskClient extends AbstractTaskClient {

    private static final Log LOG = LogFactory.getLog(TaskClient.class);

    private CountDownLatch initDone = new CountDownLatch(1);
    private AtomicBoolean success = new AtomicBoolean(true);

    public void stop() {
        Channel channel = TaskClientContext.sessionChannelMap.get(sessionKey);
        if (channel == null) {
            return;
        }
        channel.close();
    }

    public boolean start() {
        final AbstractTaskClient client = this;
        new Thread() {
            @Override
            public void run() {
                try {
                    client.doStart();
                } catch (Exception e) {
                    LOG.error("client start fail:", e);
                }
            }
        }.start();

        try {
            initDone.await();
        } catch (InterruptedException e) {
            LOG.error("client await fail:" + client.getHostConf());
            success.set(false);
        }
        return success.get();
    }

    public void callbackOnException(Exception e) {
        success.set(false);
        initDone.countDown();
    }

    public void callbackPostInit() {
        initDone.countDown();
    }

    public <T> T call(TaskCommand command) {
        SendFuture future = asynCall(command);
        if (future instanceof IdentitySendFuture) {
            return waitResult(((IdentitySendFuture) future).getId());
        }
        throw new MultiEngineException("support IdentitySendFuture only");
    }

    public SendFuture asynCall(TaskCommand command) {
        long seqId = idGen.genId();

        Signal<TaskCommand> signal = new Signal<TaskCommand>(command);
        signal.setSeqId(seqId);

        Channel channel = TaskClientContext.sessionChannelMap.get(sessionKey);
        channel.writeAndFlush(signal);

        SendFuture sendFutrue = new IdentitySendFuture(seqId);

        TaskClientContext.placeSessionResult(sessionKey, seqId, sendFutrue);
        return sendFutrue;
    }

    private <T> T waitResult(long seqId) {
        SendFuture sendFutrue = TaskClientContext.getSessionResult(sessionKey, seqId);
        T result = (T) sendFutrue.get();
        TaskClientContext.removeSessionResult(sessionKey, seqId);
        return result;
    }
}
