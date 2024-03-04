package com.triplerock.tictactoe

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import com.triplerock.tictactoe.ui.theme.TicTacToeTheme

@Composable
fun GameScreenContainer(gameViewModel: GameViewModel) {
    GameScreen()
}

@Preview
@Composable
private fun PreviewGameScreen() {
    TicTacToeTheme {
        GameScreen()
    }
}

data class Mark(val icon: ImageVector, val isEmpty: Boolean = false)

val stateList = listOf(
    Mark(Icons.Default.Clear),
    Mark(Icons.Outlined.Circle),
    Mark(Icons.Default.QuestionMark, isEmpty = true),
)

@Composable
private fun GameScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(300.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.FixedSize(100.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier.width(400.dp)
        ) {
            items(count = 9) {
                Cell(stateList.random())
            }
        }
        crossingList.forEach {
            Line(it)
        }
    }
}

@Composable
fun Line(crossing:  Crossing) {
    Canvas(
        modifier = Modifier
            .width(240.dp)
            .height(240.dp)
    ) {
        drawLine(
            color = Color.Yellow.copy(alpha = 0.5f),
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
            .size(100.dp)
            .border(
                width = 2.dp,
                color = colorScheme.outline
            )
            .padding(all = 10.dp),
    ) {
        AnimatedVisibility(visible = !mark.isEmpty) {
            Icon(
                imageVector = mark.icon,
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier.size(100.dp)
            )
        }
    }
}


