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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.triplerock.tictactoe.ui.screens.common.Loading
import com.triplerock.tictactoe.ui.screens.common.TicBackground
import com.triplerock.tictactoe.ui.screens.common.TicSurface
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

//@Preview(showBackground = true)
@Composable
fun Stats(modifier: Modifier = Modifier, room: Room = getRooms().random()) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
            .border(
                width = 1.dp,
                color = colorScheme.onBackground,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(vertical = 10.dp)
    ) {
        Stat("${room.history.oWins} wins", Icons.Outlined.Circle)
        Stat("${room.history.xWins} wins", Icons.Outlined.Cancel)
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
            tint = colorScheme.onBackground
        )
        Text(
            text = label, fontSize = 18.sp,
            color = colorScheme.onBackground
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
        modifier = modifier.size(270.dp)
    ) {
        items(count = 9) {
            val cellState = if (player1Moves.contains(it)) Mark(
                icon = Icons.Outlined.Cancel,
                iconTint = colorScheme.error,
            )
            else if (player2Moves.contains(it)) Mark(
                icon = Icons.Outlined.Circle,
                iconTint = colorScheme.onBackground,
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
fun PreviewTurnBox3() {
    TicSurface {
        TurnBox()
    }
}

//@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTurnBoxDark() {
    TicSurface {
        TurnBox()
    }
}

@Composable
fun TurnBox(
    modifier: Modifier = Modifier,
    room: Room = getRooms().random(),
    currentPlayer: String = PlayerX,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TurnItem(
            mark = Mark(
                icon = Icons.Outlined.Cancel,
                iconTint = colorScheme.error,
            ),
            playerName = room.player1Name,
            isTurn = room.nextTurn == PlayerX,
            isCurrentPlayer = currentPlayer == PlayerX
        )
        TurnItem(
            mark = Mark(
                icon = Icons.Outlined.Circle,
                iconTint = colorScheme.onBackground,
            ),
            playerName = room.player2Name,
            isTurn = room.nextTurn == PlayerO,
            isCurrentPlayer = currentPlayer == PlayerO
        )
    }
}

@Composable
fun TurnItem(
    mark: Mark,
    playerName: String,
    isTurn: Boolean,
    isCurrentPlayer: Boolean = false,
) {
    val modifier = Modifier
        .padding(vertical = 10.dp)
        .height(100.dp)
        .width(100.dp)
        .clip(shape = RoundedCornerShape(20.dp))
        .border(
            width = if (isTurn) 3.dp else 1.dp,
            color = if (isTurn) colorScheme.primary else colorScheme.onBackground,
            shape = RoundedCornerShape(20.dp)
        )
        .padding(vertical = 0.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(top = 5.dp),
    ) {
        Icon(
            imageVector = mark.icon,
            contentDescription = null,
            tint = mark.iconTint,
            modifier = mark.modifier.size(40.dp)
        )
        Text(
            text = playerName, fontSize = 20.sp,
            color = if (isTurn) colorScheme.primary else
                colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(3.dp))
        if (isCurrentPlayer) {
            Text(
                text = "you",
                textAlign = TextAlign.Center,
                color = colorScheme.onPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(color = colorScheme.primary),
            )
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

const val rectOffset = 500f
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
    color: Color = colorScheme.primary
) {
    Canvas(
        modifier = modifier.size(180.dp)
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
            .size(90.dp)
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
    Box(modifier.size(270.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val line1hStart = Offset(0f, size.width / 3)
            val line1hEnd = Offset(size.width, size.width / 3)

            val line2hStart = Offset(0f, size.width / 3 * 2)
            val line2hEnd = Offset(size.width, size.width / 3 * 2)

            val line3hStart = Offset(0f, 0f)
            val line3hEnd = Offset(size.width, 0f)

            val line4hStart = Offset(0f, size.width)
            val line4hEnd = Offset(size.width, size.width)

            val line1vStart = Offset(size.width / 3, 0f)
            val line1vEnd = Offset(size.width / 3, size.width)

            val line2vStart = Offset(size.width / 3 * 2, 0f)
            val line2vEnd = Offset(size.width / 3 * 2, size.width)

            val line3vStart = Offset(0f, 0f)
            val line3vEnd = Offset(0f, size.width)

            val line4vStart = Offset(size.width, 0f)
            val line4vEnd = Offset(size.width, size.width)

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
            drawLine(
                color = color,
                start = line3vStart,
                end = line3vEnd,
                strokeWidth = stroke.toPx(),
            )
            drawLine(
                color = color,
                start = line4vStart,
                end = line4vEnd,
                strokeWidth = stroke.toPx(),
            )

            drawLine(
                color = color,
                start = line3hStart,
                end = line3hEnd,
                strokeWidth = stroke.toPx(),
            )


            drawLine(
                color = color,
                start = line4hStart,
                end = line4hEnd,
                strokeWidth = stroke.toPx(),
            )
        }
    }
}
