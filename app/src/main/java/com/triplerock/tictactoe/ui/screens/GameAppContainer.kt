package com.triplerock.tictactoe.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.triplerock.tictactoe.utils.Logger
import com.triplerock.tictactoe.viewmodels.CreateRoomViewModel
import com.triplerock.tictactoe.viewmodels.GameViewModel
import com.triplerock.tictactoe.viewmodels.JoinRoomViewModel
import com.triplerock.tictactoe.viewmodels.MenuViewModel

const val navMenu = "menu_screen"
const val navCreateRoom = "create_room_screen"
const val navJoinRoom = "join_room_screen"
const val navGame = "game_screen"
const val navCredits = "credits_screen"

@Composable
fun GameAppContainer(
    navController: NavHostController,
    menuViewModel: MenuViewModel,
    createRoomViewModel: CreateRoomViewModel,
    joinRoomViewModel: JoinRoomViewModel,
    gameViewModel: GameViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = navMenu,
    ) {
        composable(navMenu) {
            Logger.debug("navigate=$navMenu")
            MenuContainer(
                menuViewModel = menuViewModel,
                onMenuClick = { navController.navigate(it) },
            )
        }

        composable(navCreateRoom) {
            Logger.debug("navigate=$navCreateRoom")
            CreateRoomContainer(
                createRoomViewModel = createRoomViewModel,
                onPlayerJoined = {
                    navController.navigate(navGame)
                },
                onBackClick = {
                    navController.navigate(navMenu)
                })
        }

        composable(navJoinRoom) {
            Logger.debug("navigate=$navJoinRoom")
            JoinRoomContainer(
                roomViewModel = joinRoomViewModel,
                onPlayerJoined = {
                    navController.navigate(navGame)
                },
                onBackClick = {
                    navController.navigate(navMenu)
                }
            )
        }

        composable(navGame) {
            Logger.debug("navigate=$navGame")
            GameScreenContainer(
                gameViewModel = gameViewModel,
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