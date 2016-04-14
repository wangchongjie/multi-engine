package com.baidu.unbiz.multiengine.transport.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class TaskServer extends AbstractTaskServer {

    private static final Log LOG = LogFactory.getLog(TaskServer.class);

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
    }
}
