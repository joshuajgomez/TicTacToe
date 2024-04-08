package com.triplerock.tictactoe.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.triplerock.tictactoe.model.GameRepository
import com.triplerock.tictactoe.utils.Logger
import com.triplerock.tictactoe.utils.SharedPrefUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MenuViewModel(private val gameRepository: GameRepository, sharedPrefUtil: SharedPrefUtil) :
    ViewModel() {

    var name = sharedPrefUtil.getName()

    fun setName(name: String): Boolean {
        if (name.isEmpty()) {
            Logger.error("name cannot be empty")
            return false
        }
        gameRepository.setName(name)
        return true
    }
}