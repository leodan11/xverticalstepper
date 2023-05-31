package com.github.leodan11.xstepper.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import java.util.*

class KeyboardUtils(act: Activity, listener: SoftKeyboardToggleListener?) : OnGlobalLayoutListener {
    private val MAGIC_NUMBER = 200

    private var mCallback: SoftKeyboardToggleListener? = listener
    private var mRootView: View? = null
    private var prevValue: Boolean? = null
    private var mScreenDensity = 0f
    private val sListenerMap =
        HashMap<SoftKeyboardToggleListener, KeyboardUtils?>()

    init {
        mRootView = (act.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
        mRootView!!.viewTreeObserver.addOnGlobalLayoutListener(this)
        mScreenDensity = act.resources.displayMetrics.density
    }

    interface SoftKeyboardToggleListener {
        fun onToggleSoftKeyboard(isVisible: Boolean)
    }


    override fun onGlobalLayout() {
        val r = Rect()
        mRootView!!.getWindowVisibleDisplayFrame(r)
        val heightDiff = mRootView?.rootView!!.height - (r.bottom - r.top)
        val dp = heightDiff / mScreenDensity
        val isVisible = dp > MAGIC_NUMBER
        if (mCallback != null && (prevValue == null || isVisible != prevValue)) {
            prevValue = isVisible
            mCallback!!.onToggleSoftKeyboard(isVisible)
        }
    }

    /**
     * Add a new keyboard listener
     * @param act calling activity
     * @param listener callback
     */
    fun addKeyboardToggleListener(
        act: Activity,
        listener: SoftKeyboardToggleListener
    ) {
        removeKeyboardToggleListener(listener)
        sListenerMap.put(listener, KeyboardUtils(act, listener))
    }

    /**
     * Remove a registered listener
     * @param listener [SoftKeyboardToggleListener]
     */
    fun removeKeyboardToggleListener(listener: SoftKeyboardToggleListener?) {
        if (sListenerMap.containsKey(listener)) {
            val k = sListenerMap[listener]
            k!!.removeListener()
            sListenerMap.remove(listener)
        }
    }

    /**
     * Remove all registered keyboard listeners
     */
    fun removeAllKeyboardToggleListeners() {
        for (l in sListenerMap.keys) sListenerMap[l]!!.removeListener()
        sListenerMap.clear()
    }

    /**
     * Manually toggle soft keyboard visibility
     * @param context calling context
     */
    fun toggleKeyboardVisibility(context: Context) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            0
        )
    }

    /**
     * Force closes the soft keyboard
     * @param activeView the view with the keyboard focus
     */
    fun forceCloseKeyboard(activeView: View) {
        val inputMethodManager =
            activeView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            activeView.windowToken,
            0
        )
    }

    private fun removeListener() {
        mCallback = null
        mRootView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

   /* fun KeyboardUtils(act: Activity, listener: SoftKeyboardToggleListener?) {
        mCallback = listener
        mRootView =
            (act.findViewById<View>(R.id.content) as ViewGroup).getChildAt(0)
        mRootView!!.getViewTreeObserver().addOnGlobalLayoutListener(this)
        mScreenDensity = act.resources.displayMetrics.density
    }
*/
}