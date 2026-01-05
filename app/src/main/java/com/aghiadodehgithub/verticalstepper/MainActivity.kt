package com.aghiadodehgithub.verticalstepper

import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.leodan11.xstepper.Stepper
import com.github.leodan11.xstepper.interfaces.IStepper
import com.github.leodan11.xstepper.interfaces.StepChangeListener
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val stepper: Stepper = findViewById(R.id.stepper)
        val nameInput: TextInputEditText = findViewById(R.id.name_input)
        val emailInput: TextInputEditText = findViewById(R.id.email_input)

        stepper.apply {
            setListener(object : IStepper {
                override fun onFinished() {
                    println("Stepper finished ✅")
                }

                override fun onStepOpening(step: Int) {
                    println("Opening step $step")
                }

                override fun onWaitingForOpen(step: Int) {
                    // if a step is need validation
                    when (stepper.activeStep) {
                        0 -> {
                            if (isNameValid(nameInput.text.toString())) {
                                nameInput.error = null
                                stepper.goToNextStep()
                            } else {
                                nameInput.error = "name is required"
                            }
                        }

                        1 -> {
                            if (emailInput.text.toString().isValidEmail()) {
                                emailInput.error = null
                                stepper.goToNextStep()
                            } else {
                                emailInput.error = "email is not valid"
                            }
                        }

                        else -> goToNextStep()
                    }
                }
            })
            addStepChangeListener(object : StepChangeListener {
                override fun onStepHidden(index: Int) {
                    println("Paso $index oculto")
                }

                override fun onStepRestored(index: Int) {
                    println("Paso $index restaurado")
                }
            })
        }

        stepper.addStep("Step 1") {
            it.addView(TextView(this).apply { text = "Content Step 1" })
        }

        stepper.addStep("Step 2") {
            it.addView(TextView(this).apply { text = "Content Step 2" })
        }

        stepper.addStep("Step 3") {
            it.addView(TextView(this).apply { text = "Content Step 3" })
        }

        stepper.hideStep(1)
        stepper.postDelayed({
            stepper.restoreStep(1)
        }, 8000)

    }

    fun isNameValid(name: String): Boolean {
        return name.isNotEmpty()
    }

    fun String.isValidEmail(): Boolean {
        return isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

}
