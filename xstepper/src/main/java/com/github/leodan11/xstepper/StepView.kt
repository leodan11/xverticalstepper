package com.github.leodan11.xstepper

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.github.leodan11.xstepper.databinding.StepItemBinding
import com.github.leodan11.xstepper.models.StepModel
import com.github.leodan11.xstepper.utils.Variables

internal class StepView : FrameLayout {

    lateinit var binding: StepItemBinding

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

    private fun initAttributeSet(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        val binding: StepItemBinding =
            StepItemBinding.inflate(LayoutInflater.from(context), this, true)
        this.binding = binding
    }

    fun init(stepModel: StepModel, iStepView: IStepView) {
        binding.stepHeader.stepCircleView.circleColor = Variables.primaryColor
        binding.stepHeader.stepNumber.setTextColor(ColorStateList.valueOf(Variables.textIndicatorColor))
        binding.stepHeader.stepNumber.text = "${stepModel.stepNumber}"
        binding.stepHeader.headerRootView.setOnClickListener { iStepView.onClick(stepModel) }
        binding.stepBody.stepContainer.addView(stepModel.view)
        initStepHeader(stepModel)
    }

    fun stepOpened() {
        binding.stepHeader.root.alpha = 1F
        binding.stepHeader.stepCheckIcon.visibility = View.GONE
        binding.stepHeader.stepNumber.visibility = View.VISIBLE
    }

    fun stepClosed() {
        binding.stepHeader.root.alpha = Variables.ALPHA_OF_DISABLED_ELEMENTS
        binding.stepHeader.stepCheckIcon.visibility = View.GONE
        binding.stepHeader.stepNumber.visibility = View.VISIBLE
    }

    fun stepCompleted() {
        binding.stepHeader.root.alpha = Variables.ALPHA_OF_DISABLED_ELEMENTS
        binding.stepHeader.stepCheckIcon.visibility = View.VISIBLE
        binding.stepHeader.stepNumber.visibility = View.GONE
    }

    private fun initStepHeader(stepModel: StepModel) {
        if (stepModel.header == null) {
            binding.stepHeader.stepTitle.text = stepModel.view.title
        } else {
            binding.stepHeader.wrapper.apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            binding.stepHeader.wrapper.removeAllViews()
            binding.stepHeader.wrapper.addView(stepModel.header)
        }
    }

    fun interface IStepView {
        fun onClick(stepModel: StepModel)
    }
}