package com.example.pixel_hack_stylus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.example.pixel_hack_stylus.ui.theme.PixelhackstylusTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PixelhackstylusTheme {
                CanvasZone(
                    pixelsWidth = 30f, // Ancho del rectángulo
                    pixelsHeight = 20f // Alto del rectángulo
                )
            }
        }
    }
}




