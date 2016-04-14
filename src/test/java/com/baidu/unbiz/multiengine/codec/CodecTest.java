package com.baidu.unbiz.multiengine.codec;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.baidu.unbiz.multiengine.codec.common.ProtostuffCodec;
import com.baidu.unbiz.multiengine.dto.Signal;
import com.baidu.unbiz.multiengine.vo.DeviceViewItem;

/**
 * Created by wangchongjie on 16/4/5.
 */
public class CodecTest {

    @Test
    public void testProtostuffCodec(){
        MsgCodec codec = new ProtostuffCodec();

        List<DeviceViewItem> dataList = mockList();
        Signal params = new Signal(dataList);

        byte[] bytes = codec.encode(params);
        System.out.println(bytes);

        Signal data = codec.decode(Signal.class, bytes);
        System.out.println(data);
    }


    private List<DeviceViewItem> mockList() {
        List<DeviceViewItem> list = new ArrayList<DeviceViewItem>();
        list.add(new DeviceViewItem());
        list.add(new DeviceViewItem());
        return list;
    }

}
