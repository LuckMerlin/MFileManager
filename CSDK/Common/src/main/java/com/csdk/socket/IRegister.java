package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:44 2020/12/22
 * TODO
 */
interface IRegister <T, E> {
    /**
     * 注册一个回调接收器
     *
     * @param socketActionListener 回调接收器
     */
    E registerReceiver(T socketActionListener);

    /**
     * 解除回调接收器
     *
     * @param socketActionListener 注册时的接收器,需要解除的接收器
     */
    E unRegisterReceiver(T socketActionListener);
}
