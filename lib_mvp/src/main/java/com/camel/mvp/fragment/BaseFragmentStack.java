package com.camel.mvp.fragment;

import java.util.Stack;

/**
 * Created by Oisny on 2016/6/29.
 */
public class BaseFragmentStack {
    public Stack<BaseFragment> mBaseFragmentStack;

    public BaseFragmentStack() {
        mBaseFragmentStack = new Stack<BaseFragment>();
    }

    // 入栈
    public void push(BaseFragment baseFrag) {
        if (!mBaseFragmentStack.contains(baseFrag)) {
            mBaseFragmentStack.push(baseFrag);
        }
    }

    // 出栈
    public BaseFragment pop() {
        if (mBaseFragmentStack != null && !mBaseFragmentStack.isEmpty()) {
            return mBaseFragmentStack.pop();
        }
        return null;
    }

    // 删除
    public void remove(BaseFragment baseFrag) {
        if (mBaseFragmentStack.contains(baseFrag)) {
            mBaseFragmentStack.remove(baseFrag);
        }
    }

    // 当前页面
    public BaseFragment getCurrentFragment() {
        if (mBaseFragmentStack != null && !mBaseFragmentStack.isEmpty()) {
            return mBaseFragmentStack.lastElement();
        }
        return null;
    }

    // 前一页面
    public BaseFragment getPreviousFragment() {
        if (mBaseFragmentStack != null && !mBaseFragmentStack.isEmpty()) {
            if (mBaseFragmentStack.size() > 1) {
                return mBaseFragmentStack
                        .elementAt(mBaseFragmentStack.size() - 2);
            }
        }
        return null;

    }

    // 是否是当前页面
    public boolean isCurrentFragment(Class<?> cls) {
        return getCurrentFragment() != null
                && getCurrentFragment().getClass().equals(cls);
    }

    // 是否存在页面
    public boolean isEmpty() {
        return mBaseFragmentStack == null || mBaseFragmentStack.isEmpty();
    }

    // 获取当前栈
    public Stack<BaseFragment> getFragmentStack() {
        return mBaseFragmentStack;
    }

    public boolean contains(BaseFragment baseFrag) {
        return mBaseFragmentStack.contains(baseFrag);
    }
}
