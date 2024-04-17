package com.triplerock.tictactoe.viewmodels

import androidx.lifecycle.ViewModel
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.model.RoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class CreateRoomUiState {
    data class CreateRoom(val message: String) : CreateRoomUiState()
    data class RoomCreated(val room: Room) : CreateRoomUiState()
    data class Starting(val room: Room) : CreateRoomUiState()
}

class CreateRoomViewModel(private val roomRepository: RoomRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<CreateRoomUiState> =
        MutableStateFlow(CreateRoomUiState.CreateRoom(""))
    val uiState: StateFlow<CreateRoomUiState> = _uiState

    fun createRoom(roomName: String) {
        roomRepository.createRoom(
            roomName = roomName,
            onRoomCreated = {
                _uiState.value = CreateRoomUiState.RoomCreated(it)
            },
            onPlayerJoined = {
                _uiState.value = CreateRoomUiState.Starting(it)
            })
    }

}
