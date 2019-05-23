package com.mmyh.main;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

@AVClassName("Product")
public class Product extends AVObject {

    private String name;

    public void setName(String name) {
        put("name", name);
    }

    public String getName() {
        return getString("name");
    }
}
