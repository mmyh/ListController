package com.mmyh.listcontroller;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.mmyh.arthur.ArthurAdapter;

public interface IQueryList {

    public RecyclerView getRecyclerView();

    public ArthurAdapter getArthurAdapter();

    public IListRequest getListRequest();

    public Object getHeadView(ViewGroup viewGroup);

    public Object getFootView(ViewGroup viewGroup);

    public void query(QueryListType queryListType, IQueryListCallback callback);

}
