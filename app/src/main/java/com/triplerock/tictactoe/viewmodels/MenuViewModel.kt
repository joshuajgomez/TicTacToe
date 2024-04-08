package com.triplerock.tictactoe.viewmodels

import androidx.lifecycle.ViewModel
import com.triplerock.tictactoe.model.GameRepository
import com.triplerock.tictactoe.utils.SharedPrefUtil

val sampleNames = listOf(
    "pico",
    "mini",
    "mike",
    "tiny",
    "mario",
    "big",
    "max",
)

val sampleRoomNames = listOf(
    "pac-room",
    "money-for-nothing",
    "middle-earth",
    "tiny-home",
    "max-people",
    "big-mac",
    "more-fun",
)

class MenuViewModel(
    private val gameRepository: GameRepository,
    sharedPrefUtil: SharedPrefUtil,
) : ViewModel() {

    var name = sharedPrefUtil.getName().ifEmpty { sampleNames.random() }

    fun setName(name: String): Boolean {
        gameRepository.setName(name)
        return true
    }
}