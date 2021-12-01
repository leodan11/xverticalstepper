package com.aghiadodeh.xstepper

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class StepContainer(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    var title: String = ""
    var needValidation: Boolean = false
    var attributes: AttributeSet? = null

    init {
        orientation = VERTICAL
        attributes = attrs
        initAttributeSet(attrs)
    }

    private fun initAttributeSet(attrs: AttributeSet?) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.StepContainer, 0, 0)
        attributes.getString(R.styleable.StepContainer_step_title)?.let {
            title = it
        }
        needValidation = attributes.getBoolean(R.styleable.StepContainer_need_validation, needValidation)
        attributes.recycle()
    }
}