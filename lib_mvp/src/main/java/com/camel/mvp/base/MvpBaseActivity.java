package com.camel.mvp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

/**
 * Created by CamelLuo on 2018/10/17.
 * MVP架构 MVP Activity基类，实现了MVP架构所需内容,并实现RxLifecycle生命周期管理
 */
public abstract class MvpBaseActivity<V extends MvpBaseInterface, P extends MvpBasePresenter<V>> extends RxAppCompatActivity {

    /**
     * Presenter层对象
     */
    protected P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 创建Presenter 层实例
        mPresenter = createPresenter();
        // 绑定Presenter层与View层，将其关联起来
        mPresenter.attachView((V) this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 解绑Presenter层与View层，取消关联
        mPresenter.detachView();
    }

    ////////////////////////////////////////////////////抽象方法相关/////////////////////////////////////////////////////////////////////

    /**
     * 定义一个方法用于让继承子类实现Presenter层实例的创建
     * @return
     */
    protected abstract P createPresenter();

    ////////////////////////////////////////////////////抽象方法相关/////////////////////////////////////////////////////////////////////
}
