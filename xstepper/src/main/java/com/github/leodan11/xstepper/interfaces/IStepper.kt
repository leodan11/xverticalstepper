package com.github.leodan11.xstepper.interfaces

/**
 * Interface that defines callbacks for a step-based navigation component (e.g., a stepper or wizard).
 *
 * This interface allows implementing classes to respond to changes in step state,
 * perform validations, and handle completion events when the user finishes the steps.
 */
interface IStepper {

    /**
     * Called when a new step is being opened.
     *
     * This is a good place to update the UI or load any data needed for the specified step.
     *
     * @param step The index of the step being opened.
     */
    fun onStepOpening(step: Int): Unit = Unit

    /**
     * Called before navigating to another step, to validate whether the current step is completed.
     *
     * This method should contain any logic required to determine if the current step can be left,
     * such as form validation or user input checks.
     *
     * @param step The index of the step that is being attempted to open.
     */
    fun onWaitingForOpen(step: Int): Unit = Unit

    /**
     * Called when the current step is the final one and the process is considered finished.
     *
     * Use this callback to perform any final actions such as submitting data, showing a confirmation,
     * or navigating away from the stepper.
     */
    fun onFinished(): Unit = Unit

}
