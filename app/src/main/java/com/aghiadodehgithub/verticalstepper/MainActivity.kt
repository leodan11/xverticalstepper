package com.aghiadodehgithub.verticalstepper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.leodan11.xstepper.Stepper
import com.github.leodan11.xstepper.interfaces.IStepper
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

        stepper.setListener(object : IStepper {
            override fun onWaitingForOpen(step: Int) {
                // if a step is need validation
                when(stepper.activeStep) {
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
                }
            }

        })

    }

    fun isNameValid(name: String): Boolean {
        return name.isNotEmpty()
    }

    fun String.isValidEmail() = isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}