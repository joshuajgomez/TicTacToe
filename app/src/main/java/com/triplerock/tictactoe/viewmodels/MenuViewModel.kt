package com.triplerock.tictactoe.viewmodels

import androidx.lifecycle.ViewModel
import com.triplerock.tictactoe.model.GameRepository

class MenuViewModel(
    private val gameRepository: GameRepository,
) : ViewModel() {

    var name = gameRepository.getName()

    fun setName(name: String): Boolean {
        gameRepository.setName(name)
        return true
    }
}