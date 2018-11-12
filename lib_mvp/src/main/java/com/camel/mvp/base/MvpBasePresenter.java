package com.camel.mvp.base;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by CamelLuo on 2018/10/17.
 * <br/>
 * MVP Presenter层 抽象基类
 */
public abstract class MvpBasePresenter<V extends MvpBaseInterface> {

    // View接口类型的弱引用
    protected WeakReference<V> mViewRef;
    private V viewProxy;

    /**
     * 与View建立关联
     *
     * @param view MvpBaseView类型接口
     */
    public void attachView(V view) {
        mViewRef = new WeakReference<V>(view);
        //目标接口->MvpBaseInterface
        Class<?>[] interfaces = view.getClass().getInterfaces();
        try {
            viewProxy = (V) Proxy.newProxyInstance(view.getClass().getClassLoader(), interfaces, new ProxyInvocationHandler<V>(view));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 解除与View的关联
     */
    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
    }

    /**
     * 获取View
     *
     * @return
     */
    public V getView() {
        return viewProxy;
    }

    /**
     * 判断是有与view建立了联系
     *
     * @return
     */
    public boolean isViewAttached() {
        return mViewRef != null && mViewRef.get() != null;
    }

    private class ProxyInvocationHandler<V extends MvpBaseInterface> implements InvocationHandler {

        private V view;
        public ProxyInvocationHandler(V view){
            this.view = view;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (view != null){
                //执行
                return method.invoke(view, args);
            }
            //不存在->则不执行该次方法
            return null;
        }
    }
}
