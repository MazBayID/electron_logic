package com.mazbayid.electronlogic

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun labLoadsCircuitAndSignalScope() {
        composeRule.onNodeWithTag("app-title").assertExists()
        composeRule.onNodeWithText("CIRCUIT OVERVIEW").assertExists()
        composeRule.onNodeWithText("SIGNAL SCOPE").assertExists()
        composeRule.onNodeWithText("PARAMETER LAB").assertExists()
    }

    @Test
    fun navigationOpensCircuitAndDedicatedOscilloscope() {
        composeRule.onNodeWithText("Circuit").performClick()
        composeRule.onNodeWithText("CIRCUIT EDITOR").assertExists()

        composeRule.onNodeWithText("Measure").performClick()
        composeRule.onNodeWithText("SIGNAL OSCILLOSCOPE").assertExists()
        composeRule.onNodeWithText("CH1 · tegangan output terhadap waktu").assertExists()
    }

    @Test
    fun resistanceSliderIsPresentAndReachable() {
        composeRule.onNodeWithTag("resistance-slider")
            .performScrollTo()
            .assertExists()
        composeRule.onNodeWithText("330 Ω").assertExists()
    }

    @Test
    fun demoControlsCanBeToggled() {
        composeRule.onNodeWithText("Noise demo")
            .performScrollTo()
            .performClick()
        composeRule.onNodeWithText("✓ Noise demo").assertExists()

        composeRule.onNodeWithText("Short demo")
            .performScrollTo()
            .performClick()
        composeRule.onNodeWithText("✓ Short demo").assertExists()
    }
}
