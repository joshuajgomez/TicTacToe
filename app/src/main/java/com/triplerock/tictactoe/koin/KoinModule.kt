package com.triplerock.tictactoe.koin

import com.triplerock.tictactoe.model.Firebase
import com.triplerock.tictactoe.model.GameRepository
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
        GameRepository(get())
    }
    viewModel {
        GameViewModel()
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