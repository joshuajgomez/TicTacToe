package com.triplerock.tictactoe.koin

import com.triplerock.tictactoe.GameViewModel
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val koinModule = module {
    viewModel {
        GameViewModel()
    }
}