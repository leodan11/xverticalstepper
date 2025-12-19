package com.github.leodan11.xstepper

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.IntRange
import androidx.annotation.StringRes
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
    var defaultStepIndex = -1
        private set
    var activeStep = 0
        private set
    var previousStep = 0
        private set

    constructor(context: Context) : super(context) {
        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initStepViews()
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

    /**
     * Sets the stepper event listener.
     *
     * This listener is used to receive callbacks related to step navigation
     * and completion events.
     *
     * @param iStepper Implementation of [IStepper] to handle stepper events.
     */
    fun setListener(iStepper: IStepper) {
        this.iStepper = iStepper
    }

    /**
     * Sets the default step index to be displayed when the steps are initialized.
     *
     * If the provided index is valid and there are already steps added,
     * the component will automatically navigate to the specified step.
     *
     * @param index The index of the step to be set as default. Must be greater
     * than or equal to 0.
     * @throws IllegalArgumentException If the index is out of bounds.
     * @since 1.2.4
     */
    fun setDefaultStepIndex(@IntRange(0) index: Int) {
        this.defaultStepIndex = index
        if (stepViews.isNotEmpty() && index in 0 until stepViews.size) {
            goToStep(index, firstAction = true)
        }
    }

    /**
     * Adds a new step using a string resource as the title and a custom content view.
     *
     * @param title String resource ID used as the step title.
     * @param content The view that represents the content of the step.
     * @param needValidation Indicates whether this step requires validation
     * before allowing navigation to the next step.
     * @since 1.2.4
     */
    @JvmOverloads
    fun addStep(@StringRes title: Int, content: View, needValidation: Boolean = false) {
        addStep(title = context.getString(title), content = content, needValidation = needValidation)
    }

    /**
     * Adds a new step using a string resource as the title and a layout block
     * to define its content.
     *
     * @param titleRes String resource ID used as the step title.
     * @param needValidation Indicates whether this step requires validation.
     * @param block Lambda used to populate the step content inside a [LinearLayout].
     * @since 1.2.4
     */
    @JvmOverloads
    fun addStep(@StringRes titleRes: Int, needValidation: Boolean = false, block: (LinearLayout) -> Unit) {
        addStep(title = context.getString(titleRes), needValidation = needValidation, block = block)
    }

    /**
     * Adds a new step using a string title and a layout block to define its content.
     *
     * A vertical [LinearLayout] is created internally and passed to the block
     * to allow adding custom views.
     *
     * @param title The title of the step.
     * @param needValidation Indicates whether this step requires validation.
     * @param block Lambda used to add views to the step content layout.
     * @since 1.2.4
     */
    @JvmOverloads
    fun addStep(title: String, needValidation: Boolean = false, block: (LinearLayout) -> Unit) {
        val contentLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }
        block.invoke(contentLayout)
        addStep(title = title, content = contentLayout, needValidation = needValidation)
    }

    /**
     * Adds a new step to the component.
     *
     * This method creates the internal step container, associates it with
     * a [StepModel] and [StepView], and updates the UI accordingly.
     *
     * @param title The title of the step.
     * @param content The view that represents the step content.
     * @param needValidation Indicates whether this step requires validation
     * before proceeding to the next step.
     * @since 1.2.4
     */
    @JvmOverloads
    fun addStep(title: String, content: View, needValidation: Boolean = false) {
        val stepContainer = StepContainer(context).apply {
            this.needValidation = needValidation
        }
        stepContainer.addView(content)
        val stepModel = StepModel(
            stepNumber = stepModels.size,
            view = stepContainer,
            header = null
        )
        stepModels.add(stepModel)
        val stepView = StepView(context).apply {
            layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            init(stepModel = stepModel, iStepView = iStepView)
        }
        stepViews.add(stepView)
        binding.stepsParent.addView(stepView)
        setTitle(stepViews.size - 1, title)
        reOrderSteps()
        if (defaultStepIndex in 0 until stepViews.size) {
            goToStep(defaultStepIndex, firstAction = true)
        } else {
            for (i in 0 until stepViews.size) disableStep(i, animate = false)
        }
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

    /**
     * Navigates to the next available step.
     *
     * This method searches for the next step marked as visible (`appear == true`)
     * after the current active step and navigates to it if found.
     */
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

    /**
     * Navigates to a specific step by index.
     *
     * Navigation is allowed if:
     * - The target step is before the current step, or
     * - This is the first navigation action, or
     * - The current step does not require validation, or
     * - Navigation is explicitly allowed.
     *
     * When the last step is reached, the [IStepper.onFinished] callback is triggered.
     *
     * @param index The index of the step to navigate to.
     * @param allowed Forces navigation even if validation is required.
     * @param firstAction Indicates whether this is the initial navigation action.
     * @throws IndexOutOfBoundsException If the index is invalid.
     */
    @JvmOverloads
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

    /**
     * Removes a step from the navigation flow.
     *
     * The step is not permanently deleted; instead, it is marked as not visible
     * and removed from the UI.
     *
     * @param index The index of the step to remove.
     * @throws IndexOutOfBoundsException If the index is invalid.
     */
    fun removeStep(index: Int) {
        stepModels[index].appear = false
        stepViews[index].binding.root.visibility = GONE
        reOrderSteps()
    }

    /**
     * Restores a previously removed step.
     *
     * The step becomes visible again and is reintegrated into the step order.
     *
     * @param index The index of the step to restore.
     * @throws IndexOutOfBoundsException If the index is invalid.
     */
    fun restoreStep(index: Int) {
        stepModels[index].appear = true
        stepViews[index].binding.root.visibility = VISIBLE
        reOrderSteps()
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

    /**
     * Sets the title of a step using a string resource.
     *
     * @param index The index of the step whose title will be updated.
     * @param value String resource ID representing the new title.
     * @since 1.2.1
     */
    fun setTitle(index: Int, @StringRes value: Int) {
        this.setTitle(index, context.getString(value))
    }

    /**
     * Sets the title of a step.
     *
     * @param index The index of the step whose title will be updated.
     * @param value The new title for the step.
     * @throws IndexOutOfBoundsException If the index is invalid.
     * @since 1.2.1
     */
    fun setTitle(index: Int, value: String) {
        val view = stepViews[index]
        view.stepTitle(stepModels[index], value)
    }

    /**
     * Returns the total number of steps currently added.
     *
     * @return The number of steps.
     * @since 1.2.1
     */
    fun stepSize() : Int {
        return stepModels.size
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