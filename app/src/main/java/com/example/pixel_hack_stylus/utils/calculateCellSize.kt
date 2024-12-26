package com.example.pixel_hack_stylus.utils

import androidx.compose.ui.geometry.Offset

fun calculateCellSize(canvasWidth: Float, canvasHeight: Float, pixelsWidth: Float, pixelsHeight: Float): Float {
    // Calcular el tamaño de cada celda
    val cellWidth = canvasWidth / pixelsWidth
    val cellHeight = canvasHeight / pixelsHeight

    // Asegurarse de que el tamaño de la celda sea el mismo en ambas direcciones
    return minOf(cellWidth, cellHeight)
}

fun getOffsetCell(
    canvasWidth: Float,
    canvasHeight: Float,
    pixelsWidth: Float,
    pixelsHeight: Float,
    position: Offset
): Offset {
    // Calcular el tamaño de cada celda
    val cellSize = calculateCellSize(canvasWidth, canvasHeight, pixelsWidth, pixelsHeight)

    // Determinar la fila y columna
    val col = (position.x / cellSize).toInt()
    val row = (position.y / cellSize).toInt()

    return Offset(col.toFloat(), row.toFloat())
}
