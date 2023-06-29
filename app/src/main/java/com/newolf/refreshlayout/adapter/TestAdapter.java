package com.newolf.refreshlayout.adapter;


import androidx.annotation.Nullable;


import com.newolf.library.adapt.base.BaseQuickAdapter;
import com.newolf.library.adapt.base.viewholder.BaseViewHolder;
import com.newolf.refreshlayout.R;

import java.util.List;

/**
 * ================================================
 *
 * @author : NeWolf
 * @version : 1.0
 * @date :  2018/5/21
 * 描述:
 * 历史:<br/>
 * ================================================
 */
public class TestAdapter   extends BaseQuickAdapter<String, BaseViewHolder> {

    public TestAdapter( @Nullable List<String> data) {
        super(R.layout.adapter_test, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_show, item);
    }
}
