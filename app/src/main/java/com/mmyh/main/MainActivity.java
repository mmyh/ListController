package com.mmyh.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.mmyh.arthur.ArthurAdapter;
import com.mmyh.arthur.ArthurViewHolder;
import com.mmyh.arthur.OnLoadMoreListener;
import com.mmyh.arthur.OnRefreshListener;
import com.mmyh.listcontroller.IQueryList;
import com.mmyh.listcontroller.IQueryListCallback;
import com.mmyh.listcontroller.ListController;
import com.mmyh.listcontroller.QueryListType;
import com.mmyh.main.databinding.ActivityMainItemBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity implements IQueryList {

    RecyclerView mRecyclerView;

    MyAdapter mAdapter;

    ProductsRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.addItemDecoration(new SimplePaddingDecoration(this));
        mAdapter = new MyAdapter();
        mAdapter.setEmptyView(getLayoutInflater().inflate(R.layout.activity_main_emptyview, null), false);
        request = new ProductsRequest(mAdapter);

        ListController.create(this)
                .start();
    }

    @Override
    public Object getHeadView(ViewGroup viewGroup) {
        return getLayoutInflater().inflate(R.layout.activity_main_headview, viewGroup, false);
    }

    @Override
    public Object getFootView(ViewGroup viewGroup) {
        return R.layout.activity_main_footview;
        //return null;
    }

    @Override
    public void query(QueryListType queryListType, final IQueryListCallback callback) {
        request.getQuery().findInBackground(new FindCallback<Product>() {
            @Override
            public void done(List<Product> results, AVException e) {
                callback.onFinishQuery(new ProductsResponse(results, e), new ListErrorRule(e));
            }
        });
    }

    private class MyAdapter extends ArthurAdapter<Product> {

        @Override
        public void onBindViewHolder(int position, ArthurViewHolder arthurViewHolder, Product s) {
            super.onBindViewHolder(position, arthurViewHolder, s);
            ActivityMainItemBinding binding = arthurViewHolder.getBinding();
            binding.name.setText(s.getName());
        }

        @Override
        public int getItemLayoutId() {
            return R.layout.activity_main_item;
        }

    }
}
