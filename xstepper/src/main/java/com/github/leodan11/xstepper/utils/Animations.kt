package com.github.leodan11.xstepper.utils

import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout

// http://stackoverflow.com/a/13381228/3891038 + modifications
object Animations {
    fun slideDown(v: View) {
        if (v.visibility != View.VISIBLE) {
            v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            val targetHeight = v.measuredHeight

            // Older versions of android (pre API 21) cancel animations for views with a height of 0.
            setHeight(v, 1)
            v.visibility = View.VISIBLE
            val a: Animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    val newHeight =
                        if (interpolatedTime == 1f) WindowManager.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                    setHeight(v, newHeight)
                    v.requestLayout()
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }
            a.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    v.visibility = View.VISIBLE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })

            // 0.5dp/ms
            a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt() / 2.toLong()
            v.startAnimation(a)
        }
    }

    fun slideUp(v: View) {
        if (v.visibility == View.VISIBLE) {
            val initialHeight = v.measuredHeight
            val a: Animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    if (interpolatedTime != 1f) {
                        val newHeight = initialHeight - (initialHeight * interpolatedTime).toInt()
                        setHeight(v, newHeight)
                        v.requestLayout()
                    }
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }
            a.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    v.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })

            // 0.5dp/ms
            a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt() / 2.toLong()
            v.startAnimation(a)
        }
    }

    internal fun setHeight(v: View, newHeight: Int) {
        //v.getLayoutParams().height = newHeight;
        v.layoutParams = LinearLayout.LayoutParams(v.layoutParams.width, newHeight)
    }
}