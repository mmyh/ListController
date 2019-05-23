package com.mmyh.main;

import com.avos.avoscloud.AVException;
import com.mmyh.listcontroller.AbstractListErrorRule;

public class ListErrorRule extends AbstractListErrorRule {

    AVException exception;

    public ListErrorRule(AVException e) {
        exception = e;
    }

    @Override
    public boolean isNetError() {
        return exception != null && exception.getCode() == 0;
    }
}
