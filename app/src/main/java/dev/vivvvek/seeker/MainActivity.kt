/*
 * Copyright 2021 Vivek Singh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.vivvvek.seeker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import dev.vivvvek.seeker.ui.theme.SeekerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeekerTheme {
                var count by remember { mutableStateOf(0) }
                var enabled by remember { mutableStateOf(true) }

                var value by remember {
                    mutableStateOf(0f)
                }

                // Slider(value = value, onValueChange = { value = it })

                Column {
                    Button(onClick = { enabled = !enabled }) {
                        Text("jhdfj")
                    }
                    //CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Seeker(
                            enabled = enabled,
                            value = value,
                            onValueChange = { value = it },
                            onValueChangeFinished = { count += 1 }
                        )
                    Slider(
                        value = value,
                        onValueChange = { value = it },
                        enabled = enabled
                    )
                    //}
                    Text(text = value.toString())
                    Text(text = count.toString())
                }
            }
        }
    }
    val colors = listOf(Color.Green, Color.Gray, Color.Green, Color.Blue, Color.Black, Color.White, Color.Yellow, Color.Magenta)
}
