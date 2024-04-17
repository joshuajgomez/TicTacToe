package com.triplerock.tictactoe.viewmodels

import androidx.lifecycle.ViewModel
import com.triplerock.tictactoe.model.RoomRepository

class MenuViewModel(
    private val roomRepository: RoomRepository,
) : ViewModel() {

    var name = roomRepository.getName()

    fun setName(name: String): Boolean {
        roomRepository.setName(name)
        return true
    }
}