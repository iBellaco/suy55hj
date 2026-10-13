package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.QuotaMainScreen
import com.example.ui.QuotaViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: QuotaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                QuotaMainScreen(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh state when coming back from background or after widget click
        viewModel.refreshState()
    }
}

