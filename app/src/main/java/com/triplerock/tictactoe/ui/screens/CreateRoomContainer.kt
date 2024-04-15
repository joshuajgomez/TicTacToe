package com.triplerock.tictactoe.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RamenDining
import androidx.compose.material3.Icon
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
import com.triplerock.tictactoe.data.PlayerX
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.data.sampleRoomNames
import com.triplerock.tictactoe.ui.navGame
import com.triplerock.tictactoe.ui.navMenu
import com.triplerock.tictactoe.ui.screens.common.CustomTextButton
import com.triplerock.tictactoe.ui.screens.common.CustomTextField
import com.triplerock.tictactoe.ui.screens.common.RoomNameTags
import com.triplerock.tictactoe.ui.screens.common.TicSurface
import com.triplerock.tictactoe.ui.screens.common.TitleBar
import com.triplerock.tictactoe.ui.theme.TicTacToeTheme
import com.triplerock.tictactoe.ui.theme.textHostGame
import com.triplerock.tictactoe.utils.getPrettyTime
import com.triplerock.tictactoe.viewmodels.CreateRoomUiState
import com.triplerock.tictactoe.viewmodels.CreateRoomViewModel
import org.koin.androidx.compose.koinViewModel

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewCreateRoomContainerDark() {
    TicSurface {
        Column(Modifier.fillMaxSize()) {
            TitleBar(title = textHostGame) { }
            RoomName()
        }
    }
}

@Preview
@Composable
fun PreviewCreateRoomContainerLight() {
    TicSurface {
        Column(Modifier.fillMaxSize()) {
            TitleBar(title = textHostGame) { }
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
        TitleBar(title = textHostGame) { navController.navigate(navMenu) }
        val uiState = createRoomViewModel.uiState.collectAsState()
        when (uiState.value) {
            is CreateRoomUiState.CreateRoom ->
                RoomName(
                    onCreateRoomClick = { createRoomViewModel.createRoom(it) }
                )

            is CreateRoomUiState.RoomCreated -> {
                val room = (uiState.value as CreateRoomUiState.RoomCreated).room
                WaitingForPlayers(room = room)
            }

            is CreateRoomUiState.Starting -> {
                val starting = uiState.value as CreateRoomUiState.Starting
                val room = starting.room
                WaitingForPlayers(room = room, status = "Starting game")
                navController.navigate("$navGame/${room.id}/$PlayerX")
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
        Text(
            text = "Choose a name for the room",
            color = colorScheme.onBackground
        )
        CustomTextField(
            text = name,
            onTextChanged = { name = it },
            modifier = Modifier.padding(horizontal = 50.dp)
        )
        RoomNameTags(names = sampleRoomNames) {
            name = it
        }
        CustomTextButton(
            text = textHostGame,
        ) {
            onCreateRoomClick(name)
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
private fun PreviewEmptyRoomLight() {
    TicSurface {
        WaitingForPlayers()
    }
}

@Composable
private fun WaitingForPlayers(
    room: Room = getRooms().random(),
    status: String = "Waiting for players"
) {
    TicTacToeTheme {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = room.name,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )
            Text(
                text = room.id,
                color = colorScheme.primary.copy(alpha = 0.8f)
            )
            Text(
                text = "created at ${getPrettyTime(room.timeCreated)}",
                color = colorScheme.secondary.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(30.dp))
            Icon(
                imageVector = Icons.Outlined.RamenDining,
                contentDescription = null,
                modifier = Modifier.size(200.dp),
                tint = colorScheme.onBackground.copy(alpha = 0.8f)
            )
            Text(
                text = status,
                fontSize = 30.sp,
                color = colorScheme.secondary
            )
        }
    }
}


