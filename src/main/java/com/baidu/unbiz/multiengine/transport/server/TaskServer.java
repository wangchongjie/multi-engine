package com.baidu.unbiz.multiengine.transport.server;

import java.util.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.unbiz.multiengine.transport.client.TaskClientContext;

import io.netty.channel.Channel;

public final class TaskServer extends AbstractTaskServer {

    private static final Log LOG = LogFactory.getLog(TaskServer.class);

    private CountDownLatch initDone = new CountDownLatch(1);

    public void start() {
        final TaskServer server = this;
        new Thread() {
            @Override
            public void run() {
                try {
                    server.doStart();
                } catch (Exception e) {
                    LOG.error("server run fail:", e);
                }
            }
        }.start();
        try {
            initDone.await();
        } catch (InterruptedException e) {
            // do nothing
        }
    }

    public void stop() {
        if (channel == null) {
            return;
        }
        channel.close();
    }

    public void callbackPostInit() {
        initDone.countDown();
    }
}
