package com.triplerock.tictactoe.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.triplerock.tictactoe.ui.navMenu
import com.triplerock.tictactoe.ui.screens.common.TitleBar

@Composable
fun CreditsContainer(navController: NavController) {
    Column {
        TitleBar(
            title = "Credits",
            onBackClick = { navController.navigate(navMenu) }
        )
        Text(text = "Credits not written yet")
    }
}