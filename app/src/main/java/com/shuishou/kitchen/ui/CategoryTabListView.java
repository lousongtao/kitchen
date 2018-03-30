package com.shuishou.kitchen.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

public class CategoryTabListView extends ListView {

    private final String logTag = "TestTime-TabListView";
    private Integer lastOpenPosition = -1;

    public CategoryTabListView(Context context) {
        super(context);
        setOnScrollListener(new OnExpandableLayoutScrollListener());
    }

    public CategoryTabListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnScrollListener(new OnExpandableLayoutScrollListener());
    }

    public CategoryTabListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnScrollListener(new OnExpandableLayoutScrollListener());
    }

    @Override
    public boolean performItemClick(View view, int position, long id) {
        if (lastOpenPosition != position){
            chooseItemByPosition(position);
        } else {

        }

        return super.performItemClick(view, position, id);
    }

    public void chooseItemByPosition(int position){
        lastOpenPosition = position;

        for (int index = 0; index < getChildCount(); ++index) {
            if (index != (position - getFirstVisiblePosition())) {
                CategoryTabLayoutItem currentExpandableLayout = (CategoryTabLayoutItem) getChildAt(index).findViewWithTag(CategoryTabLayoutItem.class.getName());
                currentExpandableLayout.hide();
            }
        }
        View child = getChildAt(position - getFirstVisiblePosition());
        if (child != null){
            CategoryTabLayoutItem expandableLayout = (CategoryTabLayoutItem) child.findViewWithTag(CategoryTabLayoutItem.class.getName());
            if (expandableLayout.isOpened())
                expandableLayout.hide();
            else
                expandableLayout.show();
        }
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        if (!(l instanceof OnExpandableLayoutScrollListener))
            throw new IllegalArgumentException("OnScrollListner must be an OnExpandableLayoutScrollListener");

        super.setOnScrollListener(l);
    }

    public class OnExpandableLayoutScrollListener implements OnScrollListener {
        private int scrollState = 0;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            this.scrollState = scrollState;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (scrollState != SCROLL_STATE_IDLE) {
                for (int index = 0; index < getChildCount(); ++index) {
                    CategoryTabLayoutItem currentExpandableLayout = (CategoryTabLayoutItem) getChildAt(index).findViewWithTag(CategoryTabLayoutItem.class.getName());
                    if (currentExpandableLayout.isOpened() && index != (lastOpenPosition - getFirstVisiblePosition())) {
                        currentExpandableLayout.hideNow();
                    } else if (!currentExpandableLayout.getCloseByUser() && !currentExpandableLayout.isOpened() && index == (lastOpenPosition - getFirstVisiblePosition())) {
                        currentExpandableLayout.showNow();
                    }
                }
            }
        }
    }
}
