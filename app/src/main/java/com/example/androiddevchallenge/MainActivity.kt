/*
 * Copyright 2021 The Android Open Source Project
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
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.typography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CountDownViewModel : ViewModel() {

    private companion object {
        private const val COUNT = 100
    }

    private val _count: MutableLiveData<Int> = MutableLiveData(COUNT)
    private val _done: MutableLiveData<Boolean> = MutableLiveData(false)

    val count: LiveData<Int>
        get() = _count

    val done: LiveData<Boolean>
        get() = _done

    init {
        GlobalScope.launch(Dispatchers.Default) {
            while (_count.value ?: 0 > 0) {
                delay(1000)
                val newValue = _count.value!! - 1
                withContext(Dispatchers.Main) {
                    _count.value = newValue
                }
            }
            withContext(Dispatchers.Main) {
                _done.value = true
            }
        }
    }
}

class MainActivity : AppCompatActivity() {

    private val viewModel: CountDownViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                val done = viewModel.done.observeAsState()
                if (!done.value!!) {
                    val count = viewModel.count.observeAsState()
                    MyApp(count.value!!)
                } else {
                    MyDone()
                }
            }
        }
    }
}

@Composable
fun MyApp(count: Int) {
    MySurface(text = "$count")
}

@Composable
fun MyDone() {
    MySurface(text = "Done")
}

@Composable
fun MySurface(text: String) {
    Surface(color = MaterialTheme.colors.background) {
        Row {
            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                contentAlignment = Alignment.Center) {
                Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    style = typography.h1
                )
            }
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp(200)
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp(200)
    }
}
