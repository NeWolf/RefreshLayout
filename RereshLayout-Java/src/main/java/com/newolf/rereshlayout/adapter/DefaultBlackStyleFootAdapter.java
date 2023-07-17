package com.newolf.rereshlayout.adapter;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;

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
public class DefaultBlackStyleFootAdapter extends BaseFooterAdapter {
    private View mView;
    private LinearLayout mllFooter;
    private TextView mTvState;
    private ImageView mIvLoading;
    private RotateAnimation mRotateAnim;
    private long ROTATE_ANIM_DURATION = 500;
    private boolean mNoMoreData;
    private @ColorRes
    int bgColorRes = -1;
    private @ColorRes
    int textColorRes = -1;

    public DefaultBlackStyleFootAdapter(Context context) {
        super(context);
    }

    @Override
    public View getFooterView() {
        if (mView == null) {
            mView = mInflater.inflate(R.layout.adapter_default_black_style_footer, null, false);
            mllFooter = mView.findViewById(R.id.ll_footer);
            if (bgColorRes != -1) {
                mllFooter.setBackgroundColor(mllFooter.getContext().getResources().getColor(bgColorRes));
            }
            mTvState = mView.findViewById(R.id.tv_state);
            if (textColorRes != -1) {
                mTvState.setBackgroundColor(mllFooter.getContext().getResources().getColor(textColorRes));
            }
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

    public void setBackgroundColor(@ColorRes int colorRes) {
        bgColorRes = colorRes;
        if (mllFooter != null) {
            mllFooter.setBackgroundColor(mllFooter.getContext().getResources().getColor(colorRes));
        }
    }

    public void setTextColor(@ColorRes int colorRes) {
        textColorRes = colorRes;
        if (mTvState != null) {
            mTvState.setTextColor(mTvState.getContext().getResources().getColor(colorRes));
        }
    }
}
