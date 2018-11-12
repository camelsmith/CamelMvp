package com.camel.mvpdemo.activity.base;

import com.camel.mvp.activity.BaseActivity;
import com.camel.mvp.base.MvpBaseInterface;
import com.camel.mvp.base.MvpBasePresenter;

/**
 * Created by CamelLuo on 2018/10/19.
 * 实现了MVP架构 的 Activity 应用层抽象基类
 * 请在该类中实现与项目相关的公用方法、流程
 */
public abstract class AppBaseActivity<V extends MvpBaseInterface, P extends MvpBasePresenter<V>> extends BaseActivity<V, P> {

}
