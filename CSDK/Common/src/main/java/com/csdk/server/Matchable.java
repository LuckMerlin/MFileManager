package com.csdk.server;

/**
 * Create LuckMerlin
 * Date 14:10 2020/9/7
 * TODO
 */
public interface Matchable<T> {
    int MATCHED=-2001;
    int CONTINUE=-2002;
    int BREAK=-2003;
    Integer onMatch(T arg);
}
