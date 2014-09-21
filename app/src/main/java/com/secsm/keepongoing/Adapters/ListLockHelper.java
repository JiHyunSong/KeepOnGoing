package com.secsm.keepongoing.Adapters;

import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by JinS on 2014. 9. 21..
 */
public class ListLockHelper {

    private Request request;
    private boolean isListLocked = false;
    private ListView list;

    public ListLockHelper(ListView list) {
        this.list = list;
        setOnScrollListener(null);
    }

    public ListLockHelper(ListView list, Request request) {
        this.list = list;
        this.request = request;
        setOnScrollListener(null);
    }

    public ListLockHelper(ListView list, AbsListView.OnScrollListener scrollListener) {
        this.list = list;
        setOnScrollListener(scrollListener);
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    private void setOnScrollListener(final AbsListView.OnScrollListener scrollListener) {
        list.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int preFirstVisiblePosition = list.getFirstVisiblePosition();

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollListener != null) {
                    scrollListener.onScrollStateChanged(view, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (scrollListener != null) {
                    scrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }

                Log.i("listLockHelper", "totalItemCount : " + totalItemCount);
                Log.i("listLockHelper", "firstVisibleItem : " + firstVisibleItem);
                Log.i("listLockHelper", "preLastVisiblePosition : " + preFirstVisiblePosition);
                Log.i("listLockHelper", "view.getLastVisiblePosition() : " + view.getLastVisiblePosition());

                Log.i("listLockHelper", "firstVisibleItem <= 0 : " + (firstVisibleItem <= 0));
                Log.i("listLockHelper", "totalItemCount != 0 : " + (totalItemCount != 0));
                Log.i("listLockHelper", "!isListLocked : " + (!isListLocked));
                Log.i("listLockHelper", "preLastVisiblePosition != view.getLastVisiblePosition() : " + (preFirstVisiblePosition != view.getLastVisiblePosition()));
                Log.i("listLockHelper", "preLastVisiblePosition != view.getLastVisiblePosition() : " + (preFirstVisiblePosition != view.getFirstVisiblePosition()));
                int count = totalItemCount - visibleItemCount;
                if (firstVisibleItem <= 0 && totalItemCount != 0 && !isListLocked &&
                        preFirstVisiblePosition != view.getFirstVisiblePosition() && request != null) {
                    request.request();
                }

                preFirstVisiblePosition = view.getFirstVisiblePosition();

            }
        });
    }

    public void setListLockOn() {
        isListLocked = true;
    }

    public void setListLockOff() {
        isListLocked = false;
    }

    public interface Request {
        public void request();
    }
}
