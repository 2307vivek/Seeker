package dev.vivvvek.seeker

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SeekerTest {
    private val tag = "seeker"

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun sliderPosition_value() {
        val seekerValue = mutableStateOf(0f)
        
        rule.setContent { 
            Seeker(
                modifier = Modifier.testTag(tag),
                value = seekerValue.value,
                onValueChange = { seekerValue.value = it },
                range = 0f..1f
            )
        }
        rule.runOnIdle {
            seekerValue.value = 2f
        }
        rule.onNodeWithTag(tag).assertRangeInfoEquals(ProgressBarRangeInfo(1f, 0f..1f, 0))

        rule.runOnIdle {
            seekerValue.value = -25451f
        }
        rule.onNodeWithTag(tag).assertRangeInfoEquals(ProgressBarRangeInfo(0f, 0f..1f, 0))
    }
}