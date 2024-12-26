package com.example.pixel_hack_stylus

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.example.pixel_hack_stylus.utils.calculateCellSize
import com.example.pixel_hack_stylus.utils.calculateDistance
import com.example.pixel_hack_stylus.utils.getOffsetCell

val PIXEL_RESIZE_FACTOR = 10f

@Composable
fun CanvasZone(pixelsWidth: Float, pixelsHeight: Float) {
    val rectWidth = (pixelsWidth * PIXEL_RESIZE_FACTOR).dp
    val rectHeight = (pixelsHeight * PIXEL_RESIZE_FACTOR).dp
    // Estado para almacenar la posición inicial del rectángulo
    var offset by remember { mutableStateOf(Offset(300f, 300f)) }
    var lastStylusOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var scale by remember { mutableStateOf(1f) }
    val stylusPath = remember { mutableListOf<Offset>() }
    var canvasSize by remember { mutableStateOf(Offset.Zero) }
    val isEraserMode = remember { mutableStateOf(false) }

    val gridMatrix = remember {
        MutableList(pixelsHeight.toInt()) {
            MutableList(pixelsWidth.toInt()) { 0 }
        }
    }


    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    val touchChanges = event.changes.filter { it.type == PointerType.Touch }

                    if (touchChanges.size == 2) {
                        val firstFinger = touchChanges[0]
                        val secondFinger = touchChanges[1]

                        // Relevant variables
                        val currentPosition = firstFinger.position
                        val prevPostiotion = firstFinger.previousPosition
                        val currentDistance = calculateDistance(
                            firstFinger.position, secondFinger.position
                        )
                        val previousDistance = calculateDistance(
                            firstFinger.previousPosition, secondFinger.previousPosition
                        )

                        // Position mangement

                        val delta = Offset(
                            currentPosition.x - prevPostiotion.x,
                            currentPosition.y - prevPostiotion.y
                        )

                        offset = Offset(offset.x + delta.x, offset.y + delta.y)

                        //Scale managment
                        if (previousDistance != 0f) {
                            val scaleChange = currentDistance / previousDistance
                            scale *= scaleChange
                        }

                    }

                }
            }
        }
        .border(1.dp, color = Color.Magenta)) {
        Canvas(
            modifier = Modifier
                .size(rectWidth, rectHeight) // Especificar tamaño
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .background(Color.DarkGray)
                .onGloballyPositioned { layoutCoordinates ->
                    // Obtener el tamaño del canvas
                    val size = layoutCoordinates.size
                    canvasSize = Offset(size.width.toFloat(), size.height.toFloat())
                }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val stylusChanges =
                                event.changes.filter { it.type == PointerType.Stylus }

                            if (stylusChanges.isNotEmpty()) {
                                val currentStylusPosition = stylusChanges[0].position
                                lastStylusOffset = currentStylusPosition
                                val cellPos = getOffsetCell(canvasSize.x, canvasSize.y, pixelsWidth, pixelsHeight, currentStylusPosition)
                                println(cellPos.x)
                                println(cellPos.y)

                                if (
                                    cellPos.x < pixelsWidth &&
                                    cellPos.y < pixelsHeight &&
                                    cellPos.x >= 0 && cellPos.y >= 0
                                    ) {
                                    gridMatrix[cellPos.y.toInt()][cellPos.x.toInt()] = if (isEraserMode.value) 0 else 1
                                }
                                stylusPath.add(currentStylusPosition) // Agregar la posición del stylus a la lista
                            }
                        }
                    }
                }
        ) {
            val canvasWidth = size.width.toFloat()
            val canvasHeight = size.height.toFloat()

            val cellSize = calculateCellSize(canvasSize.x, canvasSize.y, pixelsWidth, pixelsHeight)

            // Dibujar la cuadrícula
            for (i in 0 until (canvasHeight / cellSize).toInt()) {
                val lineY = i * cellSize
                drawLine(
                    start = Offset(x = 0f, y = lineY),
                    end = Offset(x = canvasWidth, y = lineY),
                    color = Color.Blue,
                    strokeWidth = 1f
                )
            }

            for (i in 0 until (canvasWidth / cellSize).toInt()) {
                val lineX = i * cellSize
                drawLine(
                    start = Offset(x = lineX, y = 0f),
                    end = Offset(x = lineX, y = canvasHeight),
                    color = Color.Blue,
                    strokeWidth = 1f
                )
            }

            for (y in 0 until pixelsHeight.toInt()) {
                for (x in 0 until pixelsWidth.toInt()) {
                    if (gridMatrix[y][x] == 1) {
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(x * cellSize, y * cellSize),
                            size = Size(cellSize, cellSize)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Position Canvas: (${offset.x}, ${offset.y})",
            )
            Text(
                text = "Canvas Size: (${canvasSize.x}, ${canvasSize.y})",
            )
            Text(
                text = "Last Position Stylus: (${lastStylusOffset.x}, ${lastStylusOffset.y})",
            )
            Text(
                text = "Scale: ${"%.2f".format(scale)}",
            )
            Row {
                Button(
                    onClick = { isEraserMode.value = false },
                    modifier = Modifier.background(if (isEraserMode.value) Color.Red else Color.Gray)
                ) {
                    Text("Lápiz", color = Color.White)
                }
                Button(
                    onClick = { isEraserMode.value = true },
                    modifier = Modifier.background(if (!isEraserMode.value) Color.Red else Color.Gray)
                ) {
                    Text("Borrador", color = Color.White)
                }
            }
        }
    }
}