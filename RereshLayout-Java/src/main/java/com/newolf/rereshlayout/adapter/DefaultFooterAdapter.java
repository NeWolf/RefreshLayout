package com.newolf.rereshlayout.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.newolf.rereshlayout.R;

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
public class DefaultFooterAdapter extends BaseFooterAdapter {

    private TextView mTvState;
    private View mView;

    public DefaultFooterAdapter(Context context) {
        super(context);
    }

    @Override
    public View getFooterView() {
        if (mView == null) {
            mView = mInflater.inflate(R.layout.adapter_default_footer, null, false);
            mTvState = mView.findViewById(R.id.tv_state);
        }
        return mView;
    }

    @Override
    public void pullViewToLoadMore(int distY) {
        mTvState.setText("上拉加载更多");
    }

    @Override
    public void releaseViewToLoadMore(int distY) {
        mTvState.setText("松手加载更多");
    }

    @Override
    public void loadingMore() {
        mTvState.setText("正在加载...");
    }

    @Override
    public void loadMoreComplete() {
        mTvState.setText("加载完成");
    }
}
