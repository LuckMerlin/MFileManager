package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:43 2020/12/22
 * TODO
 */
interface ISender<T> {
        /**
         * 在当前的连接上发送数据
         *
         * @param sendable 具有发送能力的Bean {@link ISendable}
         * @return T
         */
        T send(ISendable sendable);
}