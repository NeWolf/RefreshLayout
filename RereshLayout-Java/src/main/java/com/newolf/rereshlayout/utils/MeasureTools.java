package com.newolf.rereshlayout.utils;

import android.view.View;
import android.view.ViewGroup;

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
public class MeasureTools {
    public static void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p==null){
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0+0, p.width);
        int height = p.height;
        int childHeightSpec;
        if (height>0){
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        }else{
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
           view.measure(childWidthSpec, childHeightSpec);
    }
}
