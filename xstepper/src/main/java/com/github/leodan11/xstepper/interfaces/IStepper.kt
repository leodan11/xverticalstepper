package com.github.leodan11.xstepper.interfaces

interface IStepper {
    /**
     * New step opened
     */
    fun onStepOpening(step: Int): Unit = Unit

    /**
     * Check if a current step is completed before go to another step
     *
     * @param step [Int]
     */
    fun onWaitingForOpen(step: Int)

    /**
     * Current step is last step
     */
    fun onFinished(): Unit = Unit

}
