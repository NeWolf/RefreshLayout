package com.newolf.rereshlayout.adapter;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.newolf.rereshlayout.R;

/**
 * ======================================================================
 *
 * @author : NeWolf
 * @version : 1.0
 * @since :  2023-07-16
 * <p>
 * =======================================================================
 */
public class DefaultBlackStyleFootAdapter extends BaseFooterAdapter{
    private View mView;
    private TextView mTvState;
    private ImageView mIvLoading;
    private RotateAnimation mRotateAnim;
    private long ROTATE_ANIM_DURATION = 500;
    private boolean mNoMoreData;

    public DefaultBlackStyleFootAdapter(Context context) {
        super(context);
    }

    @Override
    public View getFooterView() {
        if (mView == null) {
            mView = mInflater.inflate(R.layout.adapter_default_black_style_footer, null, false);
            mTvState = mView.findViewById(R.id.tv_state);
            mIvLoading = mView.findViewById(R.id.iv_loading);

            mRotateAnim = new RotateAnimation(0.0f, 355,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);
            mRotateAnim.setDuration(ROTATE_ANIM_DURATION);
            mRotateAnim.setFillAfter(true);
            mRotateAnim.setRepeatCount(60); // 30s

        }
        return mView;
    }


    @Override
    public void pullViewToLoadMore(int distY) {
        if (mNoMoreData) {
            mRotateAnim.cancel();
            mIvLoading.clearAnimation();
            mIvLoading.setVisibility(View.INVISIBLE);
            mTvState.setText("已经到底啦");
        } else {
            mIvLoading.setVisibility(View.VISIBLE);
            mIvLoading.setAnimation(mRotateAnim);
            mRotateAnim.startNow();
            mTvState.setText("上拉加载更多");
        }

    }

    @Override
    public void releaseViewToLoadMore(int distY) {
        if (mNoMoreData) {
            mRotateAnim.cancel();
            mIvLoading.setVisibility(View.INVISIBLE);
            mTvState.setText("已经到底啦");
        } else {
            mIvLoading.setVisibility(View.VISIBLE);
            mTvState.setText("松手加载更多");
        }
    }

    @Override
    public void loadingMore() {
        if (mNoMoreData) {
            mRotateAnim.cancel();
            mIvLoading.setVisibility(View.INVISIBLE);
            mTvState.setText("已经到底啦");
        } else {
            mIvLoading.setVisibility(View.VISIBLE);
            mTvState.setText("正在加载中...");
        }

    }

    @Override
    public void loadMoreComplete(boolean noMoreData) {
        mNoMoreData = noMoreData;
        mRotateAnim.cancel();
        if (noMoreData) {
            mIvLoading.clearAnimation();
            mIvLoading.setVisibility(View.INVISIBLE);
            mTvState.setText("已经到底啦");
        } else {
            mIvLoading.setVisibility(View.VISIBLE);
            mTvState.setText("加载完成");
        }

    }
}
