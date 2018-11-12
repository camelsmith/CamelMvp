package com.camel.mvp.base;

/**
 * Created by CamelLuo on 2018/10/17.
 * <br/>
 * Mvp View层 基类接口
 */
public interface MvpBaseInterface {

    /**
     * 显示进度条
     */
    void showLoading();

    /**
     * 隐藏进度条
     */
    void hideLoading();

    /**
     * 显示提示文本（ToastUtils、Snack）
     *
     * @param msg 提示文本
     */
    void showMsg(String msg);

    /**
     * 显示提示文本（Toast、Snack）
     *
     * @param strId 提示文本id
     */
    void showMsg(int strId);

    /**
     * 通过resId获取String
     *
     * @param strId
     */
    String getStrById(int strId);

}
