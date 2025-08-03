@file:JvmName("StepperExtensions")

package com.github.leodan11.xstepper.utils

import com.github.leodan11.xstepper.Stepper

/**
 * Removes multiple steps from a [Stepper] in a single call.
 *
 * This utility extension simplifies removing several steps by calling [Stepper.removeStep] for each
 * provided index. It's useful for cleanup or conditional UI flows where multiple steps
 * must be removed together.
 *
 * Example usage:
 * ```
 * stepperLayout.removeSteps(1, 2, 3, 4)
 * ```
 *
 * @param steps The step indices to remove from the stepper.
 */
@JvmSynthetic
fun Stepper.removeSteps(vararg steps: Int) {
    steps.forEach { removeStep(it) }
}


/**
 * Restores multiple steps in the [Stepper] at once.
 *
 * This extension function enhances code readability and reduces repetition
 * by enabling the restoration of multiple steps through a single method call.
 *
 * Example usage:
 * ```
 * stepperLayout.restoreSteps(1, 2, 3, 4)
 * ```
 *
 * @param steps Vararg parameter representing the indices of the steps to restore.
 *
 * @throws IllegalArgumentException if any step index is invalid or out of bounds
 *         (depending on the underlying implementation of [Stepper.restoreStep]).
 *
 * @see Stepper.restoreStep
 */
@JvmSynthetic
fun Stepper.restoreSteps(vararg steps: Int) {
    steps.forEach { restoreStep(it) }
}