package com.mmyh.listcontroller;

import android.app.Activity;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mmyh.arthur.ArthurAdapter;
import com.mmyh.arthur.OnLoadMoreListener;
import com.mmyh.arthur.OnRefreshListener;

import java.lang.reflect.Field;

public class ListController {

    RecyclerView recyclerView;

    ArthurAdapter adapter;

    IQueryList iQueryList;

    Activity activity;

    IListRequest request;

    private boolean enableLoadMore = true;

    private QueryListType lastQueryListType;

    private View netOffView;

    private View netErrorView;

    private static int netOffViewId;

    private static int netOffViewRetryViewId;

    private static int netErrorViewId;

    private static int netErrorViewRetryViewId;

    public static ListController create(Activity activity) {
        ListController listController = new ListController();
        listController.activity = activity;
        if (activity instanceof IQueryList) {
            listController.iQueryList = (IQueryList) activity;
        } else {
            throw new RuntimeException(activity.getClass().getName() + " must implements IQueryList");
        }
        Field[] fields = activity.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                Object object = field.get(activity);
                if (object instanceof RecyclerView) {
                    listController.recyclerView = (RecyclerView) object;
                }
                if (object instanceof ArthurAdapter) {
                    listController.adapter = (ArthurAdapter) object;
                }
                if (object instanceof IListRequest) {
                    listController.request = (IListRequest) object;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        listController.init();
        return listController;
    }

    private ListController() {
    }

    public static void setNetOffView(@LayoutRes int viewId, @IdRes int retryViewId) {
        netOffViewId = viewId;
        netOffViewRetryViewId = retryViewId;
    }

    public static void setNetErrorView(@LayoutRes int viewId, @IdRes int retryViewId) {
        netErrorViewId = viewId;
        netErrorViewRetryViewId = retryViewId;
    }

    public ListController enablePullRefresh() {
        adapter.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                query(QueryListType.Refresh);
            }
        });
        return this;
    }

    public ListController disbalePullRefresh() {
        adapter.setOnRefreshListener(null);
        return this;
    }

    public ListController enableLoadMore() {
        enableLoadMore = true;
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                query(QueryListType.LoadMore);
            }
        });
        return this;
    }

    public ListController disbaleLoadMore() {
        enableLoadMore = false;
        adapter.setOnLoadMoreListener(null);
        return this;
    }

    public void start() {
        start(new LinearLayoutManager(activity));
    }

    public void start(@Nullable RecyclerView.LayoutManager layout) {
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);
        if (netOffView != null) {
            adapter.setFullScreenView(netOffView);
        }
        if (netErrorView != null) {
            adapter.setFullScreenView(netErrorView);
        }
        initQuery();
    }

    public void initQuery() {
        query(QueryListType.Init);
    }

    public void showNetOffView() {
        if (netOffView != null) {
            netOffView.setVisibility(View.VISIBLE);
        }
    }

    public void dismissNetOffView() {
        if (netOffView != null) {
            netOffView.setVisibility(View.GONE);
        }
    }

    public void showNetErrorView() {
        if (netErrorView != null) {
            netErrorView.setVisibility(View.VISIBLE);
        }
    }

    public void dismissNetErrorView() {
        if (netErrorView != null) {
            netErrorView.setVisibility(View.GONE);
        }
    }

    private void query(final QueryListType queryListType) {
        lastQueryListType = queryListType;
        if (QueryListType.Init.equals(queryListType) || QueryListType.Refresh.equals(queryListType)) {
            if (request != null) {
                request.resetPage();
            }
        } else if (QueryListType.LoadMore.equals(queryListType)) {
            if (request != null) {
                request.nextPage();
            }
        }
        if (iQueryList != null) {
            iQueryList.query(queryListType, new IQueryListCallback() {
                @Override
                public void onFinishQuery(IListResponse response, AbstractListErrorRule rule) {
                    adapter.finishRefresh();
                    adapter.finishLoadMore();
                    if (rule.isNetOff(activity)) {
                        showNetOffView();
                    } else if (rule.isNetError()) {
                        showNetErrorView();
                    } else {
                        dismissNetErrorView();
                        dismissNetOffView();
                        if (response.isSuccess()) {
                            adapter.updateData(response.getDataList(), QueryListType.LoadMore.equals(queryListType));
                            if (!response.hasNextPage()) {
                                adapter.setOnLoadMoreListener(null);
                                if (adapter.getDataCount() > 0) {
                                    adapter.getFootView(activity).setVisibility(View.VISIBLE);
                                }
                            } else {
                                adapter.getFootView(activity).setVisibility(View.GONE);
                            }
                            if (QueryListType.Init.equals(queryListType) || QueryListType.Refresh.equals(queryListType)) {
                                if (enableLoadMore) {
                                    enableLoadMore();
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private void init() {
        Object headView = iQueryList.getHeadView(adapter.getHeadView(activity));
        if (headView != null) {
            if (headView instanceof View) {
                adapter.addHeadView((View) headView);
            } else if (headView instanceof Integer) {
                adapter.addHeadView(activity.getLayoutInflater().inflate((Integer) headView, adapter.getHeadView(activity), false));
            }
        }
        Object footView = iQueryList.getFootView(adapter.getFootView(activity));
        if (footView != null) {
            if (footView instanceof View) {
                adapter.addFootView((View) footView);
            } else if (footView instanceof Integer) {
                adapter.addFootView(activity.getLayoutInflater().inflate((Integer) footView, adapter.getFootView(activity), false));
            }
            adapter.getFootView(activity).setVisibility(View.GONE);
        }
        if (netOffViewId != 0) {
            netOffView = activity.getLayoutInflater().inflate(netOffViewId, null);
            netOffView.findViewById(netOffViewRetryViewId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    query(lastQueryListType);
                }
            });
            netOffView.setVisibility(View.GONE);
        }
        if (netErrorViewId != 0) {
            netErrorView = activity.getLayoutInflater().inflate(netErrorViewId, null);
            netErrorView.findViewById(netErrorViewRetryViewId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    query(lastQueryListType);
                }
            });
            netErrorView.setVisibility(View.GONE);
        }
        enablePullRefresh();
        enableLoadMore();
    }


}
