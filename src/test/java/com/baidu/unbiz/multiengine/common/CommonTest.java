package com.baidu.unbiz.multiengine.common;

import java.net.InetAddress;

import org.junit.Test;

/**
 * Created by wangchongjie on 16/4/18.
 */
public class CommonTest {

    @Test
    public void test() {
        // TODO Auto-generated method stub
        InetAddress ia = null;
        try {
            ia = ia.getLocalHost();

            String localname = ia.getHostName();
            String localip = ia.getHostAddress();
            System.out.println("本机名称是：" + localname);
            System.out.println("本机的ip是 ：" + localip);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
