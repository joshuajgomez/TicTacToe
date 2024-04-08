package com.triplerock.tictactoe.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.ui.screens.common.Loading
import com.triplerock.tictactoe.ui.screens.common.TitleBar
import com.triplerock.tictactoe.ui.theme.TicTacToeTheme
import com.triplerock.tictactoe.viewmodels.JoinRoomUiState
import com.triplerock.tictactoe.viewmodels.JoinRoomViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun JoinRoomContainer(
    roomViewModel: JoinRoomViewModel = koinViewModel(),
    onBackClick: () -> Unit = {},
    onPlayerJoined: (roomId: String) -> Unit = {},
) {
    Column(Modifier.fillMaxSize()) {
        TitleBar(title = "Join a room") { onBackClick() }
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
                onPlayerJoined((uiState.value as JoinRoomUiState.Joined).roomId)
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewJoinRoomDarkTheme() {
    TicTacToeTheme {
        Column(Modifier.fillMaxWidth()) {
            TitleBar(title = "Join a room") { }
            Rooms(rooms = getRooms()) {}
        }
    }
}

@Preview()
@Composable
fun PreviewJoinRoomLightTheme() {
    TicTacToeTheme {
        Column(Modifier.fillMaxWidth()) {
            TitleBar(title = "Join a room") { }
            Rooms(rooms = getRooms()) {}
        }
    }
}

fun getRooms(): List<Room> = listOf(
    Room(name = "Jocha"),
    Room(name = "Manvila"),
    Room(name = "Visteon"),
    Room(name = "Tharavadu"),
)

@Composable
private fun Rooms(
    rooms: List<Room>,
    onJoinClick: (room: Room) -> Unit,
) {
    if (rooms.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 50.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(25.dp)
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = room.name,
            color = colorScheme.primary,
            fontSize = 25.sp
        )
        TextButton(
            onClick = { onJoinClick() },
            modifier = Modifier.background(
                shape = RoundedCornerShape(30.dp),
                color = colorScheme.primary
            )
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
    }
}

