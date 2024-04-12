package com.triplerock.tictactoe.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.triplerock.tictactoe.data.sampleNames
import com.triplerock.tictactoe.data.sampleRoomNames
import com.triplerock.tictactoe.ui.screens.common.Loading
import com.triplerock.tictactoe.ui.screens.common.TicBackground
import com.triplerock.tictactoe.ui.screens.common.TicSurface
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
        Modifier
            .fillMaxSize()
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val gameUiState = gameViewModel.uiState.collectAsState()
        val roomName = gameViewModel.roomName
        val playerName = gameViewModel.myName
        val isPlayer1 = gameViewModel.isPlayer1()
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
                    onCellClicked = { gameViewModel.onCellClick(it) },
                    player1Moves = nextTurn.player1Moves,
                    player2Moves = nextTurn.player2Moves,
                    statusText = nextTurn.statusText,
                    isPlayable = nextTurn.isMyTurn,
                    roomName = roomName,
                    playerName = playerName,
                    xTurn = gameViewModel.xTurn()
                )
            }

            is GameUiState.Winner -> {
                val winner = gameUiState.value as GameUiState.Winner
                GameScreen(
                    player1Moves = winner.player1Moves,
                    player2Moves = winner.player2Moves,
                    statusText = winner.statusText,
                    crossing = winner.crossing,
                    isShowRestartButton = isPlayer1,
                    onRestartButtonClick = { gameViewModel.onRestartClick() },
                    roomName = roomName,
                    playerName = playerName,
                    xTurn = gameViewModel.xTurn()
                )
            }

            is GameUiState.Draw -> {
                val gameOver = gameUiState.value as GameUiState.Draw
                GameScreen(
                    player1Moves = gameOver.player1Moves,
                    player2Moves = gameOver.player2Moves,
                    statusText = gameOver.statusText,
                    isShowRestartButton = isPlayer1,
                    onRestartButtonClick = { gameViewModel.onRestartClick() },
                    roomName = roomName,
                    playerName = playerName,
                    xTurn = gameViewModel.xTurn()
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

val moves = listOf(
    0, 1, 2, 3, 4, 5, 6
)

@Preview
@Composable
private fun PreviewGameBox() {
    TicSurface {
        Box(contentAlignment = Alignment.Center) {
            LineBox()
            MarkBox(player1Moves = moves, player2Moves = moves)
            crossingList.forEach {
                CrossingLine(
                    crossing = it,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewStatusBox() {
    TicBackground {
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
        fontSize = 25.sp,
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
    xTurn: Boolean = false,
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
        val (status, name, turn, grid, restart) = createRefs()
        StatusBox(
            message = statusText,
            modifier = Modifier.constrainAs(status) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
            })
        IdBox(modifier = Modifier.constrainAs(name) {
            start.linkTo(parent.start)
            top.linkTo(status.bottom, margin = 40.dp)
        }, playerName = playerName, roomName = roomName)
        TurnBox(modifier = Modifier.constrainAs(turn) {
            top.linkTo(name.top)
            end.linkTo(parent.end)
        }, isPlayable = isPlayable, xTurn = xTurn)
        GameContainer(
            modifier = Modifier.constrainAs(grid) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(name.bottom, margin = 80.dp)
            },
            player1Moves = player1Moves,
            player2Moves = player2Moves,
            crossing = crossing
        ) {
            // on cell clicked
            if (isPlayable) {
                onCellClicked(it)
            }
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
fun PreviewTurnBox() {
    TicBackground {
        TurnBox(isPlayable = false)
    }
}

@Preview
@Composable
fun PreviewTurnBox2() {
    TicBackground {
        TurnBox(xTurn = false)
    }
}

@Composable
fun TurnBox(
    modifier: Modifier = Modifier,
    isPlayable: Boolean = true,
    xTurn: Boolean = true,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        Switch(
            checked = xTurn,
            onCheckedChange = { },
            colors = SwitchDefaults.colors(
                uncheckedIconColor = colorScheme.onSurface,
                uncheckedTrackColor = colorScheme.primary,
                uncheckedBorderColor = colorScheme.primary,
                uncheckedThumbColor = colorScheme.onPrimary,
            ),
            thumbContent = {
                if (xTurn)
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize)
                    )
                else
                    Icon(
                        imageVector = Icons.Outlined.Circle,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize)
                    )
            },
        )
        Text(
            text = "${
                if (isPlayable) ""
                else "not "
            }your turn",
            fontSize = 15.sp,
            modifier = Modifier
                .background(
                    shape = RoundedCornerShape(10.dp),
                    color = colorScheme.tertiary
                )
                .padding(bottom = 5.dp, start = 10.dp, end = 10.dp),
            color = colorScheme.background
        )
    }
}

@Composable
fun GameContainer(
    modifier: Modifier = Modifier,
    crossing: Crossing? = null,
    player1Moves: List<Int>,
    player2Moves: List<Int>,
    onCellClicked: (cell: Int) -> Unit = {},
) {
    ConstraintLayout(modifier) {
        val (grid, crossingContainer) = createRefs()
        LineBox()
        MarkBox(
            modifier = Modifier.constrainAs(grid) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            player1Moves, player2Moves, onCellClicked
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
        modifier = modifier.size(300.dp)
    ) {
        items(count = 9) {
            val cellState = if (player1Moves.contains(it)) Mark(Icons.Default.Clear)
            else if (player2Moves.contains(it)) Mark(Icons.Outlined.Circle)
            else Mark(isEmpty = true)
            Cell(mark = cellState) {
                onCellClicked(it)
            }
        }
    }
}

@Preview
@Composable
fun PreviewIdBox() {
    TicBackground {
        IdBox()
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
fun Info(
    label: String,
    info: String,
    color: Color = colorScheme.onSurface,
) {
    Row {
        Text(text = label, color = color)
        Text(text = info, fontWeight = FontWeight.Bold, color = color)
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

@Composable
fun CrossingLine(
    modifier: Modifier = Modifier,
    crossing: Crossing,
) {
    Canvas(
        modifier = modifier.size(200.dp)
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

@Composable
fun LineBox(
    modifier: Modifier = Modifier,
    color: Color = colorScheme.primary,
    stroke: Dp = 2.dp,
) {
    Box(modifier.size(300.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val line1hStart = Offset(0f, size.width / 3)
            val line1hEnd = Offset(size.width, size.width / 3)

            val line2hStart = Offset(0f, size.width / 3 * 2)
            val line2hEnd = Offset(size.width, size.width / 3 * 2)

            val line1vStart = Offset(size.width / 3, 0f)
            val line1vEnd = Offset(size.width / 3, size.width)

            val line2vStart = Offset(size.width / 3 * 2, 0f)
            val line2vEnd = Offset(size.width / 3 * 2, size.width)

            drawLine(
                color = color,
                start = line1hStart,
                end = line1hEnd,
                strokeWidth = stroke.toPx(),
            )
            drawLine(
                color = color,
                start = line2hStart,
                end = line2hEnd,
                strokeWidth = stroke.toPx(),
            )
            drawLine(
                color = color,
                start = line1vStart,
                end = line1vEnd,
                strokeWidth = stroke.toPx(),
            )
            drawLine(
                color = color,
                start = line2vStart,
                end = line2vEnd,
                strokeWidth = stroke.toPx(),
            )
        }
    }
}
