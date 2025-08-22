package com.github.leodan11.xstepper

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes

class StepContainer : LinearLayout {

    lateinit var title: String
        private set
    var needValidation: Boolean = false

    constructor(context: Context) : super(context) {
        initAttributeSet(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttributeSet(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttributeSet(context, attrs, defStyleAttr, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initAttributeSet(context, attrs, defStyleAttr, defStyleRes)
    }

    init { orientation = VERTICAL }

    fun setTitle(str: String) { this.title = str }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        context.withStyledAttributes(attrs, R.styleable.StepContainer, defStyleAttr, defStyleRes) {
            title = getString(R.styleable.StepContainer_step_title) ?: String()
            needValidation = getBoolean(R.styleable.StepContainer_need_validation, false)
        }
    }

}