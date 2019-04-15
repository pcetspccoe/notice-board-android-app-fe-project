package com.pccoedevelopers.noticeboard;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecyclerDecoration extends RecyclerView.ItemDecoration {
    int sidePadding;
    int topPadding;

    public RecyclerDecoration(int sidePadding, int topPadding) {
        this.sidePadding = sidePadding;
        this.topPadding = topPadding;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = topPadding;
        outRect.top = topPadding;

        outRect.left = sidePadding;
        outRect.right = sidePadding;
    }
}
