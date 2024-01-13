package com.github.leodan11.xstepper.interfaces

interface IStepper {
    fun onStepOpening(step: Int) // new step opened
    fun onWaitingForOpen(step: Int) // check if a current step is completed before go to another step
    fun onFinished() // current step is last step
}