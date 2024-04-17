package com.triplerock.tictactoe.koin

import androidx.lifecycle.SavedStateHandle
import com.triplerock.tictactoe.model.RoomRepository
import com.triplerock.tictactoe.model.gamemanager.Firebase
import com.triplerock.tictactoe.model.gamemanager.LocalMultiPlayerGame
import com.triplerock.tictactoe.model.gamemanager.OnlineGame
import com.triplerock.tictactoe.model.gamemanager.SinglePlayerGame
import com.triplerock.tictactoe.utils.SharedPrefUtil
import com.triplerock.tictactoe.viewmodels.CreateRoomViewModel
import com.triplerock.tictactoe.viewmodels.GameViewModel
import com.triplerock.tictactoe.viewmodels.JoinRoomViewModel
import com.triplerock.tictactoe.viewmodels.MenuViewModel
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val koinModule = module {
    single {
        Firebase()
    }
    single {
        SinglePlayerGame()
    }
    single {
        LocalMultiPlayerGame()
    }
    single {
        OnlineGame(get<Firebase>())
    }
    single {
        SharedPrefUtil(get())
    }
    single {
        RoomRepository(get(), get())
    }
    viewModel { (handle: SavedStateHandle) ->
        GameViewModel(handle)
    }
    viewModel {
        CreateRoomViewModel(get())
    }
    viewModel {
        JoinRoomViewModel(get())
    }
    viewModel {
        MenuViewModel(get())
    }
}