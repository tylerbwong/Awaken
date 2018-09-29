package io.awaken.ui.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

import androidx.recyclerview.widget.RecyclerView

/**
 * @author Connor Wong
 */
class AnimatedRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {
    private var scrollable = false
    private var isAnimatable = true

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return !scrollable || super.dispatchTouchEvent(ev)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (isAnimatable) {
            for (i in 0 until childCount) {
                animate(getChildAt(i), i)

                if (i == childCount - 1) {
                    handler.postDelayed({ scrollable = true }, (i * 100).toLong())
                }
            }
        }
        isAnimatable = true
    }

    fun setIsAnimatable(isAnimatable: Boolean) {
        this.isAnimatable = isAnimatable
    }

    private fun animate(view: View, pos: Int) {
        view.animate().cancel()
        view.translationY = 100f
        view.alpha = 0f
        view.animate().alpha(1.0f).translationY(0f).setDuration(300).startDelay = (pos * 100).toLong()
    }
}
