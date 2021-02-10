package com.csdk.api.core;

/**
 * Create LuckMerlin
 * Date 18:15 2020/8/20
 * TODO
 */
public interface Status {
    /**
     * 未知状态
     */
    int STATUS_NONE=-2000;
    /**
     * 操作正在进行中
     */
    int STATUS_DOING=-2001;
    /**
     * 操作成功
     */
    int STATUS_SUCCEED=-2002;
    /**
     * 操作失败
     */
    int STATUS_FAIL=-2003;
    /**
     * 准备中
     */
    int STATUS_PENDING=-2004;
    /**
     * 操作取消
     */
    int STATUS_CANCEL=-2005;
    /**
     * 设置清空消息未读计数
     */
    int STATUS_UNREAD=-2006;
    /**
     * 上传中
     */
    int STATUS_UPLOADING=-2007;
    /**
     * 未初始化
     */
    int STATUS_NONE_INITIAL=-2008;
    /**
     * 操作错误
     */
    int STATUS_ERROR=-2009;
    /**
     * 参数不合法
     */
    int STATUS_ARGS_INVALID=-2010;
    /**
     * 已经完成了(常用于标记某种操作已经执行过了)
     */
    int STATUS_ALREADY_DONE=-2011;
}
