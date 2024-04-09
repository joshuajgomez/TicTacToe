package com.triplerock.tictactoe.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.triplerock.tictactoe.data.Player2
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.ui.navGame
import com.triplerock.tictactoe.ui.navMenu
import com.triplerock.tictactoe.ui.screens.common.Loading
import com.triplerock.tictactoe.ui.screens.common.TitleBar
import com.triplerock.tictactoe.ui.theme.TicTacToeTheme
import com.triplerock.tictactoe.utils.getPrettyTime
import com.triplerock.tictactoe.viewmodels.JoinRoomUiState
import com.triplerock.tictactoe.viewmodels.JoinRoomViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun JoinRoomContainer(
    roomViewModel: JoinRoomViewModel = koinViewModel(),
    navController: NavController,
) {
    Column(Modifier.fillMaxSize()) {
        TitleBar(title = "Join a room") { navController.navigate(navMenu) }
        val uiState = roomViewModel.uiState.collectAsState()
        when (uiState.value) {
            is JoinRoomUiState.EmptyRoom -> EmptyRoom()
            is JoinRoomUiState.RoomFound -> Rooms(
                rooms = (uiState.value as JoinRoomUiState.RoomFound).rooms,
                onJoinClick = { roomViewModel.onJoinRoomClick(it) }
            )

            is JoinRoomUiState.Joining -> Loading(
                message = (uiState.value as JoinRoomUiState.Joining).message
            )

            is JoinRoomUiState.Joined -> {
                val roomId = (uiState.value as JoinRoomUiState.Joined).roomId
                navController.navigate("$navGame/$roomId/$Player2")
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewJoinRoomDarkTheme() {
    TicTacToeTheme {
        Surface(color = colorScheme.background) {
            Column(Modifier.fillMaxWidth()) {
                TitleBar(title = "Join a room") { }
                Rooms(rooms = getRooms()) {}
            }
        }
    }
}

@Preview()
@Composable
fun PreviewJoinRoomLightTheme() {
    TicTacToeTheme {
        Surface(color = colorScheme.background) {
            Column(Modifier.fillMaxWidth()) {
                TitleBar(title = "Join a room") { }
                Rooms(rooms = getRooms()) {}
            }
        }
    }
}

fun getRooms(): List<Room> = listOf(
    Room(name = "Jocha", id = "skdfhgweyigbcajkndfbks"),
    Room(name = "Manvila", id = "skdfhgweyigbcajkndfbks"),
    Room(name = "Visteon", id = "skdfhgweyigbcajkndfbks"),
    Room(name = "Tharavadu", id = "skdfhgweyigbcajkndfbks"),
)

@Composable
private fun Rooms(
    rooms: List<Room>,
    onJoinClick: (room: Room) -> Unit,
) {
    if (rooms.isNotEmpty()) {
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
    } else {
        EmptyRoom()
    }
}

@Preview
@Composable
private fun EmptyRoom(onCreateRoomClick: () -> Unit = {}) {
    TicTacToeTheme {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 30.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Waiting for rooms")
            TextButton(
                onClick = onCreateRoomClick,
                modifier = Modifier.background(
                    shape = RoundedCornerShape(30.dp),
                    color = colorScheme.primary
                )
            ) {
                Text(
                    text = "Create a room",
                    color = colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun RoomItem(room: Room, onJoinClick: () -> Unit) {
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth(),
    ) {
        val (roomName, roomId, timeCreated, button, line) = createRefs()
        Text(
            text = room.name,
            color = colorScheme.primary,
            fontSize = 25.sp,
            modifier = Modifier.constrainAs(roomName) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            }
        )
        Text(
            text = getPrettyTime(room.timeCreated),
            color = colorScheme.secondary.copy(alpha = 0.6f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .constrainAs(timeCreated) {
                    start.linkTo(parent.start)
                    top.linkTo(roomName.bottom)
                }
        )
        Text(
            text = " | ${room.id}",
            color = colorScheme.secondary.copy(alpha = 0.6f),
            fontSize = 15.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .constrainAs(roomId) {
                    start.linkTo(timeCreated.end)
                    top.linkTo(roomName.bottom)
                }
                .width(170.dp)
        )
        TextButton(
            onClick = { onJoinClick() },
            modifier = Modifier
                .background(
                    shape = RoundedCornerShape(30.dp),
                    color = colorScheme.secondary
                )
                .constrainAs(button) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                }
        ) {
            Text(
                text = "Join",
                color = colorScheme.onPrimary
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = colorScheme.onPrimary
            )
        }
        Divider(modifier = Modifier.constrainAs(line) {
            start.linkTo(parent.start)
            bottom.linkTo(parent.bottom)
            top.linkTo(roomId.bottom, margin = 10.dp)
        })
    }
}

