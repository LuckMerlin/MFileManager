package com.csdk.api.core;

/**
 * Create LuckMerlin
 * Date 16:37 2020/8/5
 * TODO
 */
public interface Code {
    int CODE_SUCCEED = 2000;   // 成功
    int CODE_FAIL = 2001;  // 失败
    int CODE_PARAMS_INVALID=2002; // 参数错误
    int CODE_SIGN_INVALID=2003;//签名验证错误

    int CODE_SYSTEM_ERROR = 100;  // 系统错误
    int CODE_NOT_FRIEND = 101; // 不是好友
    int CODE_BLOCKED = 102; // 被拉黑
    int CODE_NOT_JOIN_GROUP= 103; //未加入群
    int CODE_GROUP_BLOCKED = 104; // 被群拉黑
    int CODE_GROUP_JOIN_FORBID = 105; // 禁止加入
    int CODE_REQUEST_EXPIRED = 106; // 请求已过期
    int CODE_NONE_ACCESS = 107; // 未授权访问
    int CODE_USER_IN_TEAM = 108; // 已经在其他群组了
    int CODE_ALREADY_FRIEND = 109; // 已经是好友了
    int CODE_EXCEED_UPPER_LIMITED = 110; // 已经达到上限
    int CODE_MESSAGE_SEND_FREQUENTLY= 111; // 发送频率太快
    int CODE_MESSAGE_SEND_DISABLED = 112; // 已被禁言
    int CODE_NOT_FOUND = 404; // 不存在
//
    int CODE_NONE_INITIAL = CODE_FAIL;  // 未初始化
    int CODE_EXCEPTION = CODE_SYSTEM_ERROR;  //异常
    int CODE_ALREADY_DONE = CODE_SUCCEED;  //已经完成
    int CODE_NONE_LOGIN = CODE_FAIL;  //未登录

}
