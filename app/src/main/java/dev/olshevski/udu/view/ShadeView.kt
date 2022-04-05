package dev.olshevski.udu.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.View
import dev.olshevski.udu.R

class ShadeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var shown = true

    /**
     * Same as navigation animation duration.
     */
    private val animationTime = resources.getInteger(R.integer.shade_view_anim_duration).toLong()

    /**
     * Show the shade view, animating it if necessary.
     */
    fun show() {
        if (!shown) {
            shown = true
            if (isLaidOut) {
                animate()
                    .alpha(1f)
                    .setDuration(animationTime)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {
                            visibility = VISIBLE
                        }
                    })
            } else {
                // view hasn't been shown yet, apply values straight away
                alpha = 1f
                visibility = VISIBLE
            }
        }
    }

    /**
     * Hide the shade view, animating it if necessary.
     */
    fun hide() {
        if (shown) {
            shown = false
            if (isLaidOut) {
                animate()
                    .alpha(0f)
                    .setDuration(animationTime)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            visibility = INVISIBLE
                        }
                    })
            } else {
                // view hasn't been shown yet, apply values straight away
                alpha = 0f
                visibility = INVISIBLE
            }
        }
    }

}