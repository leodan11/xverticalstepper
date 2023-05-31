package com.github.leodan11.xstepper

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class StepHeader(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    init {
        orientation = HORIZONTAL
        initAttributeSet(attrs)
    }

    private fun initAttributeSet(attrs: AttributeSet?) = Unit

}