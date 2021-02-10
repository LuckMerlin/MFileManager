package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:41 2020/12/22
 * TODO
 */
interface IConfiguration {
    IConnectionManager option(OkSocketOptions var1);

    OkSocketOptions getOption();
}