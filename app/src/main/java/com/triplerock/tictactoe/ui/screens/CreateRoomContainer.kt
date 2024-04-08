package com.triplerock.tictactoe.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.triplerock.tictactoe.ui.screens.common.CustomTextButton
import com.triplerock.tictactoe.ui.screens.common.Loading
import com.triplerock.tictactoe.ui.screens.common.TitleBar
import com.triplerock.tictactoe.ui.theme.TicTacToeTheme
import com.triplerock.tictactoe.viewmodels.CreateRoomUiState
import com.triplerock.tictactoe.viewmodels.CreateRoomViewModel
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun PreviewCreateRoom() {
    TicTacToeTheme {
        RoomName {
        }
    }
}

@Composable
fun CreateRoomContainer(
    createRoomViewModel: CreateRoomViewModel = koinViewModel(),
    onPlayerJoined: (roomId: String) -> Unit,
    onBackClick: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        TitleBar(title = "Host a room") { onBackClick() }
        val uiState = createRoomViewModel.uiState.collectAsState()
        when (uiState.value) {
            is CreateRoomUiState.CreateRoom ->
                RoomName(
                    onCreateRoomClick = { createRoomViewModel.createRoom(it) }
                )

            is CreateRoomUiState.EmptyRoom -> {
                EmptyPlayers(
                    roomName = (uiState.value as CreateRoomUiState.EmptyRoom).message
                )
            }

            is CreateRoomUiState.Starting -> {
                Loading(message = "Starting")
                onPlayerJoined((uiState.value as CreateRoomUiState.Starting).roomId)
            }
        }
    }
}

@Composable
fun RoomName(modifier: Modifier = Modifier, onCreateRoomClick: (roomName: String) -> Unit) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        var name by remember { mutableStateOf("") }
        TextField(
            value = name,
            onValueChange = { name = it },
            placeholder = {
                Text(text = "Enter room name")
            }
        )
        CustomTextButton(
            text = "Create",
        ) {
            onCreateRoomClick(name)
        }
    }
}

@Preview
@Composable
private fun EmptyPlayers(roomName: String = "Parassala") {
    TicTacToeTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Room: $roomName",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = "Waiting for players")
        }
    }
}


