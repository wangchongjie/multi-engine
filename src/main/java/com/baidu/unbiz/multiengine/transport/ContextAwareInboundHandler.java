package com.baidu.unbiz.multiengine.transport;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by wangchongjie on 16/4/10.
 */
@Component
public class ContextAwareInboundHandler extends ChannelInboundHandlerAdapter implements ApplicationContextAware {

    // Spring应用上下文环境
    protected static ApplicationContext applicationContext;

    protected <T> T bean(String beanName) throws BeansException {
        return (T) applicationContext.getBean(beanName);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        ContextAwareInboundHandler.applicationContext = applicationContext;
    }

}
