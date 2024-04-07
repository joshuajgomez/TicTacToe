package com.triplerock.tictactoe.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.triplerock.tictactoe.ui.screens.common.TitleBar

@Composable
fun CreditsContainer(onBackClick: () -> Unit) {
    Column {
        TitleBar(
            title = "Credits",
            onBackClick = onBackClick
        )
        Text(text = "Credits not written yet")
    }
}