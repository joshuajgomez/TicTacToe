package com.triplerock.tictactoe.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.triplerock.tictactoe.utils.Logger
import com.triplerock.tictactoe.viewmodels.CreateRoomViewModel
import com.triplerock.tictactoe.viewmodels.GameViewModel
import com.triplerock.tictactoe.viewmodels.JoinRoomViewModel
import com.triplerock.tictactoe.viewmodels.MenuViewModel
import org.koin.androidx.compose.koinViewModel

const val navMenu = "menu_screen"
const val navCreateRoom = "create_room_screen"
const val navJoinRoom = "join_room_screen"
const val navGame = "game_screen"
const val navCredits = "credits_screen"

@Composable
fun GameAppContainer(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = navMenu,
    ) {
        composable(navMenu) {
            Logger.debug("navigate=$navMenu")
            MenuContainer(
                onMenuClick = { navController.navigate(it) },
            )
        }

        composable(navCreateRoom) {
            Logger.debug("navigate=$navCreateRoom")
            CreateRoomContainer(
                onPlayerJoined = {
                    navController.navigate("$navGame/$it")
                },
                onBackClick = {
                    navController.navigate(navMenu)
                })
        }

        composable(navJoinRoom) {
            Logger.debug("navigate=$navJoinRoom")
            JoinRoomContainer(
                onPlayerJoined = {
                    navController.navigate("$navGame/$it")
                },
                onBackClick = {
                    navController.navigate(navMenu)
                }
            )
        }

        composable("$navGame/{roomId}") {
            Logger.debug("navigate=$navGame")
            GameScreenContainer(
                onBackClick = {
                    navController.navigate(navMenu)
                }
            )
        }

        composable(navCredits) {
            Logger.debug("navigate=$navCredits")
            CreditsContainer(
                onBackClick = {
                    navController.navigate(navMenu)
                }
            )
        }
    }
}