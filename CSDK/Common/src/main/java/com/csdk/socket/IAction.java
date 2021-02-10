package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:37 2020/12/22
 * TODO
 */
interface IAction extends IOAction {
    String ACTION_DATA = "action_data";
    String ACTION_READ_THREAD_START = "action_read_thread_start";
    String ACTION_READ_THREAD_SHUTDOWN = "action_read_thread_shutdown";
    String ACTION_WRITE_THREAD_START = "action_write_thread_start";
    String ACTION_WRITE_THREAD_SHUTDOWN = "action_write_thread_shutdown";
    String ACTION_CONNECTION_SUCCESS = "action_connection_success";
    String ACTION_CONNECTION_FAILED = "action_connection_failed";
    String ACTION_DISCONNECTION = "action_disconnection";
}