package com.camel.mvp.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.camel.mvp.R;
import com.camel.mvp.base.MvpBaseInterface;
import com.camel.mvp.base.MvpBasePresenter;
import com.camel.mvp.util.ScreenUtil;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by CamelLuo on 2018/10/19.
 * 实现了MVP架构 的 附带Toolbar的 Fragment抽象基类 主要实现、封装各种流程
 */
public abstract class BaseToolbarFragment<V extends MvpBaseInterface, P extends MvpBasePresenter<V>>
        extends BaseFragment {
    private static final String TAG = BaseToolbarFragment.class.getSimpleName();
    protected Toolbar mToolbar;
    protected TextView mToolbarTittle;
    protected View mToolbarBackBtnView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        //让附带Toolbar的Fragment实现子类在该方法中初始化View
        initViewAndToolbar(view,savedInstanceState);
        //设置沉浸式Toolbar
        if (isNeedToolbar()){
            //设置使用沉浸式Toolbar
            setImmsersionToolbar(true);
            //设置使用透明状态栏
            setTransparentStatusBar(true);
            //设置是否使用黑色字体状态栏
            setStatusBarFontDark(isNeedStatusBarFontDark());
            //设置Fragment使用Toolbar作为ActionBar
            if (mToolbar != null){
                mActivity.setSupportActionBar(mToolbar);
            }
            //设置返回键监听事件
            if (mToolbarBackBtnView != null){
                mToolbarBackBtnView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doOnToolbarBackClick(v);
                    }
                });
            }
        }
    }

    ////////////////////////////////////////////////////Toolbar相关///////////////////////////////////////////////////////////////////////
    /**
     * 定义一个方法用于初始化Toolbar，如继承子类在isNeedToolbar() 方法中设置为 true
     * 则必须在 继承子类的布局文件中 include 一个含有Toolbar的layout进来
     * 则必须在 继承子类的onCreate() 方法中调用 initToolBar() 来完成Toolbar的初始化工作
     *
     * @param isNeedBack 是否需要返回键
     * @param titleId    Toolbar的标题，如不需要则置null
     */
    protected void initToolBar(boolean isNeedBack, int titleId) {
        initToolBar(isNeedBack, getResources().getString(titleId));
    }

    /**
     * 定义一个方法用于初始化Toolbar，如继承子类在isNeedToolbar() 方法中设置为 true
     * 则必须在 继承子类的布局文件中 include 一个含有Toolbar的layout进来
     * 则必须在 继承子类的onCreate() 方法中调用 initToolBar() 来完成Toolbar的初始化工作
     *
     * @param isNeedBack 是否需要返回键
     * @param title      Toolbar的标题，如不需要则置 ""
     */
    protected void initToolBar(boolean isNeedBack, String title) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_id);
        //设置Toolbar的标题
        mToolbarTittle = (TextView) findViewById(R.id.toolbar_tittle);
        //绑定返回按钮
        mToolbarBackBtnView = findViewById(R.id.toolbar_back_button);
        mToolbar.setTitle("");
        if (title != null) {
            if (mToolbarTittle != null) {
                mToolbarTittle.setText(title);
            }
        }

        //设置允许Fragment使用Toolbar的菜单
        setHasOptionsMenu(true);
        if (isNeedBack) {
//            ActionBar actionBar = getSupportActionBar();
//            if (actionBar != null) {
//                // 设置ToolBar左侧按键可用
//                actionBar.setHomeButtonEnabled(true);
//                // 为ToolBar左侧按键画一个返回图标
//                actionBar.setDisplayHomeAsUpEnabled(true);
//            }
            mToolbarBackBtnView.setVisibility(View.VISIBLE);
        } else {
//            ActionBar actionBar = getSupportActionBar();
//            if (actionBar != null) {
//                // 设置ToolBar左侧按键可用
//                actionBar.setHomeButtonEnabled(false);
//                //为ToolBar左侧按键画一个返回图标
//                actionBar.setDisplayHomeAsUpEnabled(false);
//            }
            mToolbarBackBtnView.setVisibility(View.GONE);
        }
    }

    /**
     * 定义一个方法用于将Toolbar设置为沉浸式模式，Android 4.4版本以上有效
     * @param isUse true:使用,false:不使用
     */
    public void setImmsersionToolbar(boolean isUse) {
        if (mToolbar != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                int paddingTop = mToolbar.getPaddingTop();
                int paddingBottom = mToolbar.getPaddingBottom();
                int paddingLeft = mToolbar.getPaddingLeft();
                int paddingRight = mToolbar.getPaddingRight();
                int statusHeight = ScreenUtil.getStatusHeight(mActivity);
                Logger.t(TAG).e("------------------------获取到的状态栏高度为：  " + statusHeight);
                ViewGroup.LayoutParams params = mToolbar.getLayoutParams();
                int height = params.height;
                /**
                 * 利用状态栏的高度，4.4及以上版本给Toolbar设置一个paddingTop值为status_bar的高度，
                 * Toolbar延伸到status_bar顶部
                 **/
                if (isUse) {
                    paddingTop += statusHeight;
                    height += statusHeight;
                } else {
                    paddingTop -= statusHeight;
                    height -= statusHeight;
                }
                params.height = height;
                mToolbar.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                mToolbar.setVisibility(isUse ? View.VISIBLE : View.GONE);
            }
        }
    }

    /**
     * 定义一个方法用于设置透明状态栏
     * 对Android 4.4及以上版本有效
     * @param isUse
     */
    private void setTransparentStatusBar(boolean isUse) {
        if (isUse){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window window = mActivity.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.setStatusBarColor(Color.TRANSPARENT);
                }
            }
        }
    }

    /**
     * 定义一个方法用于设置Android状态栏的字体颜色，状态栏为亮色（白色）的时候字体和图标是黑色，
     * 状态栏为暗色（不和白色冲突的颜色）的时候字体和图标为白色
     *  该方法目前只对小米、魅族以及Android 6.0以上设备有效
     * @param dark 状态栏字体和图标是否为深色
     */
    protected void setStatusBarFontDark(boolean dark) {
        // 小米MIUI
        try {
            Window window = mActivity.getWindow();
            Class clazz = mActivity.getWindow().getClass();
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            if (dark) {    //状态栏亮色且黑色字体
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
            } else {       //清除黑色字体
                extraFlagField.invoke(window, 0, darkModeFlag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 魅族FlymeUI
        try {
            Window window = mActivity.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (dark) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // android6.0+系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (dark) {
                mActivity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }
    ////////////////////////////////////////////////////Toolbar相关///////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////抽象方法及空方法重载相关//////////////////////////////////////////////////////////
    /**
     * 定义一个抽象方法用于让子类在该方法中实现 初始化view等工作
     * 继承BaseToolbarFragment的子类专用
     * 建议使用该方法进行控件、Toolbar的初始化工作，上一层BaseFragment抽象类的 initView() 方法建议不要使用
     *
     * @param view
     */
    protected abstract void initViewAndToolbar(View view, Bundle savedInstanceState);

    /**
     * 定义一个方法用于设置是否需要Toolbar，即使设置为true，也要手动在 initView() 方法中调用initToolBar() 系列方法传入标题等信息
     * @return
     */
    protected abstract boolean isNeedToolbar();

    /**
     * 该方法只在 isNeedToolbar() 返回为 true时 才执行判断
     * 定义一个方法用于设置Android状态栏的字体颜色，状态栏为亮色（白色）的时候字体和图标是黑色，
     * 状态栏为暗色（不和白色冲突的颜色）的时候字体和图标为白色
     *  该方法目前只对小米、魅族以及Android 6.0以上设备有效
     * @return
     */
    protected abstract boolean isNeedStatusBarFontDark();

    /**
     * 定义一个方法用于实现Toolbar的返回键点击事件,具体内容交由子类实现
     * @param v
     */
    protected void doOnToolbarBackClick(View v) {

    }

    ////////////////////////////////////////////////////抽象方法及空方法重载相关//////////////////////////////////////////////////////////
}
