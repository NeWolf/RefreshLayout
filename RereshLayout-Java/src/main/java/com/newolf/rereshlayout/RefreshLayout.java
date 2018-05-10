package com.newolf.rereshlayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.newolf.rereshlayout.adapter.BaseFooterAdapter;
import com.newolf.rereshlayout.adapter.BaseHeaderAdapter;
import com.newolf.rereshlayout.adapter.DefaultFooterAdapter;
import com.newolf.rereshlayout.adapter.DefaultHeaderAdapter;
import com.newolf.rereshlayout.interfaces.OnFooterLoadMoreListener;
import com.newolf.rereshlayout.interfaces.OnHeaderRefreshListener;
import com.newolf.rereshlayout.utils.MeasureTools;

/**
 * ================================================
 *
 * @author : NeWolf
 * @version : 1.0
 * @date :  2018/5/8
 * 描述: 一个支持ListView , RecycleView ,ScrollView  的下拉刷新和加载更多 以及 WebView 下拉刷新的容器
 * 历史:<br/>
 * ================================================
 */
public class RefreshLayout extends LinearLayout {
    public static final float SCALE_SLOP = 0.3f;
    private int animDuration = 500;//头、尾 部回弹动画执行时间
    public String TAG = getClass().getSimpleName();
    private Context mContext;
    private int mLastY;
    private int mTouchSlop;

    // 刷新时状态
    private static final int PULL_TO_REFRESH = 2;
    private static final int RELEASE_TO_REFRESH = 3;
    private static final int REFRESHING = 4;
    // pull state
    private static final int PULL_UP_STATE = 0;
    private static final int PULL_DOWN_STATE = 1;

    private int mPullState;

    //Header
    private int mHeaderState;
    private BaseHeaderAdapter mBaseHeaderAdapter;
    private View mHeaderView;
    private int mHeaderViewMeasuredHeight;
    private AdapterView<?> mAdapterView;
    private RecyclerView mRecyclerView;
    private ScrollView mScrollView;
    private WebView mWebView;
    private BaseFooterAdapter mBaseFooterAdapter;
    private View mFooterView;
    private int mFooterViewMeasuredHeight;
    private OnFooterLoadMoreListener mOnFooterLoadMoreListener;
    private OnHeaderRefreshListener mOnHeaderRefreshListener;
    private int mFooterState;

    public RefreshLayout(Context context) {
        super(context);
        init(context);
    }

    public RefreshLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RefreshLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setOrientation(VERTICAL);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }


    public void setBaseHeaderAdapter() {
        if (mBaseHeaderAdapter == null) {
            mBaseHeaderAdapter = new DefaultHeaderAdapter(mContext);
        }
        initHeaderView();
        initSubViewType();
    }

    public void setBaseHeaderAdapter(BaseHeaderAdapter baseHeaderAdapter) {
        mBaseHeaderAdapter = baseHeaderAdapter;
        initHeaderView();
        initSubViewType();
    }

    public void setBaseFooterAdapter() {
        if (mBaseFooterAdapter == null) {
            mBaseFooterAdapter = new DefaultFooterAdapter(mContext);
        }

        initFooterView();
    }

    public void setBaseFooterAdapter(BaseFooterAdapter baseFooterAdapter) {
        mBaseFooterAdapter = baseFooterAdapter;
        initFooterView();
    }

    public void onHeaderRefreshComplete() {
        if (mBaseHeaderAdapter == null) {
            return;
        }
        reSetHeaderTopMargin(-mHeaderViewMeasuredHeight);
        mBaseHeaderAdapter.headerRefreshComplete();
        mHeaderState = PULL_TO_REFRESH;
    }

    public void onLoadMoreComplete() {
        if (mBaseFooterAdapter == null) {
            return;
        }
        reSetHeaderTopMargin(-mHeaderViewMeasuredHeight);
        mBaseFooterAdapter.loadMoreComplete();
        mFooterState = PULL_TO_REFRESH;
    }


    private void initHeaderView() {
        mHeaderView = mBaseHeaderAdapter.getHeaderView();
        MeasureTools.measureView(mHeaderView);
        mHeaderViewMeasuredHeight = mHeaderView.getMeasuredHeight();
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeaderViewMeasuredHeight);
        layoutParams.topMargin = -mHeaderViewMeasuredHeight;
        mHeaderView.setLayoutParams(layoutParams);
        removeView(mHeaderView);
        addView(mHeaderView, 0, layoutParams);
    }


    private void initFooterView() {
        mFooterView = mBaseFooterAdapter.getFooterView();
        MeasureTools.measureView(mFooterView);
        mFooterViewMeasuredHeight = mFooterView.getMeasuredHeight();
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mFooterViewMeasuredHeight);
        removeView(mFooterView);
        addView(mFooterView, layoutParams);
    }

    private void initSubViewType() {
        int count = getChildCount();
        if (count < 2) {
            return;
        }

        View view = getChildAt(1);

        if (view instanceof AdapterView<?>) {
            mAdapterView = (AdapterView<?>) view;
        }

        if (view instanceof RecyclerView) {
            mRecyclerView = (RecyclerView) view;
        }

        if (view instanceof ScrollView) {
            mScrollView = (ScrollView) view;
        }

        if (view instanceof WebView) {
            mWebView = (WebView) view;
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int distY = y - mLastY;
                Log.e(TAG, "onInterceptTouchEvent :\tdistY = " + distY);
                if (Math.abs(distY) > mTouchSlop && isParentViewScroll(distY)) {
                    Log.e(TAG, "onInterceptTouchEvent: belong to ParentView  distY = " + distY);
                    return true; //此时,触发当前onTouchEvent事件
                }
                break;


        }
        return super.onInterceptTouchEvent(event);
    }

    private boolean isParentViewScroll(int distY) {
        boolean belongToParentView = false;
        if (mHeaderState == REFRESHING) {
            belongToParentView = false;
        }

        if (mAdapterView != null) {
            if (distY > 0) {
                View child = mAdapterView.getChildAt(0);
                if (child == null) {
                    belongToParentView = false;
                } else if (mAdapterView.getFirstVisiblePosition() == 0 && child.getTop() == 0) {
                    mPullState = PULL_DOWN_STATE;
                    belongToParentView = true;
                }

            } else if (distY < 0) {
                int lastPosition = mAdapterView.getCount() - 1;
                View lastView = mAdapterView.getChildAt(lastPosition);
                if (lastView == null) {
                    belongToParentView = false;
                } else if (lastView.getBottom() <= getHeight() && mAdapterView.getLastVisiblePosition() == lastPosition) {
                    // 最后一个子view的Bottom小于父View的高度说明mAdapterView的数据没有填满父view,
                    // 等于父View的高度说明mAdapterView已经滑动到最后
                    mPullState = PULL_UP_STATE;
                    belongToParentView = true;
                }
            }
        } else if (mRecyclerView != null) {
            if (distY > 0) {
                View child = mRecyclerView.getChildAt(0);
                if (child == null) {
                    belongToParentView = false;
                }
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                int firstPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (firstPosition == 0) {
                    mPullState = PULL_DOWN_STATE;
                    belongToParentView = true;
                }
            } else if (mRecyclerView.computeVerticalScrollExtent() + mRecyclerView.computeHorizontalScrollOffset() >= mRecyclerView.computeHorizontalScrollRange()) {

                mPullState = PULL_UP_STATE;
                belongToParentView = true;
            }

        } else if (mScrollView != null) {

            View child = mScrollView.getChildAt(0);
            if (distY > 0) {

                if (child == null) {
                    belongToParentView = false;
                }

                if (mScrollView.getScrollY() == 0) {
                    mPullState = PULL_DOWN_STATE;
                    belongToParentView = true;
                }
            } else if (child.getHeight() <= getHeight() + mScrollView.getScrollY()) {
                mPullState = PULL_UP_STATE;
                belongToParentView = true;
            }

        } else if (mWebView != null) {
            View child = mWebView.getChildAt(0);
            if (distY > 0) {
                if (child == null) {
                    belongToParentView = false;
                }

                int distance = mWebView.getScrollY();
                if (distance == 0) {
                    mPullState = PULL_DOWN_STATE;
                    belongToParentView = true;
                }
            }
        }


        return belongToParentView;
    }


    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int distY = y - mLastY;
                Log.e(TAG, "onTouchEvent: distY==" + distY);
                if (mPullState == PULL_DOWN_STATE) {
                    Log.e(TAG, "onTouchEvent: pull down begin--> " + distY);
                    initHeaderViewToRefresh(distY);
                } else if (mPullState == PULL_UP_STATE) {
                    initFooterViewToLoadMore(distY);
                }
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                performClick();
                int topMargin = getHeaderTopMargin();
                Log.e(TAG, "onTouchEvent: topMargin==" + topMargin);
                if (mPullState == PULL_DOWN_STATE) {
                    if (topMargin >= 0) {
                        headerRefreshing();
                    } else {
                        reSetHeaderTopMargin(-mHeaderViewMeasuredHeight);
                    }
                } else if (mPullState == PULL_UP_STATE) {
                    if (Math.abs(topMargin) >= mHeaderViewMeasuredHeight
                            + mFooterViewMeasuredHeight) {
                        // 开始执行footer 加载更多
                        footerLoadingMore();
                    } else {
                        // 还没有执行刷新，重新隐藏
                        reSetHeaderTopMargin(-mHeaderViewMeasuredHeight);
                    }
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    private void footerLoadingMore() {
        if (mBaseFooterAdapter == null) {
            return;
        }
        mFooterState = REFRESHING;
        int top = mHeaderViewMeasuredHeight + mFooterViewMeasuredHeight;
        setHeaderTopMargin(-top);
        mBaseFooterAdapter.loadingMore();
        if (mOnFooterLoadMoreListener != null) {
            mOnFooterLoadMoreListener.onLoadMore(this);
        }
    }

    /**
     * 上拉或下拉至一半时，放弃下来，视为完成一次下拉统一处理，初始化所有内容
     *
     * @param topMargin
     */
    private void reSetHeaderTopMargin(int topMargin) {
        if (mBaseHeaderAdapter != null) {
            mBaseHeaderAdapter.headerRefreshComplete();
        }

        if (mBaseFooterAdapter != null) {
            mBaseFooterAdapter.loadMoreComplete();
        }

//        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
//        params.topMargin = topMargin;
//        mHeaderView.setLayoutParams(params);
//        invalidate();

        smoothMargin(topMargin);
    }


    /**
     * 平滑设置header view 的margin
     *
     * @param topMargin
     */
    private void smoothMargin(int topMargin) {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        ValueAnimator animator = ValueAnimator.ofInt(params.topMargin, topMargin);
        animator.setDuration(animDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeaderViewMeasuredHeight);
                lp.topMargin = (int) animation.getAnimatedValue();
                mHeaderView.setLayoutParams(lp);
            }
        });
        animator.start();
    }

    private void headerRefreshing() {
        if (mBaseHeaderAdapter == null) {
            return;
        }

        mHeaderState = REFRESHING;
        setHeaderTopMargin(0);
        mBaseHeaderAdapter.headerRefreshing();
        if (mOnHeaderRefreshListener != null) {
            mOnHeaderRefreshListener.onHeaderRefresh(this);
        }
    }

    private int getHeaderTopMargin() {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        return params.topMargin;
    }


    private void initHeaderViewToRefresh(int distY) {
        if (mBaseHeaderAdapter == null) {
            return;
        }
        int topDistance = updateHeadViewMarginTop(distY);
        if (topDistance < 0 && topDistance > -mHeaderViewMeasuredHeight) {
            mBaseHeaderAdapter.pullViewToRefresh(distY);
            mHeaderState = PULL_TO_REFRESH;
        } else if (topDistance > 0 && mHeaderState != RELEASE_TO_REFRESH) {
            mBaseHeaderAdapter.releaseViewToRefresh(distY);
            mHeaderState = RELEASE_TO_REFRESH;
        }
    }

    /**
     * @param distY
     */
    private void initFooterViewToLoadMore(int distY) {
        if (mBaseFooterAdapter == null) {
            return;
        }

        int topDistance = updateHeadViewMarginTop(distY);

        Log.e(TAG, "the distance  is " + topDistance);

        // 如果header view topMargin 的绝对值大于或等于(header + footer) 四分之一 的高度
        // 说明footer view 完全显示出来了，修改footer view 的提示状态
        if (Math.abs(topDistance) >= (mHeaderViewMeasuredHeight + mFooterViewMeasuredHeight) / 4
                && mFooterState != RELEASE_TO_REFRESH) {
            mBaseFooterAdapter.pullViewToLoadMore(distY);
            mFooterState = RELEASE_TO_REFRESH;
        } else if (Math.abs(topDistance) < (mHeaderViewMeasuredHeight + mFooterViewMeasuredHeight) / 4) {
            mBaseFooterAdapter.releaseViewToLoadMore(distY);
            mFooterState = PULL_TO_REFRESH;
        }
    }

    /**
     * 获取当前header view 的topMargin
     *
     * @param distY
     * @return
     */
    private int updateHeadViewMarginTop(int distY) {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        float topMargin = params.topMargin + distY * SCALE_SLOP;
        params.topMargin = (int) topMargin;
        mHeaderView.setLayoutParams(params);
        invalidate();
        return params.topMargin;
    }

    /**
     * 设置header view 的topMargin的值
     *
     * @param topMargin ，为0时，说明header view 刚好完全显示出来； 为-mHeaderViewHeight时，说明完全隐藏了
     * @description
     */
    private void setHeaderTopMargin(int topMargin) {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        params.topMargin = topMargin;
        mHeaderView.setLayoutParams(params);
        invalidate();
    }

    public void setOnHeaderRefreshListener(@NonNull OnHeaderRefreshListener onHeaderRefreshListener) {
        mOnHeaderRefreshListener = onHeaderRefreshListener;
    }

    public void setOnFooterLoadMoreListener(@NonNull OnFooterLoadMoreListener onFooterLoadMoreListener) {
        mOnFooterLoadMoreListener = onFooterLoadMoreListener;
    }
}
