package com.newolf.rereshlayout.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.newolf.rereshlayout.R;

import java.util.Date;

/**
 * ================================================
 *
 * @author : NeWolf
 * @version : 1.0
 * date :  2018/5/9
 * 描述:
 * 历史:
 * ================================================
 */
public class DefaultHeaderAdapter extends BaseHeaderAdapter {

    public static final String spName = "time";
    public static final String LAST_TIME = "last_time";
    public static final String SPACE = ":";
    private ImageView mIvArrow;
    private TextView mTvState;
    private TextView mTvTime;
    private RotateAnimation mRotateUpAnim;
    private RotateAnimation mRotateDownAnim;
    private long ROTATE_ANIM_DURATION = 300;
    private View mHeaderView;
    private SharedPreferences sp;
    private String mTag;
    public static final String REGEX = "-";

    public DefaultHeaderAdapter(Context context, String tag) {
        super(context);
        sp = context.getApplicationContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        mTag = tag;
    }

    @Override
    public View getHeaderView() {
        if (mHeaderView == null) {
            mHeaderView = mInflater.inflate(R.layout.adpater_default_header, null, false);
            mIvArrow = mHeaderView.findViewById(R.id.iv_arrow);
            mTvState = mHeaderView.findViewById(R.id.tv_state);
            mTvTime = mHeaderView.findViewById(R.id.tv_time);

            mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);
            mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
            mRotateUpAnim.setFillAfter(true);
            mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);
            mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
            mRotateDownAnim.setFillAfter(true);
        }


        return mHeaderView;
    }

    @Override
    public void pullViewToRefresh(int distY) {
        mIvArrow.clearAnimation();
        mIvArrow.startAnimation(mRotateDownAnim);
        mTvState.setText("下拉以刷新");
        mTvTime.setText(friendlyTime(new Date(getLastTime())));
    }

    @Override
    public void releaseViewToRefresh(int distY) {
        mIvArrow.clearAnimation();
        mIvArrow.startAnimation(mRotateUpAnim);
        mTvState.setText("松手开始刷新");
    }

    @Override
    public void headerRefreshing() {
        mTvState.setText("更新中...");
    }

    @Override
    public void headerRefreshComplete() {
        mTvState.setText("刷新完成");
        savaTime(System.currentTimeMillis());
        mTvTime.setText(friendlyTime(new Date(getLastTime())));
    }

    private long getLastTime() {
        String tags = sp.getString(LAST_TIME, "");
        long time = 0;

        if (tags.contains(mTag)){
            String[] split = tags.split(REGEX);
            for (int i = 0; i < split.length; i++) {
                if (split[i].contains(mTag)){
                    time =Long.parseLong(split[i].substring(split[i].indexOf(SPACE)+1));
                    break;
                }
            }
        }
        return time == 0 ? System.currentTimeMillis() : time;
    }


    public String friendlyTime(Date time) {
        //获取time距离当前的秒数
        int ct = (int) ((System.currentTimeMillis() - time.getTime()) / 1000);

        if (ct == 0) {
            return "刚刚";
        }

        if (ct > 0 && ct < 60) {
            return ct + "秒前";
        }

        if (ct >= 60 && ct < 3600) {
            return Math.max(ct / 60, 1) + "分钟前";
        }
        if (ct >= 3600 && ct < 86400)
            return ct / 3600 + "小时前";
        if (ct >= 86400 && ct < 2592000) { //86400 * 30
            int day = ct / 86400;
            return day + "天前";
        }
        if (ct >= 2592000 && ct < 31104000) { //86400 * 30
            return ct / 2592000 + "月前";
        }
        return ct / 31104000 + "年前";
    }

    private void savaTime(long time) {
        String tags = sp.getString(LAST_TIME, "");

        if (tags.contains(mTag)) {
            String[] split = tags.split(REGEX);
            for (int i = 0; i < split.length; i++) {
                if (split[i].contains(mTag)){
                    tags = tags.replace(split[i],mTag + SPACE + time + REGEX);
                    break;
                }
            }
        } else {
            tags = tags + mTag + SPACE + time +REGEX;
        }
        sp.edit().putString(LAST_TIME, tags).apply();
    }


}
