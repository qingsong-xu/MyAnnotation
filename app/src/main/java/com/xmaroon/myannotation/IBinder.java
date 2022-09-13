package com.xmaroon.myannotation;

/**
 * @author xuqingsong
 * @date 2022/9/13
 * @desc
 **/
public interface IBinder<T> {
    /**
     * bind view
     * @param activity
     */
    void bind(T activity);
}
