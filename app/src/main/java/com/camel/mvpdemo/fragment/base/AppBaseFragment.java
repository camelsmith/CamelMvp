package com.camel.mvpdemo.fragment.base;

import com.camel.mvp.base.MvpBaseInterface;
import com.camel.mvp.base.MvpBasePresenter;
import com.camel.mvp.fragment.BaseFragment;

/**
 * Created by CamelLuo on 2018/10/25.
 */
public abstract class AppBaseFragment<V extends MvpBaseInterface, P extends MvpBasePresenter<V>> extends BaseFragment<V, P> {
}
