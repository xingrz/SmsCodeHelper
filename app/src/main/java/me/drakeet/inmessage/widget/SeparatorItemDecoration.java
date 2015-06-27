package me.drakeet.inmessage.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import me.drakeet.inmessage.R;
import me.drakeet.inmessage.adapter.MainMessageAdapter;
import me.drakeet.inmessage.utils.VersionUtils;

public class SeparatorItemDecoration extends RecyclerView.ItemDecoration {

    private static final int DIVIDER_SIZE_PX = 1;

    private final Paint mDivider;

    private final Drawable mShadow;

    public SeparatorItemDecoration(Context context) {
        mDivider = new Paint();
        mDivider.setColor(context.getResources().getColor(R.color.line_gray));
        mDivider.setStyle(Paint.Style.STROKE);
        mDivider.setStrokeWidth(DIVIDER_SIZE_PX);

        mShadow = context.getResources().getDrawable(R.drawable.shadow);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            if (shouldDrawDivider(parent, i, childCount)) {
                c.drawLine(0, child.getBottom(), c.getWidth(), child.getBottom(), mDivider);
            }

            if (shouldDrawShadow(parent, i)) {
                mShadow.setBounds(0, child.getTop(), c.getWidth(), child.getTop() + mShadow.getIntrinsicHeight());
                mShadow.draw(c);
            }
        }
    }

    private MainMessageAdapter.ITEM_TYPE getItemType(View child, RecyclerView parent) {
        int adapterPosition = parent.getChildAdapterPosition(child);
        int itemType = parent.getAdapter().getItemViewType(adapterPosition);
        return MainMessageAdapter.ITEM_TYPE.values()[itemType];
    }

    private boolean shouldDrawDivider(RecyclerView parent, int i, int childCount) {
        View child = parent.getChildAt(i);

        // 对于 Lollipop 以上，因为 message item 有 elevation，不绘制 date item 底下的分割线
        if (VersionUtils.IS_MORE_THAN_LOLLIPOP && getItemType(child, parent)
                == MainMessageAdapter.ITEM_TYPE.ITEM_TYPE_DATE) {
            return false;
        }

        // 总是不绘制 date item 上一个 message item 底下的分割线，因为有 elevation 或假阴影
        if (i < childCount - 1 && getItemType(parent.getChildAt(i + 1), parent)
                == MainMessageAdapter.ITEM_TYPE.ITEM_TYPE_DATE) {
            return false;
        }

        return true;
    }

    private boolean shouldDrawShadow(RecyclerView parent, int i) {
        return !VersionUtils.IS_MORE_THAN_LOLLIPOP
                && i > 0
                && getItemType(parent.getChildAt(i - 1), parent) == MainMessageAdapter.ITEM_TYPE.ITEM_TYPE_MESSAGE
                && getItemType(parent.getChildAt(i), parent) == MainMessageAdapter.ITEM_TYPE.ITEM_TYPE_DATE;
    }

}
