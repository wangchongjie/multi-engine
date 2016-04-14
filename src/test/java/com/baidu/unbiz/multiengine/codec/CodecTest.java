package com.baidu.unbiz.multiengine.codec;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.baidu.unbiz.multiengine.codec.common.ProtostuffCodec;
import com.baidu.unbiz.multiengine.dto.RpcParam;
import com.baidu.unbiz.multiengine.vo.DeviceViewItem;

/**
 * Created by wangchongjie on 16/4/5.
 */
public class CodecTest {

    @Test
    public void testProtostuffCodec(){
        MsgCodec codec = new ProtostuffCodec();

        List<DeviceViewItem> dataList = mockList();

        RpcParam params = RpcParam.newInstance().setParams(dataList);

        byte[] bytes = codec.encode(params);
        System.out.println(bytes);

        RpcParam data = codec.decode(RpcParam.class, bytes);
        System.out.println(data.getParams());
    }


    private List<DeviceViewItem> mockList() {
        List<DeviceViewItem> list = new ArrayList<DeviceViewItem>();
        list.add(new DeviceViewItem());
        list.add(new DeviceViewItem());
        return list;
    }

}
