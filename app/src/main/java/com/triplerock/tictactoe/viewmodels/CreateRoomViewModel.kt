package com.triplerock.tictactoe.viewmodels

import androidx.lifecycle.ViewModel
import com.triplerock.tictactoe.model.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class CreateRoomUiState {
    data class CreateRoom(val message: String) : CreateRoomUiState()
    data class EmptyRoom(val message: String) : CreateRoomUiState()
    data class Starting(val roomId: String) : CreateRoomUiState()
}

class CreateRoomViewModel(private val gameRepository: GameRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<CreateRoomUiState> =
        MutableStateFlow(CreateRoomUiState.CreateRoom(""))
    val uiState: StateFlow<CreateRoomUiState> = _uiState

    fun createRoom(roomName: String) {
        gameRepository.createRoom(
            roomName = roomName,
            onRoomCreated = { _uiState.value = CreateRoomUiState.EmptyRoom(it.name) },
            onPlayerJoined = {
                val message = "Starting game with $it"
                _uiState.value = CreateRoomUiState.Starting(message)
            })
    }

}
