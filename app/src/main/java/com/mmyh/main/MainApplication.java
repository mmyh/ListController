package com.mmyh.main;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.mmyh.listcontroller.ListController;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化参数依次为 this, AppId, AppKey
        AVObject.registerSubclass(Product.class);
        AVOSCloud.initialize(this, "oDG6UmBgxJ4tTBwW1oI9px9C-gzGzoHsz", "KjFjfvpdKmt5HwSioPbgEUcg");

//        ListController.setNetErrorView(R.layout.net_error_view, R.id.retry);
//        ListController.setNetOffView(R.layout.net_off_view, R.id.retry);
    }
}
