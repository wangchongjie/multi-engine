package com.baidu.unbiz.multiengine.vo;

import java.util.Date;

import com.baidu.unbiz.multitask.common.EnableCast;
import com.baidu.unbiz.multitask.task.TaskRequest;

/**
 * 封装前端的查询参数
 *
 * @author wangchongjie
 * @fileName QueryParam.java
 * @since 2014-11-13 下午5:52:36
 */
public class QueryParam extends EnableCast implements TaskRequest {

    // 基础查询相关公共字段
    /** 起始查询日期 */
    protected Date from;
    /** 终止查询日期 */
    protected Date to;
    /** 查询的时间粒度 */
    protected int timeUnit;
    /** 第几页 */
    protected int page;
    /** 每页大小 */
    protected int pageSize = 20;
    /** 前端过滤字符 */
    protected String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public int getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(int timeUnit) {
        this.timeUnit = timeUnit;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }


}
