package com.conboi.feature.level.common.interactions

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.conboi.core.ui.animation.DrawAnimation
import kotlin.math.roundToInt

@Composable
fun DraggableText(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    text: String,
    style: TextStyle,
    delayOrder: Int? = 0,
    onDrag: (Offset, Offset) -> Unit = { _, _ -> }
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp.roundToPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.roundToPx() }

    var offsetX by remember { mutableStateOf(0F) }
    var offsetY by remember { mutableStateOf(0F) }
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    var rectOfDraggable by remember { mutableStateOf<Rect?>(null) }

    DrawAnimation(modifier = modifier, delayOrder = delayOrder) {
        Text(
            text = text,
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    rectOfDraggable = coordinates.boundsInWindow()
                    currentPosition = coordinates
                        .localToWindow(Offset.Zero)
                        .run { Offset(x, y) }
                }
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(isEnabled) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        if (isEnabled) {
                            val width = rectOfDraggable?.width ?: return@detectDragGestures
                            val height = rectOfDraggable?.height ?: return@detectDragGestures

                            offsetX = (offsetX + dragAmount.x)
                                .coerceIn(
                                    -currentPosition.x,
                                    screenWidth - currentPosition.x - width
                                )
                            offsetY = (offsetY + dragAmount.y)
                                .coerceIn(
                                    -currentPosition.y + height * 2,
                                    screenHeight - currentPosition.y - height
                                )

                            val draggableOffset = Offset(offsetX, offsetY) + currentPosition
                            val screenSize = Offset(screenWidth.toFloat(), screenHeight.toFloat())
                            onDrag(
                                draggableOffset,
                                screenSize
                            )
                        }


                    }
                },
            style = style
        )
    }
}