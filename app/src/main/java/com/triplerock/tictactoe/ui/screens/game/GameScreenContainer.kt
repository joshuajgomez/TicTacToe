package com.triplerock.tictactoe.ui.screens.game

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.triplerock.tictactoe.data.PlayerX
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.ui.navMenu
import com.triplerock.tictactoe.ui.screens.common.CustomButton
import com.triplerock.tictactoe.ui.screens.common.Loading
import com.triplerock.tictactoe.ui.screens.common.TicBackground
import com.triplerock.tictactoe.ui.screens.common.solidShadow
import com.triplerock.tictactoe.ui.screens.getRooms
import com.triplerock.tictactoe.utils.Logger
import com.triplerock.tictactoe.viewmodels.GameUiState
import com.triplerock.tictactoe.viewmodels.GameViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun GameScreenContainer(
    gameViewModel: GameViewModel = koinViewModel(),
    navController: NavController,
) {
    Column(
        Modifier.fillMaxSize(),
    ) {
        val gameUiState = gameViewModel.uiState.collectAsState()
        Logger.debug("gameUiState = ${gameUiState.value}")
        when (gameUiState.value) {
            is GameUiState.Waiting -> {
                val waiting = gameUiState.value as GameUiState.Waiting
                Loading(
                    message = waiting.statusText,
                )
            }

            is GameUiState.NextTurn -> {
                val nextTurn = gameUiState.value as GameUiState.NextTurn
                Logger.debug("nextTurn = $nextTurn")
                GameScreen(
                    room = nextTurn.room,
                    onCellClicked = { gameViewModel.onCellClick(it) },
                    statusText = nextTurn.room.status,
                    isPlayable = nextTurn.room.nextTurn == gameViewModel.player,
                    currentPlayer = gameViewModel.player,
                    onCloseClicked = {
                        navController.navigate(navMenu)
                    }
                )
            }

            is GameUiState.Winner -> {
                val winner = gameUiState.value as GameUiState.Winner
                GameScreen(
                    room = winner.room,
                    statusText = winner.room.status,
                    crossing = winner.crossing,
                    isShowRestartButton = gameViewModel.player == PlayerX,
                    onRestartButtonClick = { gameViewModel.onRestartClick() },
                    currentPlayer = gameViewModel.player,
                    onCloseClicked = {
                        navController.navigate(navMenu)
                    }
                )
            }

            is GameUiState.Draw -> {
                val gameOver = gameUiState.value as GameUiState.Draw
                GameScreen(
                    room = gameOver.room,
                    statusText = gameOver.room.status,
                    isShowRestartButton = gameViewModel.player == PlayerX,
                    onRestartButtonClick = { gameViewModel.onRestartClick() },
                    currentPlayer = gameViewModel.player,
                    onCloseClicked = {
                        navController.navigate(navMenu)
                    }
                )
            }
        }
    }

}

@Preview(device = Devices.PIXEL_2)
@Composable
private fun PreviewGameScreenLightPixel2() {
    GameScreenForPreview()
}

@Preview
@Composable
private fun PreviewGameScreenLight() {
    GameScreenForPreview()
}

@Composable
fun GameScreenForPreview() {
    TicBackground {
        Column(
            Modifier.fillMaxSize(),
        ) {
            GameScreen(isPlayable = true, isShowRestartButton = false)
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewGameScreenDark() {
    GameScreenForPreview()
}

const val statusTurnPlayer1 = "Turn: Player 1"

@Composable
fun StatusBox(
    modifier: Modifier = Modifier,
    message: String = statusTurnPlayer1,
) {
    Text(
        text = message,
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
        color = colorScheme.onBackground,
        modifier = modifier
    )
}

@Composable
private fun GameScreen(
    room: Room = getRooms().random(),
    isPlayable: Boolean = true,
    crossing: Crossing? = null,
    statusText: String = statusTurnPlayer1,
    isShowRestartButton: Boolean = true,
    currentPlayer: String = PlayerX,
    onRestartButtonClick: () -> Unit = {},
    onCellClicked: (cell: Int) -> Unit = {},
    onCloseClicked: () -> Unit = {},
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (close, title, turn, turnText, grid, restart, stats) = createRefs()
        CustomButton(modifier = Modifier.constrainAs(close) {
            start.linkTo(parent.start, 6.dp)
            top.linkTo(parent.top, 6.dp)
        }, icon = Icons.Default.Clear) {
            onCloseClicked()
        }
        StatusBox(
            message = statusText,
            modifier = Modifier.constrainAs(title) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top, 20.dp)
            })
        TurnBox(
            modifier = Modifier.constrainAs(turn) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(title.bottom, margin = 10.dp)
            },
            room = room, currentPlayer = currentPlayer
        )
        GameContainer(
            modifier = Modifier.constrainAs(grid) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(turn.bottom, margin = 30.dp)
            },
            room = room,
            crossing = crossing
        ) {
            // on cell clicked
            if (isPlayable) {
                onCellClicked(it)
            }
        }

        AnimatedVisibility(
            visible = room.nextTurn == currentPlayer,
            modifier = Modifier.constrainAs(turnText) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(grid.bottom, 20.dp)
            }) {
            Text(
                text = "your turn",
                fontSize = 25.sp,
            )
        }

        Stats(
            modifier = Modifier.constrainAs(stats) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(grid.bottom, margin = 80.dp)
            },
            room = room
        )

        AnimatedVisibility(
            visible = isShowRestartButton,
            modifier = Modifier.constrainAs(restart) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, 20.dp)
            }) {
            RestartButton { onRestartButtonClick() }
        }
    }
}

@Composable
fun RestartButton(onRestartClick: () -> Unit = {}) {
    TextButton(
        onClick = { onRestartClick() },
        modifier = Modifier
            .solidShadow()
            .clip(RoundedCornerShape(30.dp))
            .background(colorScheme.background)
            .border(2.dp, colorScheme.onBackground, RoundedCornerShape(30.dp))
            .padding(horizontal = 10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Autorenew,
            contentDescription = null,
            tint = colorScheme.onBackground
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "New Game", fontSize = 25.sp,
            color = colorScheme.onBackground
        )
    }
}

