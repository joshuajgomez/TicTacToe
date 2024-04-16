package com.triplerock.tictactoe.ui.screens.game

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.triplerock.tictactoe.data.PlayerX
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.ui.screens.common.Loading
import com.triplerock.tictactoe.ui.screens.common.TicBackground
import com.triplerock.tictactoe.ui.screens.getRooms
import com.triplerock.tictactoe.utils.Logger
import com.triplerock.tictactoe.viewmodels.Crossing
import com.triplerock.tictactoe.viewmodels.GameUiState
import com.triplerock.tictactoe.viewmodels.GameViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun GameScreenContainer(
    gameViewModel: GameViewModel = koinViewModel(),
    navController: NavController,
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
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
                    currentPlayer = gameViewModel.player
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
                    currentPlayer = gameViewModel.player
                )
            }

            is GameUiState.Draw -> {
                val gameOver = gameUiState.value as GameUiState.Draw
                GameScreen(
                    statusText = gameOver.room.status,
                    isShowRestartButton = gameViewModel.player == PlayerX,
                    onRestartButtonClick = { gameViewModel.onRestartClick() },
                    currentPlayer = gameViewModel.player
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

@Composable
fun GameScreenForPreview() {
    TicBackground {
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GameScreen(isPlayable = true, isShowRestartButton = true)
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
        color = colorScheme.primary,
        modifier = modifier
    )
}

@Composable
private fun GameScreen(
    room: Room = getRooms().random(),
    isPlayable: Boolean = true,
    crossing: Crossing? = null,
    statusText: String = statusTurnPlayer1,
    isShowRestartButton: Boolean = false,
    onCellClicked: (cell: Int) -> Unit = {},
    onRestartButtonClick: () -> Unit = {},
    currentPlayer: String = PlayerX,
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (title, turn, grid, restart, stats) = createRefs()
        StatusBox(
            message = statusText,
            modifier = Modifier.constrainAs(title) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
            })
        TurnBox(
            modifier = Modifier.constrainAs(turn) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(title.bottom, margin = 30.dp)
            },
            room = room, currentPlayer = currentPlayer
        )
        GameContainer(
            modifier = Modifier.constrainAs(grid) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(turn.bottom, margin = 20.dp)
                bottom.linkTo(stats.top)
            },
            room = room,
            crossing = crossing
        ) {
            // on cell clicked
            if (isPlayable) {
                onCellClicked(it)
            }
        }

        Stats(
            modifier = Modifier.constrainAs(stats) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(grid.bottom, margin = 20.dp)
                bottom.linkTo(restart.top)
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
    Button(
        onClick = { onRestartClick() },
    ) {
        Icon(
            imageVector = Icons.Default.Autorenew, contentDescription = null
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = "New game", fontSize = 20.sp)
    }
}

