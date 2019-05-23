package com.mmyh.listcontroller;

import android.view.View;
import android.view.ViewGroup;

public interface IQueryList {

    public Object getHeadView(ViewGroup viewGroup);

    public Object getFootView(ViewGroup viewGroup);

    public void query(QueryListType queryListType, IQueryListCallback callback);

}
