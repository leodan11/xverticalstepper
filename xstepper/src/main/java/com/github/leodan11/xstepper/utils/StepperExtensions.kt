@file:JvmName("StepperExtensions")

package com.github.leodan11.xstepper.utils

import android.view.View
import android.widget.LinearLayout
import androidx.annotation.StringRes
import com.github.leodan11.xstepper.Stepper

/**
 * Hides multiple steps in a [Stepper] in a single call.
 *
 * The specified steps are marked as not visible and removed from the UI.
 * They are not permanently deleted and can be restored later using [Stepper.restoreStep].
 *
 * Example:
 * ```kotlin
 * stepper.hideSteps(1, 2, 3)
 * ```
 *
 * @param steps The indices of the steps to hide.
 * @throws IllegalArgumentException if any index is invalid.
 * @since 1.2.6
 */
@JvmSynthetic
fun Stepper.hideSteps(vararg steps: Int) {
    steps.forEach {
        require(it in 0..lastStepIndex) { "Index out of bounds: $it" }
        hideStep(it)
    }
}

/**
 * Restores multiple steps in a [Stepper] at once.
 *
 * Example:
 * ```kotlin
 * stepper.restoreSteps(1, 2, 3)
 * ```
 *
 * @param steps The indices of the steps to restore.
 * @throws IllegalArgumentException if any index is invalid.
 * @since 1.2.6
 */
@JvmSynthetic
fun Stepper.restoreSteps(vararg steps: Int) {
    steps.forEach {
        require(it in 0..lastStepIndex) { "Index out of bounds: $it" }
        restoreStep(it)
    }
}

/**
 * Moves a step to the last position in the stepper.
 *
 * @param index The index of the step to move.
 * @throws IllegalArgumentException if the index is invalid.
 * @since 1.2.6
 */
fun Stepper.moveStepToLast(index: Int) {
    require(index in 0..lastStepIndex) { "Index out of bounds: $index" }
    if (lastStepIndex >= 0) moveStep(index, lastStepIndex)
}

/**
 * Moves a step to the first position in the stepper.
 *
 * @param index The index of the step to move.
 * @throws IllegalArgumentException if the index is invalid.
 * @since 1.2.6
 */
fun Stepper.moveStepToFirst(index: Int) {
    require(index in 0..lastStepIndex) { "Index out of bounds: $index" }
    moveStep(index, 0)
}

/**
 * Inserts a step before or after a specific index.
 *
 * Internal helper used to reduce code duplication.
 */
private fun Stepper.insertStepAt(
    index: Int,
    title: String,
    needValidation: Boolean = false,
    content: View? = null,
    block: ((LinearLayout) -> Unit)? = null
) {
    require(index in 0..(stepCount)) { "Index out of bounds: $index" }
    when {
        content != null -> addStep(title, content, index, needValidation)
        block != null -> addStep(title, index, needValidation, block)
        else -> throw IllegalArgumentException("Either content or block must be provided")
    }
}

/** Insert step after a specific index with a View */
fun Stepper.insertStepAfter(index: Int, title: String, content: View, needValidation: Boolean = false) =
    insertStepAt(index + 1, title, needValidation, content = content)

/** Insert step before a specific index with a View */
fun Stepper.insertStepBefore(index: Int, title: String, content: View, needValidation: Boolean = false) =
    insertStepAt(index, title, needValidation, content = content)

/** Insert step after a specific index using a string resource */
fun Stepper.insertStepAfter(index: Int, @StringRes title: Int, content: View, needValidation: Boolean = false) =
    insertStepAfter(index, context.getString(title), content, needValidation)

/** Insert step before a specific index using a string resource */
fun Stepper.insertStepBefore(index: Int, @StringRes title: Int, content: View, needValidation: Boolean = false) =
    insertStepBefore(index, context.getString(title), content, needValidation)

/** Insert step after a specific index using a content block */
fun Stepper.insertStepAfter(index: Int, title: String, needValidation: Boolean = false, block: (LinearLayout) -> Unit) =
    insertStepAt(index + 1, title, needValidation, block = block)

/** Insert step before a specific index using a content block */
fun Stepper.insertStepBefore(index: Int, title: String, needValidation: Boolean = false, block: (LinearLayout) -> Unit) =
    insertStepAt(index, title, needValidation, block = block)

/** Insert step after a specific index using a string resource and a content block */
fun Stepper.insertStepAfter(index: Int, @StringRes title: Int, needValidation: Boolean = false, block: (LinearLayout) -> Unit) =
    insertStepAfter(index, context.getString(title), needValidation, block)

/** Insert step before a specific index using a string resource and a content block */
fun Stepper.insertStepBefore(index: Int, @StringRes title: Int, needValidation: Boolean = false, block: (LinearLayout) -> Unit) =
    insertStepBefore(index, context.getString(title), needValidation, block)
