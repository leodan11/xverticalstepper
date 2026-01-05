package com.github.leodan11.xstepper.interfaces

import com.github.leodan11.xstepper.Stepper
import com.github.leodan11.xstepper.utils.hideSteps
import com.github.leodan11.xstepper.utils.restoreSteps


/**
 * Listener interface to observe structural changes in the [Stepper].
 *
 * Use this listener to be notified when steps are added, removed, moved, hidden, or restored.
 */
interface StepChangeListener {

    /**
     * Called when a new step is added to the Stepper.
     *
     * @param index The index at which the new step was inserted.
     */
    fun onStepAdded(index: Int) = Unit

    /**
     * Called when an existing step is removed from the Stepper.
     *
     * @param index The index of the step that was removed.
     */
    fun onStepRemoved(index: Int) = Unit

    /**
     * Called when a step is moved from one position to another within the Stepper.
     *
     * @param fromIndex The original index of the step.
     * @param toIndex The new index of the step after moving.
     */
    fun onStepMoved(fromIndex: Int, toIndex: Int) = Unit

    /**
     * Called when a step is hidden using [Stepper.hideStep] or [Stepper.hideSteps].
     *
     * @param index The index of the step that was hidden.
     */
    fun onStepHidden(index: Int) = Unit

    /**
     * Called when a previously hidden step is restored using [Stepper.restoreStep] or [Stepper.restoreSteps].
     *
     * @param index The index of the step that was restored.
     */
    fun onStepRestored(index: Int) = Unit
}
