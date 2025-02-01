package com.github.leodan11.xstepper

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class StepContainer : LinearLayout {

    var title: String = String()
    var needValidation: Boolean = false

    constructor(context: Context) : super(context) {
        initAttributeSet(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttributeSet(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttributeSet(context, attrs, defStyleAttr, 0)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initAttributeSet(context, attrs, defStyleAttr, defStyleRes)
    }

    init {
        orientation = VERTICAL
    }

    private fun initAttributeSet(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        val attributes = context.obtainStyledAttributes(
            attrs,
            R.styleable.StepContainer,
            defStyleAttr,
            defStyleRes
        )
        try {
            title = attributes.getString(R.styleable.StepContainer_step_title) ?: title
            needValidation =
                attributes.getBoolean(R.styleable.StepContainer_need_validation, needValidation)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            attributes.recycle()
        }
    }

}