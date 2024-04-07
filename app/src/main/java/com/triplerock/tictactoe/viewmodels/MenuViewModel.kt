package com.triplerock.tictactoe.viewmodels

import androidx.lifecycle.ViewModel
import com.triplerock.tictactoe.model.GameRepository
import com.triplerock.tictactoe.utils.Logger

class MenuViewModel(private val gameRepository: GameRepository) : ViewModel() {
    fun setName(name: String): Boolean {
        if (name.isEmpty()) {
            Logger.error("name cannot be empty")
            return false
        }
        gameRepository.setName(name)
        return true
    }
}