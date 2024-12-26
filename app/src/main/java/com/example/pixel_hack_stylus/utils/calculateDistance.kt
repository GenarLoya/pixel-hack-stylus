package com.example.pixel_hack_stylus.utils

import androidx.compose.ui.geometry.Offset

fun calculateDistance(offset1: Offset, offset2: Offset): Float {
    return Math.sqrt(
        ((offset1.x - offset2.x) * (offset1.x - offset2.x) +
                (offset1.y - offset2.y) * (offset1.y - offset2.y)).toDouble()
    ).toFloat()
}