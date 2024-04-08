package com.triplerock.tictactoe.koin

import androidx.lifecycle.SavedStateHandle
import com.triplerock.tictactoe.model.Firebase
import com.triplerock.tictactoe.model.GameRepository
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
        SharedPrefUtil(get())
    }
    single {
        SharedPrefUtil(get())
    }
    single {
        GameRepository(get(), get())
    }
    viewModel { (handle: SavedStateHandle) ->
        GameViewModel(handle, get())
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