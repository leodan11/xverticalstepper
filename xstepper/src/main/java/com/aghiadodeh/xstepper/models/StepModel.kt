package com.aghiadodeh.xstepper.models

import com.aghiadodeh.xstepper.StepContainer
import com.aghiadodeh.xstepper.StepHeader

data class StepModel(
    var stepNumber: Int,
    val view: StepContainer,
    val header: StepHeader? = null,
    var active: Boolean = false,
    var appear: Boolean = true
)