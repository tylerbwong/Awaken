package com.example.tylerbwong.awaken.utilities;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author Connor Wong
 */
public class AnimatedRecyclerView extends RecyclerView {
    private boolean mScrollable;
    private boolean mIsAnimatable = true;

    public AnimatedRecyclerView(Context context) {
        this(context, null);
    }

    public AnimatedRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScrollable = false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return !mScrollable || super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mIsAnimatable) {
            for (int i = 0; i < getChildCount(); i++) {
                animate(getChildAt(i), i);

                if (i == getChildCount() - 1) {
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mScrollable = true;
                        }
                    }, i * 100);
                }
            }
        }
        mIsAnimatable = true;
    }

    public void setIsAnimatable(boolean isAnimatable) {
        this.mIsAnimatable = isAnimatable;
    }

    private void animate(View view, final int pos) {
        view.animate().cancel();
        view.setTranslationY(100);
        view.setAlpha(0);
        view.animate().alpha(1.0f).translationY(0).setDuration(300).setStartDelay(pos * 100);
    }
}
