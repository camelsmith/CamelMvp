package com.camel.mvpdemo.fragment;

import android.os.Bundle;
import android.view.View;

import com.camel.mvp.base.MvpBasePresenter;
import com.camel.mvpdemo.R;
import com.camel.mvpdemo.fragment.base.AppBaseFragment;
import com.camel.mvpdemo.presenter.EmptyPresenter;
import com.camel.mvpdemo.viewinterface.EmptyInterface;

/**
 * Created by CamelLuo on 2018/10/25.
 */
public class AtyFragment extends AppBaseFragment<EmptyInterface, EmptyPresenter> implements EmptyInterface {

    @Override
    protected void getBundleValues(Bundle bundle) {

    }

    @Override
    protected int setContentViewId() {
        return R.layout.fragment_aty;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

    }

    @Override
    protected MvpBasePresenter createPresenter() {
        return new EmptyPresenter();
    }

    /**
     * 实例化Fragment的入口，可以携带参数传入
     * @return
     */
    public static AtyFragment newInstance() {
        AtyFragment fragment = new AtyFragment();
        return fragment;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}
