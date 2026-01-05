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
import com.github.leodan11.xstepper.interfaces.StepChangeListener
import com.github.leodan11.xstepper.models.StepModel
import com.github.leodan11.xstepper.utils.Animations
import com.github.leodan11.xstepper.utils.Variables

/**
 * A customizable stepper component for Android that manages a sequence of steps.
 *
 * Each step can have a title, content view, and optional validation. Steps can be
 * added, removed, moved, hidden, or restored dynamically.
 *
 * Example usage:
 * ```kotlin
 * val stepper = Stepper(context)
 * stepper.addStep("Step 1", myView)
 * stepper.addStep("Step 2", myOtherView, needValidation = true)
 * stepper.setListener(object : IStepper {
 *     override fun onFinished() { /* handle finish */ }
 * })
 * ```
 *
 * @constructor Creates a new Stepper with optional XML attributes.
 * @since 1.0.0
 */
class Stepper : RelativeLayout {

    private lateinit var binding: StepperLayoutBinding
    private var iStepper: IStepper? = null
    private val stepChangeListeners = mutableListOf<StepChangeListener>()
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

    val stepCount: Int
        get() = stepModels.size

    val lastStepIndex: Int
        get() = stepModels.lastIndex

    // Constructors
    constructor(context: Context) : super(context) { init(context, null, 0, 0) }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init(context, attrs, 0, 0) }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init(context, attrs, defStyleAttr, 0) }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) { init(context, attrs, defStyleAttr, defStyleRes) }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initStepViews()
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.Stepper, defStyleAttr, defStyleRes)
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
     * Sets the listener to receive stepper events like finishing or step opening.
     *
     * Example usage:
     * ```kotlin
     * stepper.setListener(object : IStepper {
     *     override fun onFinished() { /* do something */ }
     * })
     * ```
     *
     * @param iStepper Listener instance implementing [IStepper]
     * @since 1.0.0
     */
    fun setListener(iStepper: IStepper) { this.iStepper = iStepper }

    /**
     * Adds a listener to observe structural changes like step added/removed/moved.
     *
     * Example usage:
     * ```kotlin
     * stepper.addStepChangeListener(object : StepChangeListener {
     *     override fun onStepAdded(index: Int) { println("Added $index") }
     * })
     * ```
     *
     * @param listener Listener instance
     * @since 1.2.6
     */
    fun addStepChangeListener(listener: StepChangeListener) = stepChangeListeners.add(listener)

    /**
     * Removes a previously added [StepChangeListener].
     *
     * Example usage:
     * ```kotlin
     * stepper.removeStepChangeListener(listener)
     * ```
     *
     * @param listener Listener instance to remove
     * @since 1.2.6
     */
    fun removeStepChangeListener(listener: StepChangeListener) = stepChangeListeners.remove(listener)

    /**
     * Sets the default step index and navigates to it if available.
     *
     * Example usage:
     * ```kotlin
     * stepper.setDefaultStepIndex(0)
     * ```
     *
     * @param index Step index to set as default
     * @throws IllegalArgumentException if index is negative
     * @since 1.2.4
     */
    fun setDefaultStepIndex(@IntRange(0) index: Int) {
        require(index >= 0) { "Index must be >= 0" }
        defaultStepIndex = index
        if (stepViews.isNotEmpty() && index in stepViews.indices) goToStep(index, firstAction = true)
    }

    /**
     * Adds a step using a string resource and a content view.
     *
     * Example usage:
     * ```kotlin
     * stepper.addStep(R.string.step_title, myStepView)
     * ```
     *
     * @param title String resource ID for the step title
     * @param content View to display as step content
     * @param index Position to insert the step (defaults to last)
     * @param needValidation Whether the step requires validation
     * @since 1.2.4
     */
    @JvmOverloads
    fun addStep(@StringRes title: Int, content: View, index: Int = stepViews.size, needValidation: Boolean = false) {
        addStep(context.getString(title), content, index, needValidation)
    }

    /**
     * Adds a step using a string resource and a content view.
     *
     * Example usage:
     * ```kotlin
     * stepper.addStep(R.string.step_title, myStepView)
     * ```
     *
     * @param title String resource ID for the step title
     * @param index Position to insert the step (defaults to last)
     * @param needValidation Whether the step requires validation
     * @param block View to display as step content
     * @since 1.2.4
     */
    @JvmOverloads
    fun addStep(@StringRes title: Int, index: Int = stepViews.size, needValidation: Boolean = false, block: (LinearLayout) -> Unit) {
        addStep(context.getString(title), index, needValidation, block)
    }

    /**
     * Adds a step using a string resource and a content view.
     *
     * Example usage:
     * ```kotlin
     * stepper.addStep(R.string.step_title, myStepView)
     * ```
     *
     * @param title String resource ID for the step title
     * @param index Position to insert the step (defaults to last)
     * @param needValidation Whether the step requires validation
     * @param block View to display as step content
     * @since 1.2.4
     */
    @JvmOverloads
    fun addStep(title: String, index: Int = stepViews.size, needValidation: Boolean = false, block: (LinearLayout) -> Unit) {
        val contentLayout = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
        block(contentLayout)
        addStep(title, contentLayout, index, needValidation)
    }

    /**
     * Adds a step using a string title and content view.
     *
     * Example usage:
     * ```kotlin
     * stepper.addStep("Step 1", myStepView)
     * ```
     *
     * @param title Title of the step
     * @param content View to display as step content
     * @param index Position to insert the step (defaults to last)
     * @param needValidation Whether the step requires validation
     * @since 1.2.4
     */
    @JvmOverloads
    fun addStep(title: String, content: View, index: Int = stepViews.size, needValidation: Boolean = false) {
        require(index in 0..stepViews.size) { "Index out of bounds. Size: ${stepViews.size}, index: $index" }

        val stepContainer = StepContainer(content.context).apply {
            this.needValidation = needValidation
            addView(content)
        }

        val stepModel = StepModel(stepNumber = index, view = stepContainer, header = null)
        val stepView = StepView(content.context).apply {
            layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            init(stepModel = stepModel, iStepView = iStepView)
        }

        stepModels.add(index, stepModel)
        stepViews.add(index, stepView)
        binding.stepsParent.addView(stepView, index)
        setTitle(index, title)
        reOrderSteps()
        stepChangeListeners.forEach { it.onStepAdded(index) }

        if (defaultStepIndex in stepViews.indices) goToStep(defaultStepIndex, firstAction = true)
        else stepViews.indices.forEach { disableStep(it, animate = false) }
    }

    /**
     * Permanently removes a step from the Stepper.
     *
     * Example usage:
     * ```kotlin
     * stepper.removeStepAt(0) // Just delete
     * val removedView = stepper.removeStepAtReturningView(2) // Delete and return the deleted view.
     * ```
     *
     * @param index Index of the step to remove
     * @throws IllegalArgumentException if the index is out of bounds
     * @since 1.2.6
     */
    fun removeStepAt(index: Int) {
        removeStepAtReturningView(index)
    }

    /**
     * Permanently removes a step from the Stepper and returns its root view.
     *
     * This allows the caller to reuse or manipulate the removed step view.
     *
     * @param index Index of the step to remove
     * @return The removed step's root view
     * @throws IllegalArgumentException if the index is invalid
     * @since 1.2.6
     */
    fun removeStepAtReturningView(index: Int): View {
        require(index in stepModels.indices) { "Index out of bounds: $index" }
        activeStep = when {
            activeStep == index && index > 0 -> index - 1
            activeStep == index && index < stepModels.lastIndex -> index
            activeStep > index -> activeStep - 1
            else -> activeStep
        }
        previousStep = (previousStep - if (previousStep >= index) 1 else 0).coerceAtLeast(0)
        val removedView = stepViews[index].binding.root
        binding.stepsParent.removeView(stepViews[index])
        stepViews.removeAt(index)
        stepModels.removeAt(index)
        reOrderSteps()
        activeStep = activeStep.coerceIn(0, stepModels.lastIndex.coerceAtLeast(0))
        previousStep = previousStep.coerceIn(0, activeStep)
        stepChangeListeners.forEach { it.onStepRemoved(index) }
        return removedView
    }

    /**
     * Moves a step from one index to another.
     *
     * Example usage:
     * ```kotlin
     * stepper.moveStep(0, 2)
     * ```
     *
     * @param fromIndex Original index of the step
     * @param toIndex New index of the step
     * @throws IllegalArgumentException if any index is invalid
     * @since 1.2.6
     */
    fun moveStep(fromIndex: Int, toIndex: Int) {
        require(fromIndex in stepModels.indices) { "fromIndex out of bounds" }
        require(toIndex in stepModels.indices) { "toIndex out of bounds" }
        if (fromIndex == toIndex) return

        fun adjustIndex(value: Int): Int = when {
            value == fromIndex -> toIndex
            fromIndex < toIndex && value in (fromIndex + 1)..toIndex -> value - 1
            fromIndex > toIndex && value in toIndex until fromIndex -> value + 1
            else -> value
        }

        activeStep = adjustIndex(activeStep)
        previousStep = adjustIndex(previousStep)

        val stepModel = stepModels.removeAt(fromIndex)
        val stepView = stepViews.removeAt(fromIndex)
        stepModels.add(toIndex, stepModel)
        stepViews.add(toIndex, stepView)
        binding.stepsParent.removeView(stepView)
        binding.stepsParent.addView(stepView, toIndex)

        reOrderSteps()
        stepChangeListeners.forEach { it.onStepMoved(fromIndex, toIndex) }
    }

    /**
     * Temporarily hides a step from the UI.
     *
     * This step will not be permanently removed and can be restored later using [restoreStep].
     *
     * Previously, this functionality was provided by `removeStep`, which was renamed to
     * improve clarity. Use [restoreStep] to bring back the step if needed.
     *
     * Example usage:
     * ```kotlin
     * stepper.hideStep(1)
     * ```
     *
     * @param index Index of the step to hide
     * @throws IllegalArgumentException if the index is invalid
     * @since 1.2.6
     * @see restoreStep
     * @see removeStepAt
     */
    fun hideStep(index: Int) {
        require(index in stepModels.indices)
        stepModels[index].appear = false
        stepViews[index].binding.root.visibility = GONE
        reOrderSteps()
        stepChangeListeners.forEach { it.onStepHidden(index) }

        if (activeStep == index) goToNextStep()
    }

    /**
     * Restores a previously hidden step.
     *
     * Example usage:
     * ```kotlin
     * stepper.restoreStep(1)
     * ```
     *
     * @param index Index of the step to restore
     * @throws IllegalArgumentException if the index is invalid
     * @since 1.2.6
     */
    fun restoreStep(index: Int) {
        require(index in stepModels.indices)
        stepModels[index].appear = true
        stepViews[index].binding.root.visibility = VISIBLE
        reOrderSteps()
        stepChangeListeners.forEach { it.onStepRestored(index) }
    }

    /**
     * Navigates to the next visible step.
     *
     * Example usage:
     * ```kotlin
     * stepper.goToNextStep()
     * ```
     *
     * @since 1.2.6
     */
    fun goToNextStep() {
        for (x in activeStep + 1 until stepModels.size) {
            if (stepModels[x].appear) {
                goToStep(index = x, allowed = true)
                break
            }
        }
    }

    /**
     * Navigates to a specific step, respecting validation rules.
     *
     * Example usage:
     * ```kotlin
     * stepper.goToStep(2)
     * ```
     *
     * @param index Step index to navigate to
     * @param allowed If true, ignores validation requirements
     * @param firstAction Internal flag for first-time navigation
     * @throws IllegalArgumentException if index is invalid
     * @since 1.2.6
     */
    @JvmOverloads
    fun goToStep(index: Int, allowed: Boolean = false, firstAction: Boolean = false) {
        require(index in stepViews.indices)
        val canNavigate = activeStep > index || firstAction || !stepModels[activeStep].view.needValidation || allowed
        if (canNavigate) {
            previousStep = activeStep
            activeStep = index
            stepViews.indices.forEach { i -> if (i == index) enableStep(i) else disableStep(i, !firstAction) }
            if (index == stepViews.size - 1) iStepper?.onFinished()
        }
    }

    /**
     * Checks if all previous steps are completed.
     *
     * Example usage:
     * ```kotlin
     * val completed = stepper.isPreviousStepsCompleted(2)
     * ```
     *
     * @param index Step index to check
     * @return True if all previous steps are completed, false otherwise
     * @throws IllegalArgumentException if index is invalid
     * @since 1.2.6
     */
    fun isPreviousStepsCompleted(index: Int): Boolean {
        require(index in stepModels.indices)
        for (x in index - 1 downTo 0) {
            val model = stepModels[x]
            if (model.view.needValidation && model.appear) return false
        }
        return true
    }

    /**
     * Sets the title of a step.
     *
     * Example usage:
     * ```kotlin
     * stepper.setTitle(0, "Step 1")
     * ```
     *
     * @param index Index of the step to update
     * @param value New title for the step
     * @throws IllegalArgumentException if index is invalid
     * @since 1.2.1
     */
    fun setTitle(index: Int, value: String) {
        require(index in stepViews.indices)
        stepViews[index].stepTitle(stepModels[index], value)
    }

    /**
     * Sets the title of a step using a string resource.
     *
     * Example usage:
     * ```kotlin
     * stepper.setTitle(0, R.string.step_title)
     * ```
     *
     * @param index Index of the step to update
     * @param value String resource ID
     * @since 1.2.1
     */
    fun setTitle(index: Int, @StringRes value: Int) = setTitle(index, context.getString(value))

    /** StepView callback handler. */
    private val iStepView = StepView.IStepView { stepModel ->
        val index = stepModels.indexOf(stepModel)
        if (index < activeStep || isPreviousStepsCompleted(index)) goToStep(index)
        else iStepper?.onWaitingForOpen(index)
    }

    /** Disables a step visually. */
    private fun disableStep(index: Int, animate: Boolean = true) {
        if (animate) Animations.slideUp(stepViews[index].binding.stepBody.root)
        else stepViews[index].binding.stepBody.root.visibility = GONE
        stepViews[index].stepClosed()
    }

    /** Enables a step visually and notifies listener. */
    private fun enableStep(index: Int) {
        Animations.slideDown(stepViews[index].binding.stepBody.root)
        stepViews[index].stepOpened()
        iStepper?.onStepOpening(index)
    }

    /** Reorders step numbers for visible steps. */
    private fun reOrderSteps() {
        var stepNumber = 0
        for (x in 0 until stepModels.size) {
            if (stepModels[x].appear) {
                stepViews[x].binding.stepHeader.stepNumber.text = context.getString(R.string.integer_value, stepNumber + 1)
                stepModels[x].stepNumber = stepNumber++
            }
        }
    }

    /** Initializes step views from XML after inflation. */
    private fun initStepViews() {
        children.forEach { child ->
            if (child is StepContainer) {
                val stepHeader = child.children.find { it is StepHeader }?.also { child.removeView(it) }
                val stepModel = StepModel(stepNumber = children.indexOf(child), view = child, header = stepHeader as StepHeader?)
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

        if (defaultStepIndex in 0 until stepViews.size) goToStep(defaultStepIndex, firstAction = true)
        else stepViews.indices.forEach { disableStep(it, animate = false) }
    }

}
