package com.camel.mvp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.trello.rxlifecycle2.components.support.RxDialogFragment;


/**
 * Created by CamelLuo on 2018/10/17.
 * MVP架构 MVP DialogFragment 基类，实现了MVP架构所需内容,并实现RxLifecycle生命周期管理
 */
public abstract class MvpBaseDialogFragment<V extends MvpBaseInterface, P extends MvpBasePresenter<V>> extends RxDialogFragment {

    /**
     * Presenter层对象
     */
    protected P mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 创建Presenter 层实例
        mPresenter = createPresenter();
        // 绑定Presenter层与View层，将其关联起来
        mPresenter.attachView((V) this);
    }

    @Override
    public void onDestroy() {
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
