package com.newolf.rereshlayout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * ================================================
 *
 * @author : NeWolf
 * @version : 1.0
 * @date :  2018/5/9
 * 描述:
 * 历史:<br/>
 * ================================================
 */
public abstract class BaseFooterAdapter {

    protected final LayoutInflater mInflater;

    public BaseFooterAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    /**
     * 获取底部 View
     *
     * @return View
     */
    public abstract View getFooterView();

    /**
     * 上拉加载更多
     *
     * @param distY
     */
    public abstract void pullViewToLoadMore(int distY);

    /**
     * 松开开始加载更多
     *
     * @param distY
     */
    public abstract void releaseViewToLoadMore(int distY);

    /**
     * 正在加载
     */
    public abstract void loadingMore();

    /**
     * 加载完成
     */
    public abstract void loadMoreComplete();

}
