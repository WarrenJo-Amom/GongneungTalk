package com.maeultalk.gongneungtalk.contents.setting;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

public class RecyclerViewDecoration extends RecyclerView.ItemDecoration {
    private final int divHeightPX;
    Context context;

    public RecyclerViewDecoration(Context context, int divHeightDP) {
        this.context = context;
        this.divHeightPX = dpToPx(context, divHeightDP);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        //outRect.top = divHeightPX;
        outRect.bottom = divHeightPX;
        //outRect.left = divHeightPX;
        //outRect.right = divHeightPX;
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = divHeightPX;
        } else {
            outRect.top = 0;
        }
    }

    /**
     * DP 를 픽셀로 변환하는 메소드.
     * @param dp dp
     * @return dp 에서 변환된 픽셀 값.
     */
    private int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}