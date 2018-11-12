package com.camel.mvp.fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oisny on 2016/6/29.
 */
public class BaseFragmentManager {
    BaseFragmentStack mBaseFragmentStack;
    private FragmentManager mFragmentManager = null;
    private List<OnFragmentChangedListener> mOnFragmentChangedListeners = new ArrayList<OnFragmentChangedListener>();
    private int mContainerId;

    public BaseFragmentManager(FragmentManager manager, int containerId,
                               OnFragmentChangedListener listener) {
        mContainerId = containerId;
        mFragmentManager = manager;
        if (listener != null) {
            mOnFragmentChangedListeners.add(listener);
        }
        mBaseFragmentStack = new BaseFragmentStack();
    }

    public BaseFragmentManager(FragmentManager manager, int containerId) {
        this(manager, containerId, null);
    }

    public void addOnFragmentChangedListener(OnFragmentChangedListener listener) {
        mOnFragmentChangedListeners.add(listener);
    }

    public void removeOnFragmentChangedListener(
            OnFragmentChangedListener listener) {
        if (mOnFragmentChangedListeners.contains(listener)) {
            mOnFragmentChangedListeners.remove(listener);
        }
    }

    public void add(BaseFragment baseFrag) {
        BaseFragment previousFragment = null;
        if (!mOnFragmentChangedListeners.isEmpty()) {
            previousFragment = mBaseFragmentStack.getCurrentFragment();
        }
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(mContainerId, baseFrag, baseFrag.getClass()
                .getSimpleName());
        transaction.commitAllowingStateLoss();
        mBaseFragmentStack.push(baseFrag);
        if (!mOnFragmentChangedListeners.isEmpty()) {
            for (OnFragmentChangedListener listener : mOnFragmentChangedListeners) {
                listener.onFragmentChanged(baseFrag, previousFragment);
            }
        }
    }

    public void reset(BaseFragment baseFrag) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        BaseFragment fragment = null;
        while ((fragment = mBaseFragmentStack.pop()) != null) {
            if (fragment.isAdded()) {
                transaction.remove(fragment);
            }
        }
        if (baseFrag != null) {
            transaction.add(mContainerId, baseFrag, baseFrag.getClass()
                    .getSimpleName());
        }
        transaction.commitAllowingStateLoss();
        if (baseFrag != null) {
            mBaseFragmentStack.push(baseFrag);
        }
        if (!mOnFragmentChangedListeners.isEmpty()) {
            BaseFragment previousFragment = mBaseFragmentStack
                    .getPreviousFragment();
            for (OnFragmentChangedListener listener : mOnFragmentChangedListeners) {
                listener.onFragmentChanged(baseFrag, previousFragment);
            }
        }
    }

    public void remove(BaseFragment baseFrag) {
        if (baseFrag.isAdded()) {
            FragmentTransaction transaction = mFragmentManager
                    .beginTransaction();
            transaction.remove(baseFrag);
            transaction.commitAllowingStateLoss();
            mBaseFragmentStack.remove(baseFrag);
            if (!mOnFragmentChangedListeners.isEmpty()) {
                BaseFragment currentFragment = mBaseFragmentStack
                        .getCurrentFragment();
                for (OnFragmentChangedListener listener : mOnFragmentChangedListeners) {
                    listener.onFragmentChanged(currentFragment, baseFrag);
                }
            }
        }
    }

    public void replace(BaseFragment baseFrag) {
        replace(baseFrag, false);
    }

    public void replace(BaseFragment baseFrag, boolean isAddBackStack) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (baseFrag.isAdded()) {
            transaction.remove(baseFrag);
            mBaseFragmentStack.remove(baseFrag);
        }
        transaction.replace(mContainerId, baseFrag, baseFrag.getClass()
                .getSimpleName());
        transaction.commitAllowingStateLoss();
        BaseFragment previousFragment = null;
        if (!mOnFragmentChangedListeners.isEmpty()) {
            previousFragment = mBaseFragmentStack.getCurrentFragment();
        }
        if (!isAddBackStack) {
            mBaseFragmentStack.pop();
            if (!mOnFragmentChangedListeners.isEmpty()) {
                previousFragment = mBaseFragmentStack.getCurrentFragment();
            }
        }
        mBaseFragmentStack.push(baseFrag);
        if (!mOnFragmentChangedListeners.isEmpty()) {
            for (OnFragmentChangedListener listener : mOnFragmentChangedListeners) {
                listener.onFragmentChanged(baseFrag, previousFragment);
            }
        }
    }

    public synchronized void back() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        BaseFragment backfragment = mBaseFragmentStack.pop();
        if (backfragment == null) {
            return;
        }
        if (backfragment.isAdded()) {
            transaction.remove(backfragment);
        }
        BaseFragment fragment = mBaseFragmentStack.getCurrentFragment();
        if (fragment != null) {
            if (!fragment.isAdded()) {
                transaction.add(mContainerId, fragment, fragment.getClass()
                        .getSimpleName());
            }
        }
        transaction.commitAllowingStateLoss();
        if (!mOnFragmentChangedListeners.isEmpty()) {
            BaseFragment previousFragment = mBaseFragmentStack
                    .getPreviousFragment();
            for (OnFragmentChangedListener listener : mOnFragmentChangedListeners) {
                listener.onFragmentChanged(fragment, previousFragment);
            }
        }
    }

    public boolean isEmpty() {
        return mBaseFragmentStack.isEmpty();
    }

    public synchronized BaseFragment getCurrentFragment() {
        return mBaseFragmentStack.getCurrentFragment();
    }

    public boolean isCurrentFragment(Class<?> cls) {
        return mBaseFragmentStack.isCurrentFragment(cls);
    }

    public boolean isCurrentFragmentResumed() {
        BaseFragment fragment = getCurrentFragment();
        return fragment.isResumed();
    }

    public interface OnFragmentChangedListener {
        void onFragmentChanged(BaseFragment currentFragment,
                               BaseFragment previousFragment);
    }
}
