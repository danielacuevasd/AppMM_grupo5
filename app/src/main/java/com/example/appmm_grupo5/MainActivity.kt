package com.example.appmm_grupo5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.appmm_grupo5.ui.screens.HomeScreen2
import com.example.appmm_grupo5.ui.theme.AppMM_grupo5Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppMM_grupo5Theme {
                HomeScreen2()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppMM_grupo5Theme {
        HomeScreen2()
    }
}