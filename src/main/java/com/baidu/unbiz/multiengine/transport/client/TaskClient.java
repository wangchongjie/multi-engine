package com.baidu.unbiz.multiengine.transport.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;

import com.baidu.unbiz.multiengine.endpoint.HostConf;
import com.baidu.unbiz.multiengine.endpoint.gossip.GossipSync;
import com.baidu.unbiz.multiengine.endpoint.heartbeat.HeartbeatInfo;
import com.baidu.unbiz.multiengine.task.TaskCommand;
import com.baidu.unbiz.multiengine.transport.dto.Signal;
import com.baidu.unbiz.multiengine.transport.dto.SignalType;
import com.baidu.unbiz.multitask.log.AopLogFactory;

import io.netty.channel.Channel;

public final class TaskClient extends AbstractTaskClient {

    private static final Logger LOG = AopLogFactory.getLogger(TaskClient.class);

    private CountDownLatch initDone = new CountDownLatch(1);
    private AtomicBoolean initSuccess = new AtomicBoolean(true);
    private AtomicBoolean invalid = new AtomicBoolean(false);

    public <T> T call(TaskCommand command) {
        return syncSend(command);
    }

    public SendFuture asyncCall(TaskCommand request) {
        return asyncSend(request);
    }

    public boolean heartBeat(HeartbeatInfo hbi) {
        Signal<HeartbeatInfo> signal = new Signal<HeartbeatInfo>();
        signal.setType(SignalType.HEART_BEAT);
        try {
            syncSend(signal, hbi.getTimeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            return false;
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    public void syncGossip() {
        Signal<GossipSync> signal = new Signal<GossipSync>();
        signal.setType(SignalType.GOSSIP_SYNC);
        try {
            onewaySend(signal);
        } catch (Exception e) {
            LOG.warn("sync gossip:", e);
        }
    }

    public boolean restart() {
        this.stop();
        initDone = new CountDownLatch(1);
        initSuccess.set(false);
        return start();
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
            initSuccess.set(false);
        }
        return initSuccess.get();
    }

    public void stop() {
        Channel channel = TaskClientContext.sessionChannelMap.get(sessionKey);
        if (channel == null) {
            return;
        }
        channel.close();
    }

    public void callbackOnException(Exception e) {
        LOG.error("client start fail:", e);
        initSuccess.set(false);
        initDone.countDown();
    }

    public TaskClient() {}

    public TaskClient(HostConf hostConf) {
        this.hostConf = hostConf;
    }

    public void callbackPostInit() {
        initDone.countDown();
    }

    public AtomicBoolean getInvalid() {
        return invalid;
    }

    public boolean isInvalid() {
        return invalid.get();
    }

    public void setInvalid(AtomicBoolean invalid) {
        this.invalid = invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid.set(invalid);
    }
}
