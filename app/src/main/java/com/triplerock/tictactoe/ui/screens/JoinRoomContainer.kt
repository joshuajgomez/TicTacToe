package com.triplerock.tictactoe.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Attractions
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.triplerock.tictactoe.data.PlayerO
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.ui.navHostGame
import com.triplerock.tictactoe.ui.navGame
import com.triplerock.tictactoe.ui.navMenu
import com.triplerock.tictactoe.ui.screens.common.CustomTextButton
import com.triplerock.tictactoe.ui.screens.common.Loading
import com.triplerock.tictactoe.ui.screens.common.TicSurface
import com.triplerock.tictactoe.ui.screens.common.TitleBar
import com.triplerock.tictactoe.ui.theme.textHostGame
import com.triplerock.tictactoe.ui.theme.textJoinGame
import com.triplerock.tictactoe.utils.getRelativeTime
import com.triplerock.tictactoe.viewmodels.JoinRoomUiState
import com.triplerock.tictactoe.viewmodels.JoinRoomViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun JoinRoomContainer(
    roomViewModel: JoinRoomViewModel = koinViewModel(),
    navController: NavController,
) {
    Column(Modifier.fillMaxSize()) {
        TitleBar(title = textJoinGame) { navController.navigate(navMenu) }
        val uiState = roomViewModel.uiState.collectAsState()
        when (uiState.value) {
            is JoinRoomUiState.EmptyRoom -> WaitingForPlayers {
                // on host game click
                navController.navigate(navHostGame)
            }

            is JoinRoomUiState.RoomFound -> Rooms(
                rooms = (uiState.value as JoinRoomUiState.RoomFound).rooms,
                onJoinClick = { roomViewModel.onJoinRoomClick(it) }
            )

            is JoinRoomUiState.Joining -> Loading(
                message = (uiState.value as JoinRoomUiState.Joining).message
            )

            is JoinRoomUiState.Joined -> {
                val roomId = (uiState.value as JoinRoomUiState.Joined).roomId
                navController.navigate("$navGame/$roomId/$PlayerO")
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewJoinRoomDarkTheme() {
    TicSurface {
        Column(Modifier.fillMaxWidth()) {
            TitleBar(title = textJoinGame) { }
            Rooms(rooms = getRooms()) {}
        }
    }
}

@Preview()
@Composable
fun PreviewJoinRoomLightTheme() {
    TicSurface {
        Column(Modifier.fillMaxWidth()) {
            TitleBar(title = textJoinGame) { }
            Rooms(rooms = getRooms()) {}
        }
    }
}

fun getRooms(): List<Room> = listOf(
    Room(name = "Jocha", id = "skdfhgweyigbcajkndfbks", player1Name = "pico"),
    Room(name = "Manvila", id = "skdfhgweyigbcajkndfbks", player1Name = "max"),
    Room(name = "Visteon", id = "skdfhgweyigbcajkndfbks", player1Name = "biggie"),
    Room(name = "Tharavadu", id = "skdfhgweyigbcajkndfbks", player1Name = "mini"),
)

@Composable
private fun Rooms(
    rooms: List<Room>,
    onJoinClick: (room: Room) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(rooms) {
            RoomItem(it) {
                onJoinClick(it)
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewEmptyRoomDark() {
    TicSurface {
        WaitingForPlayers()
    }
}

@Preview
@Composable
private fun PreviewEmptyRoomDarkLight() {
    TicSurface {
        WaitingForPlayers()
    }
}

@Composable
private fun WaitingForPlayers(onCreateRoomClick: () -> Unit = {}) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Attractions,
            contentDescription = null,
            modifier = Modifier.size(200.dp),
            tint = colorScheme.onBackground
        )
        Text(
            text = "No games yet",
            color = colorScheme.onBackground,
            fontSize = 30.sp
        )

        Text(
            text = "Would you like to host a game?",
            color = colorScheme.onBackground.copy(alpha = 0.8f),
            fontSize = 22.sp
        )
        CustomTextButton(text = textHostGame) {
            onCreateRoomClick()
        }
    }
}

@Composable
private fun RoomItem(room: Room, onJoinClick: () -> Unit) {
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth(),
    ) {
        val (roomName, roomId, timeCreated, button, line, player) = createRefs()
        Text(
            text = room.name,
            color = colorScheme.onBackground,
            fontSize = 25.sp,
            modifier = Modifier.constrainAs(roomName) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            }
        )
        Text(
            buildAnnotatedString {
                append("Created by ")
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(room.player1Name)
                }
            },
            modifier = Modifier.constrainAs(player) {
                start.linkTo(roomName.start)
                top.linkTo(roomName.bottom, margin = 3.dp)
            },
            color = colorScheme.onBackground
        )
        Text(
            text = getRelativeTime(room.timeCreated),
            color = colorScheme.onBackground.copy(alpha = 0.8f),
            fontSize = 15.sp,
            maxLines = 1,
            modifier = Modifier
                .constrainAs(timeCreated) {
                    start.linkTo(parent.start)
                    top.linkTo(player.bottom, margin = 3.dp)
                }
        )
        Text(
            text = " | ${room.id}",
            color = colorScheme.onBackground.copy(alpha = 0.6f),
            fontSize = 15.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .constrainAs(roomId) {
                    start.linkTo(timeCreated.end)
                    top.linkTo(timeCreated.top)
                }
                .width(170.dp)
        )
        JoinButton(modifier = Modifier.constrainAs(button) {
            end.linkTo(parent.end)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        }) {
            onJoinClick()
        }
        Divider(modifier = Modifier.constrainAs(line) {
            start.linkTo(parent.start)
            bottom.linkTo(parent.bottom)
            top.linkTo(roomId.bottom, margin = 10.dp)
        })
    }
}

@Composable
fun JoinButton(
    modifier: Modifier = Modifier,
    onJoinClick: () -> Unit,
) {
    TextButton(
        onClick = { onJoinClick() },
        modifier = modifier
            .background(
                shape = RoundedCornerShape(30.dp),
                color = colorScheme.onBackground
            )
            .height(40.dp)
    ) {
        Text(
            text = "Join",
            color = colorScheme.surface
        )
        Spacer(modifier = Modifier.width(5.dp))
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = colorScheme.surface
        )
    }
}

