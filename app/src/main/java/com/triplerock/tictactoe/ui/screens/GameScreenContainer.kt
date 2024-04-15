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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Clear
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
import com.triplerock.tictactoe.data.PlayerO
import com.triplerock.tictactoe.data.PlayerX
import com.triplerock.tictactoe.data.Room
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
        val playerName = gameViewModel.playerName
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
                    playerName = playerName,
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
                    playerName = playerName,
                )
            }

            is GameUiState.Draw -> {
                val gameOver = gameUiState.value as GameUiState.Draw
                GameScreen(
                    statusText = gameOver.room.status,
                    isShowRestartButton = gameViewModel.player == PlayerX,
                    onRestartButtonClick = { gameViewModel.onRestartClick() },
                    playerName = playerName,
                )
            }
        }
    }

}

//@Preview(device = Devices.PIXEL_2)
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
            MarkBox(player1Moves = moves.subList(0, 4), player2Moves = moves)
            crossingList.forEach {
                CrossingLine(
                    crossing = it,
                )
            }
        }
    }
}

//@Preview
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
    val modifier: Modifier = Modifier,
    val iconTint: Color = Color.Gray,
    val isEmpty: Boolean = false,
)

@Composable
private fun GameScreen(
    room: Room = getRooms().random(),
    isPlayable: Boolean = true,
    playerName: String = sampleNames.random(),
    crossing: Crossing? = null,
    statusText: String = statusTurnPlayer1,
    isShowRestartButton: Boolean = false,
    onCellClicked: (cell: Int) -> Unit = {},
    onRestartButtonClick: () -> Unit = {},
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (status, name, turn, grid, restart, stats) = createRefs()
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
        }, playerName = playerName, roomName = room.name)
        TurnBox(modifier = Modifier.constrainAs(turn) {
            top.linkTo(name.top)
            end.linkTo(parent.end)
        }, isPlayable = isPlayable, xTurn = room.nextTurn == PlayerX)
        GameContainer(
            modifier = Modifier.constrainAs(grid) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(name.bottom, margin = 60.dp)
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
                top.linkTo(grid.bottom)
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

@Preview(showBackground = true)
@Composable
fun Stats(modifier: Modifier = Modifier, room: Room = getRooms().random()) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 10.dp)
            .background(
                shape = RoundedCornerShape(10.dp),
                color = colorScheme.onBackground
            )
            .padding(10.dp)
    ) {
        Stat("${room.history.oWins} wins", Icons.Outlined.Circle)
        Stat("${room.history.xWins} wins", Icons.Outlined.Clear)
        Stat("${room.history.draws} draws", Icons.Outlined.Balance)
    }
}

@Composable
fun Stat(label: String, icon: ImageVector = Icons.Default.Close) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            tint = colorScheme.surface
        )
        Text(
            text = label, fontSize = 18.sp,
            color = colorScheme.surface
        )
    }
}

//@Preview
@Composable
fun PreviewTurnBox() {
    TicBackground {
        TurnBox(isPlayable = false)
    }
}

//@Preview
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
    room: Room,
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
            room.moves[PlayerX]!!, room.moves[PlayerX]!!, onCellClicked
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
            val cellState = if (player1Moves.contains(it)) Mark(
                icon = Icons.Default.Clear,
                iconTint = colorScheme.error,
            )
            else if (player2Moves.contains(it)) Mark(
                icon = Icons.Outlined.Circle,
                iconTint = colorScheme.onBackground,
                modifier = Modifier.size(65.dp)
            )
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
    color: Color = colorScheme.primary
) {
    Canvas(
        modifier = modifier.size(200.dp)
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
            .size(100.dp)
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
fun LineBox(
    modifier: Modifier = Modifier,
    color: Color = colorScheme.onBackground,
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
