package com.camel.mvp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.camel.mvp.R;
import com.camel.mvp.base.MvpBaseFragment;
import com.camel.mvp.base.MvpBaseInterface;
import com.camel.mvp.base.MvpBasePresenter;
import com.camel.mvp.util.ToastUtils;
import com.orhanobut.logger.Logger;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by CamelLuo on 2018/10/17.
 * 实现了MVP架构 的 Fragment抽象基类 主要实现、封装各种流程
 */
public abstract class BaseFragment<V extends MvpBaseInterface, P extends MvpBasePresenter<V>>
        extends MvpBaseFragment implements MvpBaseInterface {

    /**
     * Fragment内部的子Fragment管理类
     */
    protected BaseFragmentManager mChildBaseFragmentManager;
    /**
     * Fragment的根布局View
     */
    protected View mRoot;
    /**
     * ButterKnife对象
     */
    private Unbinder unbinder;
    /**
     * Activity上下文对象
     */
    protected AppCompatActivity mActivity;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) context;
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
        onFragmentResumed();
    }

    @Override
    public void onPause() {
        super.onPause();
        onFragmentPaused();
    }

    @Override
    public void onStop() {
        super.onStop();
        onFragmentStop();
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
     *
     * @param cls 所跳转的目的Activity类的class类
     */
    public void startActivity(Class<?> cls) {
        startActivity(new Intent(mActivity, cls));
    }

    /**
     * 定义一个方法用于进行跳转页面，并附带Bundle
     *
     * @param cls    所跳转的目的Activity类的class类
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
     *
     * @param cls         所跳转的目的Activity类的class类
     * @param requestCode 请求码
     */
    public void startActivityForResult(Class<?> cls, int requestCode) {
        startActivityForResult(cls, null, requestCode);
    }

    /**
     * 定义一个方法用于进行跳转页面，并附带Bundle和请求码
     *
     * @param cls         所跳转的目的Activity类的class类
     * @param bundle      跳转所携带的信息(Bundle对象)
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

    /**
     * 定义一个方法用于简化findViewById操作
     * @param resId 要绑定的控件资源id
     * @param <E> 控件View的对象类型
     * @return
     */
    public <E extends View> E findViewById(int resId) {
        return (E) mRoot.findViewById(resId);
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

    /**
     * 定义一个方法用于打印debug调试Log信息
     * 是否真正打印出来需要继承子类通过覆写 isShowDebugLog() 方法来决定，默认为不打印
     */
    public void debugLog(String content){
        if (isShowDebugLog()){
            Logger.t(getAtyClassName()).e(content);
        }
    }


    /**
     * 定义一个方法用于获取该Activity的Class名称
     * @return Activity的Class名称
     */
    private String getAtyClassName(){
        return this.getClass().getSimpleName();
    }

    ////////////////////////////////////////////////////公用方法相关//////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////Fragment相关//////////////////////////////////////////////////////////

    /**
     * 定义一个方法用于获取Fragment内部的FragmentManager管理类
     *
     * @return
     */
    public BaseFragmentManager getBaseChildFragmentManager() {
        if (mChildBaseFragmentManager != null) {
            return mChildBaseFragmentManager;
        } else {
            return null;
        }
    }

    /**
     * 定义一个方法用于初始化Fragment内部的FragmentManager管理类
     * 如需在Fragment中再次嵌套使用Fragment，并且加载的子Fragment所依赖的为同一个layout布局，则请调用该方法
     *
     * @param manager              Fragment管理类，请调用 getChildFragmentManager() 来获取
     * @param fragmentRootLayoutId 加载Fragment时所依赖的layout布局
     * @param listener             Fragment页面变化监听器，如不需要则置null
     */
    protected void initBaseChildFragmentManager(FragmentManager manager, int fragmentRootLayoutId,
                                                BaseFragmentManager.OnFragmentChangedListener listener) {
        if (listener == null) {
            mChildBaseFragmentManager = new BaseFragmentManager(manager, fragmentRootLayoutId);
        } else {
            mChildBaseFragmentManager = new BaseFragmentManager(manager, fragmentRootLayoutId, listener);
        }
    }

    /**
     * 定义一个方法用于通知加载该Fragment的Activity页面当前Fragment正在执行onResume() 方法
     */
    public void onFragmentResumed() {
        ((FragmentSupport) mActivity).onFragmentResumed();
    }

    /**
     * 定义一个方法用于通知加载该Fragment的Activity页面当前Fragment正在执行onPause() 方法
     */
    protected void onFragmentPaused() {
        ((FragmentSupport) mActivity).onFragmentPaused();
    }

    /**
     * 定义一个方法用于通知加载该Fragment的Activity页面当前Fragment正在执行onStop() 方法
     */
    protected void onFragmentStop() {
        ((FragmentSupport) mActivity).onFragmentStop();
    }

    ////////////////////////////////////////////////////Fragment相关//////////////////////////////////////////////////////////


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

    /**
     * 定义一个方法用于开启或关闭该页面的debug调试Log打印信息
     * @return 继承子类不覆写的话默认返回false，如需要打印debug调试Log打印信息，请覆写该方法并返回true
     */
    protected boolean isShowDebugLog() {
        return false;
    }


    ////////////////////////////////////////////////////抽象方法及空方法重载相关//////////////////////////////////////////////////////////
}
