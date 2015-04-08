package com.fenchtose.ormlitedemo.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Jay Rambhia on 20/03/15.
 */
public abstract class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    private int mScrollThreshold = 40;
    private int scrolledDistance = 0;
    private static final int HIDE_THRESHOLD = 20;
    private LinearLayoutManager mLinearLayoutManager;

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 2; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private boolean infiniteScrollingEnabled = true;

    private boolean controlsVisible = true;

    public RecyclerViewScrollListener(){

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (mLinearLayoutManager == null) {
            mLinearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        }

        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();

        if (infiniteScrollingEnabled) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }

            if (!loading && (totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold)) {
                // End has been reached
                // do something
                onLoadMore();
                loading = true;
            }
        }

        if (firstVisibleItem == 0) {
            if (!controlsVisible) {
                onScrollUp();
                controlsVisible = true;
            }

            return;
        }

        if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
            onScrollDown();
            controlsVisible = false;
            scrolledDistance = 0;
        } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
            onScrollUp();
            controlsVisible = true;
            scrolledDistance = 0;
        }

        if ((controlsVisible && dy>0) || (!controlsVisible && dy <0)) {
            scrolledDistance+=dy;
        }
    }

    abstract void onScrollUp();
    abstract void onScrollDown();
    abstract void onLoadMore();

    public void setScrollThreshold(int scrollThreshold) {
        mScrollThreshold = scrollThreshold;
    }

    public void stopInfiniteScrolling() {
        infiniteScrollingEnabled = false;
    }
}
