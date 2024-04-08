package com.triplerock.tictactoe.viewmodels

import androidx.lifecycle.ViewModel
import com.triplerock.tictactoe.model.GameRepository
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class JoinRoomUiState {
    data class EmptyRoom(val message: String) : JoinRoomUiState()
    data class RoomFound(val rooms: List<Room>) : JoinRoomUiState()
    data class Joining(val message: String) : JoinRoomUiState()
    data class Joined(val roomId: String) : JoinRoomUiState()
}

class JoinRoomViewModel(private val gameRepository: GameRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<JoinRoomUiState> =
        MutableStateFlow(JoinRoomUiState.EmptyRoom(""))
    val uiState: StateFlow<JoinRoomUiState> = _uiState

    init {
        Logger.entry()
        gameRepository.findRooms {
            // on room found
            _uiState.value = JoinRoomUiState.RoomFound(it)
        }
    }

    fun onJoinRoomClick(room: Room) {
        val message = "Joining room ${room.name}"
        _uiState.value = JoinRoomUiState.Joining(message)
        gameRepository.joinRoom(room) {
            // on room joined
            _uiState.value = JoinRoomUiState.Joined(room.id)
        }
    }

}
