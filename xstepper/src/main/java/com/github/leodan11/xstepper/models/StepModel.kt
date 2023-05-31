package com.github.leodan11.xstepper.models

import com.github.leodan11.xstepper.StepContainer
import com.github.leodan11.xstepper.StepHeader

data class StepModel(
    var stepNumber: Int,
    val view: StepContainer,
    val header: StepHeader? = null,
    var active: Boolean = false,
    var appear: Boolean = true
)