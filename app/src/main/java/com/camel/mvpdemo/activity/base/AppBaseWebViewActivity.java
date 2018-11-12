package com.camel.mvpdemo.activity.base;


import android.os.Bundle;

import com.camel.mvp.activity.BaseActivity;
import com.camel.mvp.activity.BaseWebViewActivity;
import com.camel.mvp.base.MvpBaseInterface;
import com.camel.mvp.base.MvpBasePresenter;
import com.camel.mvpdemo.presenter.EmptyPresenter;
import com.camel.mvpdemo.viewinterface.EmptyInterface;
import com.orhanobut.logger.Logger;

/**
 * Created by CamelLuo on 2018/10/26.
 */
public abstract class AppBaseWebViewActivity<V extends MvpBaseInterface, P extends MvpBasePresenter<V>> extends BaseWebViewActivity<V, P> {

}
