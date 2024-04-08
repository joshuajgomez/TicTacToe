package com.triplerock.tictactoe.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.triplerock.tictactoe.data.Player1
import com.triplerock.tictactoe.data.Player2
import com.triplerock.tictactoe.ui.screens.CreateRoomContainer
import com.triplerock.tictactoe.ui.screens.CreditsContainer
import com.triplerock.tictactoe.ui.screens.GameScreenContainer
import com.triplerock.tictactoe.ui.screens.JoinRoomContainer
import com.triplerock.tictactoe.ui.screens.MenuContainer
import com.triplerock.tictactoe.utils.Logger
import com.triplerock.tictactoe.viewmodels.navKeyPlayer
import com.triplerock.tictactoe.viewmodels.navKeyRoomId

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
                    navController.navigate("$navGame/$it/$Player1")
                },
                onBackClick = {
                    navController.navigate(navMenu)
                })
        }

        composable(navJoinRoom) {
            Logger.debug("navigate=$navJoinRoom")
            JoinRoomContainer(
                onPlayerJoined = {
                    navController.navigate("$navGame/$it/$Player2")
                },
                onBackClick = {
                    navController.navigate(navMenu)
                }
            )
        }

        composable("$navGame/{$navKeyRoomId}/{$navKeyPlayer}") {
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