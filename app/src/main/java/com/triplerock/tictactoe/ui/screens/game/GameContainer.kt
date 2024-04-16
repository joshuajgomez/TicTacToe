package com.triplerock.tictactoe.ui.screens.game

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.triplerock.tictactoe.data.PlayerO
import com.triplerock.tictactoe.data.PlayerX
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.ui.screens.common.TicBackground
import com.triplerock.tictactoe.ui.screens.common.solidShadow
import com.triplerock.tictactoe.ui.screens.getRooms
import com.triplerock.tictactoe.viewmodels.Crossing

data class Mark(
    val icon: ImageVector = Icons.Default.QuestionMark,
    val modifier: Modifier = Modifier,
    val iconTint: Color = Color.Gray,
    val isEmpty: Boolean = false,
)

@Preview
@Composable
fun PreviewGameContainer() {
    TicBackground {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(all = 20.dp)
        ) {
            GameContainer()
            crossingList.forEach { crossing ->
                CrossingLine(crossing = crossing)
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewGameContainerDark() {
    TicBackground {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(all = 20.dp)
        ) {
            GameContainer()
            crossingList.forEach { crossing ->
                CrossingLine(crossing = crossing)
            }
        }
    }
}

@Composable
fun GameContainer(
    modifier: Modifier = Modifier,
    crossing: Crossing? = null,
    room: Room = getRooms().first(),
    onCellClicked: (cell: Int) -> Unit = {},
) {
    Box(
        modifier.solidShadow(
            color = colorScheme.onBackground,
            radius = 0f, offset = 7.dp
        )
    ) {
        ConstraintLayout(Modifier.background(color = colorScheme.background)) {
            val (grid, crossingContainer) = createRefs()
            BorderLines()
            MarkBox(
                modifier = Modifier.constrainAs(grid) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                room.moves[PlayerX]!!, room.moves[PlayerO]!!, onCellClicked
            )
            if (crossing != null) {
                CrossingLine(
                    modifier = Modifier.constrainAs(crossingContainer) {
                        start.linkTo(grid.start)
                        end.linkTo(grid.end)
                        top.linkTo(grid.top)
                        bottom.linkTo(grid.bottom)
                    },
                    crossing = crossing
                )
            }
        }
    }
}

val gameBoxSize = 330.dp

@Composable
fun MarkBox(
    modifier: Modifier = Modifier,
    player1Moves: List<Int>,
    player2Moves: List<Int>,
    onCellClicked: (cell: Int) -> Unit = {},
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        modifier = modifier.size(gameBoxSize)
    ) {
        items(count = 9) {
            val cellState = if (player1Moves.contains(it)) Mark(
                icon = Icons.Outlined.Cancel,
                iconTint = MaterialTheme.colorScheme.error,
            )
            else if (player2Moves.contains(it)) Mark(
                icon = Icons.Outlined.Circle,
                iconTint = MaterialTheme.colorScheme.onBackground,
            )
            else Mark(isEmpty = true)
            Cell(mark = cellState) {
                onCellClicked(it)
            }
        }
    }
}

const val rectOffset = 610f
val rect = Rect(Offset.Zero, Size(width = rectOffset, height = rectOffset))
val crossingList = listOf(
    Crossing(positions = listOf(0, 1, 2), start = rect.topLeft, end = rect.topRight),
    Crossing(positions = listOf(3, 4, 5), start = rect.centerLeft, end = rect.centerRight),
    Crossing(positions = listOf(6, 7, 8), start = rect.bottomLeft, end = rect.bottomRight),

    Crossing(positions = listOf(0, 3, 6), start = rect.topLeft, end = rect.bottomLeft),
    Crossing(positions = listOf(1, 4, 7), start = rect.topCenter, end = rect.bottomCenter),
    Crossing(positions = listOf(2, 5, 8), start = rect.topRight, end = rect.bottomRight),

    Crossing(positions = listOf(0, 4, 8), start = rect.topLeft, end = rect.bottomRight),
    Crossing(positions = listOf(2, 4, 6), start = rect.topRight, end = rect.bottomLeft),
)

@Composable
fun CrossingLine(
    modifier: Modifier = Modifier,
    crossing: Crossing,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Canvas(
        modifier = modifier.size(220.dp)
    ) {
        drawLine(
            color = color.copy(alpha = 0.7f),
            start = crossing.start,
            end = crossing.end,
            strokeWidth = 30f
        )
    }
}

@Composable
fun Cell(mark: Mark, onCellClick: () -> Unit = {}) {
    IconButton(
        onClick = { onCellClick() },
        modifier = Modifier
            .size(gameBoxSize / 3)
            .padding(all = 10.dp),
    ) {
        AnimatedVisibility(visible = !mark.isEmpty) {
            Icon(
                imageVector = mark.icon,
                contentDescription = null,
                tint = mark.iconTint,
                modifier = mark.modifier.size(100.dp)
            )
        }
    }
}

@Composable
fun BorderLines(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground,
    stroke: Dp = 2.dp,
) {
    Box(modifier.size(gameBoxSize)) {
        Canvas(Modifier.fillMaxSize()) {
            data class Line(
                val xStart: Float,
                val yStart: Float,
                val xEnd: Float,
                val yEnd: Float,
            )

            val lines = listOf(
                Line(0f, 0f, size.width, 0f),
                Line(0f, size.width / 3, size.width, size.width / 3),
                Line(0f, size.width / 3 * 2, size.width, size.width / 3 * 2),
                Line(0f, size.width, size.width, size.width),

                Line(0f, 0f, 0f, size.width),
                Line(size.width / 3, 0f, size.width / 3, size.width),
                Line(size.width / 3 * 2, 0f, size.width / 3 * 2, size.width),
                Line(size.width, 0f, size.width, size.width),
            )

            lines.forEach { line ->
                drawLine(
                    color = color,
                    start = Offset(line.xStart, line.yStart),
                    end = Offset(line.xEnd, line.yEnd),
                    strokeWidth = stroke.toPx(),
                )
            }
        }
    }
}