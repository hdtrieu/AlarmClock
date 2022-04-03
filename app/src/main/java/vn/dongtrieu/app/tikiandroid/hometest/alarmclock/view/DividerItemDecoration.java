package vn.dongtrieu.app.tikiandroid.hometest.alarmclock.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import vn.dongtrieu.app.tikiandroid.hometest.R;
import vn.dongtrieu.app.tikiandroid.hometest.alarmclock.ViewUtilsKt;

public final class DividerItemDecoration extends RecyclerView.ItemDecoration {

    @SuppressLint("RestrictedApi")
    private static final int DIVIDER_HEIGHT = (int) ViewUtilsKt.dpToPx(1);
    private final Drawable mDivider;

    public DividerItemDecoration(Context context) {
        this(context, null);
    }

    public DividerItemDecoration(Context context, Drawable divider) {
        mDivider = (divider == null)
                ? ContextCompat.getDrawable(context, R.drawable.divider)
                : divider;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if(parent.getChildAdapterPosition(view) == 0) return;

        outRect.top = DIVIDER_HEIGHT;

    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);

        final int dividerLeft = parent.getPaddingLeft();
        final int dividerRight = parent.getWidth() - parent.getPaddingRight();

        final int count = parent.getChildCount();

        for (int i = 0; i < count; i++) {

            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params =
                    (RecyclerView.LayoutParams) child.getLayoutParams();

            final int dividerTop = child.getBottom() + params.bottomMargin;
            final int dividerBottom = dividerTop + DIVIDER_HEIGHT;

            mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
            mDivider.draw(c);

        }
    }
}
