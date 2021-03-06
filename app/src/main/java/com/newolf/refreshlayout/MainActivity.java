package com.newolf.refreshlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.newolf.refreshlayout.adapter.TestAdapter;
import com.newolf.rereshlayout.RefreshLayout;
import com.newolf.rereshlayout.interfaces.OnFooterLoadMoreListener;
import com.newolf.rereshlayout.interfaces.OnHeaderRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int page = 1;
    private TestAdapter mTestAdapter;
    private RefreshLayout mRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        loadData(page);
        initListener();
    }

    private void initView() {
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setBaseHeaderAdapter(getClass().getSimpleName());
        mRefreshLayout.setBaseFooterAdapter();
        RecyclerView rvList = findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        mTestAdapter = new TestAdapter(null);
        rvList.setAdapter(mTestAdapter);
    }

    private void loadData(int page) {
        List<String> data = new ArrayList<>();
        try {
            Thread.sleep(500);
            if (page == 1) {
                for (int i = 1; i < 11; i++) {
                    data.add("Item :" + i);
                }

                mRefreshLayout.onHeaderRefreshComplete();
                mTestAdapter.setNewData(data);
            } else {
                data.clear();
                for (int i = (page - 1) * 10 + 1; i < page * 10 + 1; i++) {
                    data.add("Item :" + i);
                }
                mRefreshLayout.onLoadMoreComplete();
                mTestAdapter.addData(data);

            }

        } catch (InterruptedException e) {

            e.printStackTrace();
        }


    }

    private void initListener() {
        mRefreshLayout.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(RefreshLayout refreshLayout) {
                loadData(page = 1);
            }
        });
        mRefreshLayout.setOnFooterLoadMoreListener(new OnFooterLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                loadData(page++);
            }
        });
    }
}
