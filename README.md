# Android Vertical Stepper
[![](https://jitpack.io/v/leodan11/xverticalstepper.svg)](https://jitpack.io/#leodan11/xverticalstepper)
[![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=23)

# Implementation

- Step 1. Add the JitPack repository to your build file

  Add it in your root build.gradle at the end of repositories:

  ```gradle
  allprojects {
    repositories {
      ...
      maven { url 'https://jitpack.io' }
      }
  }
  ```

#### Gradle

- Step 2. Add the dependency

  ```gradle
  dependencies {
    implementation 'com.github.leodan11:xverticalstepper:Tag'
  }
  ```


#### Kotlin

- Step 2. Add the dependency

    ```kotlin
    dependencies {
      implementation("com.github.leodan11:xverticalstepper:$tag")
    }
    ```


# Credits

This is just an updated version of [Android Vertical Stepper](https://github.com/aghiadodeh/xverticalstepper) and applying some of the active pull requests in it. 
Credits go completely to its creator and the people who has contributed with those pull requests.

### Usage:

#### Stepper

Attribute Name | format | Default Value
------------- | ------------- | -------------
stepper_opened_step_index  | integer | 0
stepper_primary_color  | color | Color.GRAY

#### StepContainer

Attribute Name | format | Default Value
------------- | ------------- | -------------
step_title  | string | " "
need_validation  | boolean | false


#### Methods

`goToNextStep`: open next step, (no matter if you need validation or not)

`goToStep(index: Int, allowed: Boolean = false, firstAction: Boolean = false)`:
* `index` is step index
* `allowed`: set true when you want to open a step anyway and the step is need validation
* `firstAction`: set true to disable animation

`removeStep(index: Int)`: hide step from stepper

`restoreStep(index: Int)`: show step after hidden

`isPreviousStepsCompleted(index: Int): Boolean`: detect if previous steps is valid
##

### Overview

in `your_activity.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <com.github.leodan11.xstepper.Stepper
        android:id="@+id/stepper"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:stepper_opened_step_index="0"
        app:stepper_primary_color="@color/purple_500">

	<!-- 1st Step -->
        <com.github.leodan11.xstepper.StepContainer
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:need_validation="true">

	    <!-- customize your step header -->
            <com.github.leodan11.xstepper.StepHeader
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:gravity="center_vertical"
                android:padding="4dp">

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_gravity="center"
                    android:text="Name"
                    tools:ignore="HardcodedText" />

            </com.github.leodan11.xstepper.StepHeader>

	    <!-- step body -->
            <com.google.android.material.textfield.TextInputLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content">
                <com.google.android.material.textfield.TextInputEditText
                    android:id="@+id/name_input"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:hint="Name" />
            </com.google.android.material.textfield.TextInputLayout>

        </com.github.leodan11.xstepper.StepContainer>

	<!-- 2nd Step -->
        <com.github.leodan11.xstepper.StepContainer
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:need_validation="false"
            app:step_title="Step Title 2">
	    <!-- step body -->
            <TextView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Step 2" />

        </com.github.leodan11.xstepper.StepContainer>

    </com.github.leodan11.xstepper.Stepper>

</androidx.constraintlayout.widget.ConstraintLayout>
```

in `YourActivity.kt`:
```kotlin

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val stepper: Stepper = findViewById(R.id.stepper)
        val nameInput: TextInputEditText = findViewById(R.id.name_input)
        val emailInput: TextInputEditText = findViewById(R.id.email_input)

        stepper.setListener(object : IStepper {
            override fun onStepOpening(step: Int) {
                // detect when step opening
            }

            override fun onWaitingForOpen(step: Int) {
                // if a step is need validation
                when(stepper.activeStep) {
                    0 -> {
                        if (emailInput.text.toString().isValidEmail()) {
                            emailInput.error = null
                            stepper.goToNextStep()
                        } else {
                            emailInput.error = "email is not valid"
                        }
                    }
                }
            }

            override fun onFinished() {
                //  when call `stepper.goToNextStep()` on last step
            }
        })
    }
}
```
