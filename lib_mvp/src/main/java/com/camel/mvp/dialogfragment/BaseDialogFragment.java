package com.camel.mvp.dialogfragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.camel.mvp.base.MvpBaseDialogFragment;
import com.camel.mvp.base.MvpBaseFragment;
import com.camel.mvp.base.MvpBaseInterface;
import com.camel.mvp.base.MvpBasePresenter;
import com.camel.mvp.fragment.BaseFragmentManager;
import com.camel.mvp.fragment.FragmentSupport;
import com.camel.mvp.util.ToastUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by CamelLuo on 2018/10/17.
 */
public abstract class BaseDialogFragment<V extends MvpBaseInterface, P extends MvpBasePresenter<V>>
        extends MvpBaseDialogFragment implements MvpBaseInterface{

    /**
     * DialogFragment的根布局View
     */
    protected View mRoot;
    /**
     * ButterKnife对象
     */
    private Unbinder unbinder;
    /**
     * Activity上下文对象
     */
    private Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot != null) {
            ViewGroup parent = (ViewGroup) mRoot.getParent();
            if (parent != null) {
                parent.removeView(mRoot);
            }
        } else {
            //获取传值进来的bundle
            Bundle bundle = getArguments();
            if (bundle != null) {
                getBundleValues(bundle);
            }
            //绑定Xml布局文件
            if (setContentViewId() != 0) {
                mRoot = inflater.inflate(setContentViewId(), container, false);
                //初始化ButterKnife
                unbinder = ButterKnife.bind(this, mRoot);
                //初始化View
                initView(mRoot, savedInstanceState);
            }
        }
        return mRoot;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    ////////////////////////////////////////////////////公用方法相关//////////////////////////////////////////////////////////
    /**
     * 定义一个方法用于进行跳转页面
     * @param cls 所跳转的目的Activity类的class类
     */
    public void startActivity(Class<?> cls) {
        startActivity(new Intent(mActivity, cls));
    }

    /**
     * 定义一个方法用于进行跳转页面，并附带Bundle
     * @param cls 所跳转的目的Activity类的class类
     * @param bundle 跳转所携带的信息(Bundle对象)
     */
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(mActivity, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 定义一个方法用于进行跳转页面，并附带请求码
     * @param cls 所跳转的目的Activity类的class类
     * @param requestCode 请求码
     */
    public void startActivityForResult(Class<?> cls, int requestCode) {
        startActivityForResult(cls, null, requestCode);
    }

    /**
     * 定义一个方法用于进行跳转页面，并附带Bundle和请求码
     * @param cls 所跳转的目的Activity类的class类
     * @param bundle 跳转所携带的信息(Bundle对象)
     * @param requestCode 请求码
     */
    public void startActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(mActivity, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void showMsg(String msg) {
        ToastUtils.showText(mActivity, msg);
    }

    @Override
    public void showMsg(int strId) {
        ToastUtils.showText(mActivity, strId);
    }

    @Override
    public String getStrById(int strId) {
        return getString(strId);
    }

    ////////////////////////////////////////////////////公用方法相关//////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////抽象方法及空方法重载相关//////////////////////////////////////////////////////////
    /**
     * 定义一个方法用于获取创建Fragment传进来的Bundle
     */
    protected abstract void getBundleValues(Bundle bundle);

    /**
     * 定义一个抽象方法用于设置xml布局文件的id
     *
     * @return
     */
    protected abstract int setContentViewId();

    /**
     * 定义一个抽象方法用于让子类在该方法中实现 初始化view等工作
     *
     * @param view
     */
    protected abstract void initView(View view, Bundle savedInstanceState);

    ////////////////////////////////////////////////////抽象方法及空方法重载相关//////////////////////////////////////////////////////////
}
