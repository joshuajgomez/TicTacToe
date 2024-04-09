package com.triplerock.tictactoe.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.data.sampleNames
import com.triplerock.tictactoe.data.sampleRoomNames
import com.triplerock.tictactoe.ui.navMenu
import com.triplerock.tictactoe.ui.screens.common.Loading
import com.triplerock.tictactoe.ui.screens.common.TitleBar
import com.triplerock.tictactoe.ui.theme.TicTacToeTheme
import com.triplerock.tictactoe.utils.Logger
import com.triplerock.tictactoe.viewmodels.Crossing
import com.triplerock.tictactoe.viewmodels.GameUiState
import com.triplerock.tictactoe.viewmodels.GameViewModel
import com.triplerock.tictactoe.viewmodels.crossingList
import org.koin.androidx.compose.koinViewModel

@Composable
fun GameScreenContainer(
    gameViewModel: GameViewModel = koinViewModel(),
    navController: NavController,
) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TitleBar(title = "Tic Tac Toe") { navController.navigate(navMenu) }
        val gameUiState = gameViewModel.uiState.collectAsState()
        val roomName = gameViewModel.roomName
        val playerName = gameViewModel.myName
        when (gameUiState.value) {
            is GameUiState.Waiting -> {
                val waiting = gameUiState.value as GameUiState.Waiting
                Loading(
                    message = waiting.statusText,
                )
            }

            is GameUiState.NextTurn -> {
                val nextTurn = gameUiState.value as GameUiState.NextTurn
                GameScreen(
                    onCellClicked = { gameViewModel.onCellClick(it) },
                    player1Moves = nextTurn.player1Moves,
                    player2Moves = nextTurn.player2Moves,
                    statusText = nextTurn.statusText,
                    isPlayable = nextTurn.isMyTurn,
                    roomName = roomName,
                    playerName = playerName
                )
            }

            is GameUiState.Winner -> {
                val winner = gameUiState.value as GameUiState.Winner
                GameScreen(
                    player1Moves = winner.player1Moves,
                    player2Moves = winner.player2Moves,
                    statusText = winner.statusText,
                    crossing = winner.crossing,
                    isShowRestartButton = true,
                    onRestartButtonClick = { gameViewModel.onRestartClick() },
                    roomName = roomName,
                    playerName = playerName
                )
            }

            is GameUiState.Draw -> {
                val gameOver = gameUiState.value as GameUiState.Draw
                GameScreen(
                    player1Moves = gameOver.player1Moves,
                    player2Moves = gameOver.player2Moves,
                    statusText = gameOver.statusText,
                    isShowRestartButton = true,
                    onRestartButtonClick = { gameViewModel.onRestartClick() },
                    roomName = roomName,
                    playerName = playerName
                )
            }
        }
    }

}

@Preview
@Composable
private fun PreviewGameScreenLight() {
    TicTacToeTheme {
        Surface(color = colorScheme.background) {
            GameScreen(isShowRestartButton = true, isPlayable = true)
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewGameScreenDark() {
    TicTacToeTheme {
        Surface(color = colorScheme.background) {
            GameScreen(isShowRestartButton = true, isPlayable = true)
        }
    }
}

@Preview
@Composable
private fun PreviewGameScreenNotMyTurn() {
    TicTacToeTheme {
        Column {
            GameScreen()
        }
    }
}

@Preview
@Composable
private fun PreviewGameScreenFull() {
    TicTacToeTheme {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TitleBar()
            GameScreen(isShowRestartButton = true)
        }
    }
}

@Preview
@Composable
private fun PreviewGame() {
    TicTacToeTheme {
        Box(contentAlignment = Alignment.Center) {
            LazyVerticalGrid(
                columns = GridCells.FixedSize(100.dp),
                modifier = Modifier
                    .width(300.dp)
                    .height(300.dp)
            ) {
                items(count = 9) {
                    Cell(stateList.random())
                }
            }
            crossingList.forEach {
                Line(
                    crossing = it,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewStatusBox() {
    TicTacToeTheme {
        StatusBox()
    }
}

const val statusTurnPlayer1 = "Turn: Player 1"

@Composable
fun StatusBox(
    modifier: Modifier = Modifier,
    message: String = statusTurnPlayer1,
) {
    Text(
        text = message,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = colorScheme.primary,
        modifier = modifier
    )
}

data class Mark(
    val icon: ImageVector = Icons.Default.QuestionMark,
    val isEmpty: Boolean = false,
)

val stateList = listOf(
    Mark(Icons.Default.Clear),
    Mark(Icons.Outlined.Circle),
    Mark(isEmpty = true),
)

@Composable
private fun GameScreen(
    isPlayable: Boolean = false,
    playerName: String = sampleNames.random(),
    roomName: String = sampleRoomNames.random(),
    onCellClicked: (cell: Int) -> Unit = {},
    player1Moves: List<Int> = emptyList(),
    player2Moves: List<Int> = emptyList(),
    crossing: Crossing? = null,
    statusText: String = statusTurnPlayer1,
    isShowRestartButton: Boolean = false,
    onRestartButtonClick: () -> Unit = {},
) {
    ConstraintLayout {
        val (status, name, turn, grid, crossingContainer, restart) = createRefs()
        IdBox(modifier = Modifier.constrainAs(name) {
            start.linkTo(parent.start)
            top.linkTo(parent.top)
        }, playerName = playerName, roomName = roomName)
        StatusBox(message = statusText, modifier = Modifier.constrainAs(status) {
            end.linkTo(parent.end)
            top.linkTo(parent.top)
        })
        AnimatedVisibility(visible = isPlayable, modifier = Modifier.constrainAs(turn) {
            end.linkTo(grid.end)
            bottom.linkTo(grid.top)
        }) {
            Text(
                text = "your turn",
                fontSize = 14.sp,
                modifier = Modifier
                    .background(
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                        color = colorScheme.tertiary
                    )
                    .padding(horizontal = 10.dp, vertical = 2.dp),
                color = colorScheme.background
            )
        }
        LazyVerticalGrid(columns = GridCells.FixedSize(100.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier
                .constrainAs(grid) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(name.bottom, margin = 35.dp)
                }
                .size(300.dp)) {
            items(count = 9) {
                val cellState = if (player1Moves.contains(it)) Mark(Icons.Default.Clear)
                else if (player2Moves.contains(it)) Mark(Icons.Outlined.Circle)
                else Mark(isEmpty = true)
                Cell(cellState, onCellClick = {
                    if (isPlayable) onCellClicked(it) else Logger.warn("not your turn")
                })
            }
        }
        if (crossing != null) {
            Line(
                modifier = Modifier.constrainAs(crossingContainer) {
                    start.linkTo(grid.start)
                    end.linkTo(grid.end)
                    top.linkTo(grid.top)
                    bottom.linkTo(grid.bottom)
                }, crossing = crossing
            )
        }
        AnimatedVisibility(
            visible = isShowRestartButton,
            modifier = Modifier.constrainAs(restart) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(grid.bottom, 50.dp)
            }) {
            RestartButton { onRestartButtonClick() }
        }
    }
}

@Preview
@Composable
fun PreviewIdBox() {
    TicTacToeTheme {
        Surface(color = colorScheme.background) {
            IdBox()
        }
    }
}

@Composable
fun IdBox(
    modifier: Modifier = Modifier, roomName: String = "big-mac",
    playerName: String = "pico",
) {
    Column(
        modifier = modifier
    ) {
        Info("Room: ", roomName)
        Info("Player: ", playerName)
    }
}

@Composable
fun Info(label: String, info: String) {
    Row {
        Text(text = label)
        Text(text = info, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RestartButton(onRestartClick: () -> Unit = {}) {
    Button(
        onClick = { onRestartClick() },
    ) {
        Icon(
            imageVector = Icons.Default.Autorenew, contentDescription = null
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = "New game")
    }
}

@Composable
fun Line(
    modifier: Modifier = Modifier,
    crossing: Crossing,
) {
    Canvas(
        modifier = modifier.size(210.dp)
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
                width = 2.dp, color = colorScheme.outline
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


