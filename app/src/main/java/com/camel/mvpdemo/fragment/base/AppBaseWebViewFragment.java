package com.camel.mvpdemo.fragment.base;

import com.camel.mvp.base.MvpBaseInterface;
import com.camel.mvp.base.MvpBasePresenter;
import com.camel.mvp.fragment.BaseWebViewFragment;

/**
 * Created by CamelLuo on 2018/11/2.
 */
public abstract class AppBaseWebViewFragment<V extends MvpBaseInterface, P extends MvpBasePresenter<V>> extends BaseWebViewFragment<V, P> {
}
