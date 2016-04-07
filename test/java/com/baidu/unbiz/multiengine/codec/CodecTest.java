package com.baidu.unbiz.multiengine.codec;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.baidu.unbiz.multiengine.codec.impl.ProtostuffCodec;
import com.baidu.unbiz.multiengine.transport.DeviceViewItem;

/**
 * Created by wangchongjie on 16/4/5.
 */
public class CodecTest {

    @Test
    public void testProtostuffCodec(){
        Codec codec = new ProtostuffCodec();

        List<DeviceViewItem> response = mockList();
        byte[] bytes = codec.encode(List.class, response);
        System.out.println(bytes);

        response = codec.decode(List.class, bytes);
        System.out.println(response);
    }


    private List<DeviceViewItem> mockList() {
        List<DeviceViewItem> list = new ArrayList<DeviceViewItem>();
        list.add(new DeviceViewItem());
        list.add(new DeviceViewItem());
        return list;
    }

}
