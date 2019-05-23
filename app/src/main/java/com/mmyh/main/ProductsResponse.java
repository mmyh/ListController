package com.mmyh.main;

import com.avos.avoscloud.AVException;
import com.mmyh.listcontroller.IListResponse;

import java.util.List;

public class ProductsResponse implements IListResponse {

    private List<Product> results;

    private AVException e;

    public ProductsResponse(List<Product> results, AVException e) {
        this.results = results;
        this.e = e;
    }

    @Override
    public boolean hasNextPage() {
        if (!isSuccess()) {
            return true;
        }
        return results != null && results.size() > 0;
    }

    @Override
    public List getDataList() {
        return results;
    }

    @Override
    public boolean isSuccess() {
        return e == null;
    }
}
