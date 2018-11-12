package com.camel.mvp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.camel.mvp.R;
import com.camel.mvp.base.MvpBaseActivity;
import com.camel.mvp.base.MvpBaseInterface;
import com.camel.mvp.base.MvpBasePresenter;
import com.camel.mvp.fragment.BaseFragmentManager;
import com.camel.mvp.fragment.FragmentSupport;
import com.camel.mvp.util.AtyManager;
import com.camel.mvp.util.ScreenUtil;
import com.camel.mvp.util.ToastUtils;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by CamelLuo on 2018/10/17.
 * 实现了MVP架构 的 Activity抽象基类 主要实现、封装各种流程
 */
public abstract class BaseActivity<V extends MvpBaseInterface, P extends MvpBasePresenter<V>> extends MvpBaseActivity<V, P>
        implements MvpBaseInterface, FragmentSupport {
    private static final String TAG = BaseActivity.class.getSimpleName();

    protected Toolbar mToolbar;
    protected TextView mToolbarTittle;
    protected View mToolbarBackBtnView;

    private Unbinder unbinder;
    /**
     * Fragment管理器类
     */
    protected BaseFragmentManager mBaseFragmentManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将Activity存入Activity堆栈管理
        AtyManager.getInstance().pushAty(this);

        //获取Bundle中的数据
        getIntentExtras(getIntent());

        //布局文件id来源于继承子类实现
        if (setContentViewId() != 0) {
            setContentView(setContentViewId());
            //初始化ButterKnife
            unbinder = ButterKnife.bind(this);

            //让子类初始化视图附带Bundle
            initView(savedInstanceState);

            //设置沉浸式Toolbar
            if (isNeedToolbar()) {
//                //设置使用沉浸式Toolbar
                setImmsersionToolbar(true);
//                //设置使用透明状态栏
                setTransparentStatusBar(true);
//                //设置是否使用黑色字体状态栏
                setStatusBarFontDark(isNeedStatusBarFontDark());
                //设置Fragment使用Toolbar作为ActionBar
                if (mToolbar != null) {
                    setSupportActionBar(mToolbar);
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
        } else {
            finish();
        }
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
        //将Activity从Activity堆栈管理中移除
        AtyManager.getInstance().popAty(this);
        super.onDestroy();
        //解除ButterKnife绑定
        unbinder.unbind();
    }

    /**
     * 定义一个方法用于实现所有Activity页面控件的onClick 点击事件
     * 所有控件只需实现 onXmlClick 的style 或者onclick中填入 onXmlClick 即可
     *
     * @param v
     */
    public void onXmlClick(View v) {

    }

    ////////////////////////////////////////////////////公用方法相关//////////////////////////////////////////////////////////

    /**
     * 定义一个方法用于进行跳转页面
     *
     * @param cls 所跳转的目的Activity类的class类
     */
    public void startActivity(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }

    /**
     * 定义一个方法用于进行跳转页面，并附带Bundle
     *
     * @param cls    所跳转的目的Activity类的class类
     * @param bundle 跳转所携带的信息(Bundle对象)
     */
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
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
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 定义一个方法用于退出当前Activity页面，并附带结果码
     *
     * @param resultCode 返回给 跳转当前Activity页面的上一页面 的结果码
     */
    public void finish(int resultCode) {
        setResult(resultCode);
        finish();
    }

    /**
     * 定义一个方法用于退出当前Activity页面，并附带结果码
     *
     * @param resultCode 返回给 跳转当前Activity页面的上一页面 的结果码
     * @param data       返回给 跳转当前Activity页面的上一页面 的Intent数据
     */
    public void finish(int resultCode, Intent data) {
        setResult(resultCode, data);
        finish();
    }


    @Override
    public void showMsg(String msg) {
        ToastUtils.showText(this, msg);
    }

    @Override
    public void showMsg(int strId) {
        ToastUtils.showText(this, strId);
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


    ////////////////////////////////////////////////////Fragment相关//////////////////////////////////////////////////////////
    @Override
    public BaseFragmentManager getBaseFragmentManager() {
        if (mBaseFragmentManager != null) {
            return mBaseFragmentManager;
        } else {
            return null;
        }
    }

    /**
     * 定义一个方法用于初始化FragmentManager管理类
     * 如需在Activity中使用Fragment，并且加载的Fragment所依赖的为同一个layout布局，则请调用该方法
     *
     * @param manager              Fragment管理类
     * @param fragmentRootLayoutId 加载Fragment时所依赖的layout布局
     * @param listener             Fragment页面变化监听器，如不需要则置null
     */
    protected void initBaseFragmentManager(FragmentManager manager, int fragmentRootLayoutId,
                                           BaseFragmentManager.OnFragmentChangedListener listener) {
        if (listener == null) {
            mBaseFragmentManager = new BaseFragmentManager(manager, fragmentRootLayoutId);
        } else {
            mBaseFragmentManager = new BaseFragmentManager(manager, fragmentRootLayoutId, listener);
        }
    }

    @Override
    public void onFragmentResumed() {
    }

    @Override
    public void onFragmentPaused() {
    }

    @Override
    public void onFragmentStop() {
    }

    ////////////////////////////////////////////////////Fragment相关//////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////Toolbar相关//////////////////////////////////////////////////////////

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
        mToolbar = findViewById(R.id.toolbar_id);
        //设置Toolbar的标题
        mToolbarTittle = findViewById(R.id.toolbar_tittle);
        //绑定返回按钮
        mToolbarBackBtnView = findViewById(R.id.toolbar_back_button);
        mToolbar.setTitle("");
        if (title != null) {
            if (mToolbarTittle != null) {
                mToolbarTittle.setText(title);
            }
        }

        setSupportActionBar(mToolbar);
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
     *
     * @param isUse true:使用,false:不使用
     */
    public void setImmsersionToolbar(boolean isUse) {
        if (mToolbar != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                int paddingTop = mToolbar.getPaddingTop();
                int paddingBottom = mToolbar.getPaddingBottom();
                int paddingLeft = mToolbar.getPaddingLeft();
                int paddingRight = mToolbar.getPaddingRight();
                int statusHeight = ScreenUtil.getStatusHeight(this);
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
     *
     * @param isUse
     */
    private void setTransparentStatusBar(boolean isUse) {
        if (isUse) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window window = getWindow();
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
     * 该方法目前只对小米、魅族以及Android 6.0以上设备有效
     *
     * @param dark 状态栏字体和图标是否为深色
     */
    protected void setStatusBarFontDark(boolean dark) {
        // 小米MIUI
        try {
            Window window = this.getWindow();
            Class clazz = this.getWindow().getClass();
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
            Window window = this.getWindow();
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
                this.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }
    ////////////////////////////////////////////////////Toolbar相关//////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////抽象方法及空方法重载相关//////////////////////////////////////////////////////////
    /**
     * 定义一个方法用于实现Toolbar的返回键点击事件,具体内容交由子类实现
     * @param v
     */
    protected void doOnToolbarBackClick(View v) {

    }

    /**
     * 定义一个方法用于开启或关闭该页面的debug调试Log打印信息
     * @return 继承子类不覆写的话默认返回false，如需要打印debug调试Log打印信息，请覆写该方法并返回true
     */
    protected boolean isShowDebugLog() {
        return false;
    }

    /**
     * 定义一个抽象方法用于设置xml布局文件的id
     *
     * @return
     */
    protected abstract int setContentViewId();

    /**
     * 定义一个抽象方法用于让子类在该方法中实现 初始化view等工作
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 定义一个方法用于获取Bundle中的内容,必须自行判断Intent对象是否为null
     */
    public abstract void getIntentExtras(Intent intent);

    /**
     * 定义一个方法用于设置是否需要Toolbar，即使设置为true，也要手动在 initView() 方法中调用initToolBar() 系列方法传入标题等信息
     *
     * @return
     */
    protected abstract boolean isNeedToolbar();

    /**
     * 该方法只在 isNeedToolbar() 返回为 true时 才执行判断
     * 定义一个方法用于设置Android状态栏的字体颜色，状态栏为亮色（白色）的时候字体和图标是黑色，
     * 状态栏为暗色（不和白色冲突的颜色）的时候字体和图标为白色
     * 该方法目前只对小米、魅族以及Android 6.0以上设备有效
     *
     * @return
     */
    protected abstract boolean isNeedStatusBarFontDark();

    ////////////////////////////////////////////////////抽象方法及空方法重载相关//////////////////////////////////////////////////////////
}
