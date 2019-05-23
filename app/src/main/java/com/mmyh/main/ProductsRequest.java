package com.mmyh.main;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.mmyh.arthur.ArthurAdapter;
import com.mmyh.listcontroller.IListRequest;
import com.mmyh.listcontroller.IListResponse;

import java.util.List;

public class ProductsRequest implements IListRequest {

    AVQuery<Product> query;

    ArthurAdapter adapter;

    public ProductsRequest(ArthurAdapter adapter) {
        query = AVObject.getQuery(Product.class);
        query.limit(15);
        this.adapter = adapter;
    }

    @Override
    public void nextPage() {
        query.skip(adapter.getDataCount());
    }

    @Override
    public void resetPage() {
        query.skip(0);
    }

    public AVQuery<Product> getQuery() {
        return query;
    }
}
