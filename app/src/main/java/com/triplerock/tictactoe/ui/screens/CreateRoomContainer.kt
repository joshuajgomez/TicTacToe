package com.triplerock.tictactoe.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
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
import androidx.navigation.NavController
import com.triplerock.tictactoe.data.Player1
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.ui.navGame
import com.triplerock.tictactoe.ui.navMenu
import com.triplerock.tictactoe.ui.screens.common.CustomTextButton
import com.triplerock.tictactoe.ui.screens.common.CustomTextField
import com.triplerock.tictactoe.ui.screens.common.Loading
import com.triplerock.tictactoe.ui.screens.common.RoomNameTags
import com.triplerock.tictactoe.ui.screens.common.TitleBar
import com.triplerock.tictactoe.ui.theme.TicTacToeTheme
import com.triplerock.tictactoe.utils.getPrettyTime
import com.triplerock.tictactoe.viewmodels.CreateRoomUiState
import com.triplerock.tictactoe.viewmodels.CreateRoomViewModel
import com.triplerock.tictactoe.viewmodels.sampleRoomNames
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun PreviewCreateRoom() {
    TicTacToeTheme {
        RoomName()
    }
}

@Preview
@Composable
fun PreviewCreateRoomContainer() {
    TicTacToeTheme {
        Column(Modifier.fillMaxSize()) {
            TitleBar(title = "Host a room") { }
            RoomName()
        }
    }
}

@Composable
fun CreateRoomContainer(
    createRoomViewModel: CreateRoomViewModel = koinViewModel(),
    navController: NavController,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TitleBar(title = "Host a room") { navController.navigate(navMenu) }
        val uiState = createRoomViewModel.uiState.collectAsState()
        when (uiState.value) {
            is CreateRoomUiState.CreateRoom ->
                RoomName(
                    onCreateRoomClick = { createRoomViewModel.createRoom(it) }
                )

            is CreateRoomUiState.RoomCreated -> {
                val room = (uiState.value as CreateRoomUiState.RoomCreated).room
                EmptyRoom(
                    room = room
                )
            }

            is CreateRoomUiState.Starting -> {
                Loading(message = "Starting")
                val roomId = (uiState.value as CreateRoomUiState.Starting).roomId
                navController.navigate("$navGame/$roomId/$Player1")
            }
        }
    }
}

@Composable
fun RoomName(
    modifier: Modifier = Modifier,
    defaultName: String = sampleRoomNames.random(),
    onCreateRoomClick: (roomName: String) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        var name by remember { mutableStateOf(defaultName) }
        CustomTextField(
            text = name,
            onTextChanged = { name = it },
            modifier = Modifier.padding(horizontal = 50.dp)
        )
        RoomNameTags(names = sampleRoomNames) {
            name = it
        }
        CustomTextButton(
            text = "Create",
        ) {
            onCreateRoomClick(name)
        }
    }
}

@Preview
@Composable
private fun EmptyRoom(room: Room = getRooms().random()) {
    TicTacToeTheme {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = room.name,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )
            Text(
                text = room.id,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary.copy(alpha = 0.4f)
            )
            Text(
                text = "created at ${getPrettyTime(room.timeCreated)}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.secondary.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Waiting for players",
                fontSize = 25.sp
            )
        }
    }
}


