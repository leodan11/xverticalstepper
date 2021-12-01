package com.aghiadodeh.xstepper

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.aghiadodeh.xstepper.models.StepModel
import com.aghiadodeh.xstepper.utils.Variables
import com.aghiadodeh.xstepper.databinding.StepItemBinding

internal class StepView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {
    val binding: StepItemBinding = StepItemBinding.inflate(LayoutInflater.from(context), this, true)

    fun init(stepModel: StepModel, iStepView: IStepView) {
        binding.stepHeader.stepCircleView.circleColor = Variables.primaryColor
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
        binding.stepHeader.root.alpha = Variables.alphaOfDisabledElements
        binding.stepHeader.stepCheckIcon.visibility = View.GONE
        binding.stepHeader.stepNumber.visibility = View.VISIBLE
    }

    fun stepCompleted() {
        binding.stepHeader.root.alpha = Variables.alphaOfDisabledElements
        binding.stepHeader.stepCheckIcon.visibility = View.VISIBLE
        binding.stepHeader.stepNumber.visibility = View.GONE
    }

    private fun initStepHeader(stepModel: StepModel) {
        if (stepModel.header == null) {
            binding.stepHeader.stepTitle.text = stepModel.view.title
        } else {
            binding.stepHeader.wrapper.apply {
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
            binding.stepHeader.wrapper.removeAllViews()
            binding.stepHeader.wrapper.addView(stepModel.header)
        }
    }

    fun interface IStepView {
        fun onClick(stepModel: StepModel)
    }
}