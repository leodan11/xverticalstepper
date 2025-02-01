package com.github.leodan11.xstepper

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import androidx.core.view.children
import com.github.leodan11.xstepper.databinding.StepperLayoutBinding
import com.github.leodan11.xstepper.interfaces.IStepper
import com.github.leodan11.xstepper.models.StepModel
import com.github.leodan11.xstepper.utils.Animations
import com.github.leodan11.xstepper.utils.Variables

class Stepper : RelativeLayout {
    private lateinit var binding: StepperLayoutBinding
    private var iStepper: IStepper? = null
    private var stepModels = ArrayList<StepModel>()
    private var stepViews = ArrayList<StepView>()
    private var primaryColor = Color.GRAY
    private var textIndicatorColor = Color.WHITE
    private var defaultStepIndex = -1
    var activeStep = 0
    var previousStep = 0

    constructor(context: Context) : super(context) {
        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr, 0)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initStepViews()
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        // Load the styled attributes and set their properties
        val attributes =
            context.obtainStyledAttributes(attrs, R.styleable.Stepper, defStyleAttr, defStyleRes)
        try {
            binding = StepperLayoutBinding.inflate(LayoutInflater.from(context), this, true)
            primaryColor = attributes.getColor(R.styleable.Stepper_stepper_color_indicator, primaryColor)
            textIndicatorColor = attributes.getColor(R.styleable.Stepper_stepper_color_text_indicator, textIndicatorColor)
            defaultStepIndex = attributes.getInt(R.styleable.Stepper_stepper_opened_step_index, 0)
            Variables.primaryColor = primaryColor
            Variables.textIndicatorColor = textIndicatorColor
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            attributes.recycle()
        }
    }

    fun setListener(iStepper: IStepper) {
        this.iStepper = iStepper
    }

    private fun initStepViews() {
        for (x in 0 until childCount) {
            val child = children.elementAt(x)
            if (child is StepContainer) {
                val stepHeader = child.children.find { it is StepHeader }
                    ?.also { view -> child.removeView(view) }
                val stepModel = StepModel(
                    stepNumber = children.indexOf(child),
                    view = child,
                    header = stepHeader as StepHeader?
                )
                stepModels.add(stepModel)
            }
        }

        stepModels.forEach {
            removeView(it.view)
            val stepView = StepView(context).apply {
                layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                init(stepModel = it, iStepView = iStepView)
            }
            stepViews.add(stepView)
        }

        stepViews.forEach { binding.stepsParent.addView(it) }

        if (defaultStepIndex in 0 until stepViews.size) {
            goToStep(index = defaultStepIndex, firstAction = true)
        } else {
            for (x in 0 until stepViews.size) disableStep(index = x, animate = false)
        }
    }

    fun goToNextStep() {
        if (activeStep <= stepViews.size - 1) { // if an active step is not the last step
            for (x in activeStep + 1 until stepModels.size) { // get the first view with (appear == true)
                if (stepModels[x].appear) {
                    goToStep(index = x, allowed = true)
                    break
                }
            }
        }
    }

    fun goToStep(index: Int, allowed: Boolean = false, firstAction: Boolean = false) {
        val boolean =
            activeStep > index || firstAction || !stepModels[activeStep].view.needValidation || allowed
        if (boolean) {
            previousStep = activeStep
            activeStep = index
            for (x in 0 until stepViews.size)
                if (x == index) enableStep(index)
                else disableStep(x, !firstAction)
            if (index == stepViews.size - 1) iStepper?.onFinished()
        }
    }

    private fun disableStep(index: Int, animate: Boolean = true) {
        if (animate)
            Animations.slideUp(stepViews[index].binding.stepBody.root)
        else
            stepViews[index].binding.stepBody.root.visibility = GONE

        stepViews[index].stepClosed()
    }

    private fun enableStep(index: Int) {
        Animations.slideDown(stepViews[index].binding.stepBody.root)
        stepViews[index].stepOpened()
        iStepper?.onStepOpening(index)
    }

    fun removeStep(index: Int) {
        stepModels[index].appear = false
        stepViews[index].binding.root.visibility = View.GONE
        reOrderSteps()
    }

    fun restoreStep(index: Int) {
        stepModels[index].appear = true
        stepViews[index].binding.root.visibility = View.VISIBLE
        reOrderSteps()
    }

    fun completedStep(index: Int) {
        try {
            stepViews[index].stepCompleted()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun reOrderSteps() {
        var stepNumber = 0
        for (x in 0 until stepModels.size) {
            if (stepModels[x].appear) {
                stepViews[x].binding.stepHeader.stepNumber.text =
                    context.getString(R.string.integer_value, (stepNumber + 1))
                stepModels[x].stepNumber = stepNumber++
            }
        }
    }

    private val iStepView = StepView.IStepView { stepModel ->
        val index = stepModels.indexOf(stepModel)
        if (index < activeStep || isPreviousStepsCompleted(index)) goToStep(index)
        else iStepper?.onWaitingForOpen(index) // check if a current step is completed before go to another step
    }

    fun isPreviousStepsCompleted(index: Int): Boolean {
        var boolean = true
        for (x in index downTo 0) {
            val model = stepModels[x]
            if (model.view.needValidation && model.appear) {
                boolean = false
                break
            }
        }
        return boolean
    }

}