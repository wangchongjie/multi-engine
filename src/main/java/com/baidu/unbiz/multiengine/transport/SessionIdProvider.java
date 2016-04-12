package com.baidu.unbiz.multiengine.transport;

/**
 * session id 提供者。
 * <p>
 * 在实际的应用中，在使用一个sessionid一段时间后，比如超过2个星期，会出现连接被断开，
 * 并且重复连接依然失败的现象，但重启系统后能连接成功，考虑增加一种策略，在出现这种情况后，
 * 试着使用新的sessionid 进行连接。
 * </p>
 * 
 * @author wangchongjie
 *
 */
public interface SessionIdProvider {
    /**
     * 获取sessionid
     *
     * @param refresh 是否刷新新session id
     * @return session id
     */
    String getSessionId(boolean refresh);

}
