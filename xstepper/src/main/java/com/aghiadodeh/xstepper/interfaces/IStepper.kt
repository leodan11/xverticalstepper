package com.aghiadodeh.xstepper.interfaces

interface IStepper {
    fun onStepOpening(step: Int) // new step opened
    fun onWaitingForOpen(step: Int) // check if current step is completed before go to other step
    fun onFinished() // current step is last step
}