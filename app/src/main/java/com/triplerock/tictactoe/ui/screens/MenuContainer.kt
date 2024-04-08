package com.triplerock.tictactoe.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.triplerock.tictactoe.ui.navCreateRoom
import com.triplerock.tictactoe.ui.navCredits
import com.triplerock.tictactoe.ui.navJoinRoom
import com.triplerock.tictactoe.ui.theme.TicTacToeTheme
import com.triplerock.tictactoe.utils.Logger
import com.triplerock.tictactoe.viewmodels.MenuViewModel
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun PreviewMenu() {
    TicTacToeTheme {
        Menu()
    }
}

@Composable
fun MenuContainer(
    menuViewModel: MenuViewModel = koinViewModel(),
    onMenuClick: (destination: String) -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val nameValue = menuViewModel.name
        var name by remember { mutableStateOf(nameValue) }
        var errorStatus by remember { mutableStateOf("") }
        Menu(
            name = name,
            errorStatus = errorStatus,
            onNameChange = {
                name = it
                if (name.isNotEmpty()) errorStatus = ""
            },
            onMenuClick = {
                if (menuViewModel.setName(name)) onMenuClick(it)
                else errorStatus = "Name cannot be empty"
            },
            onNameSelect = {
                name = it
                menuViewModel.setName(it)
            }
        )

    }
}

@Composable
fun Menu(
    name: String = "",
    errorStatus: String = "",
    onNameChange: (name: String) -> Unit = {},
    onMenuClick: (destination: String) -> Unit = {},
    onNameSelect: (name: String) -> Unit = {}
) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "TicTacToe",
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.height(20.dp))

        TextField(value = name,
            onValueChange = { onNameChange(it) },
            placeholder = { Text(text = "Enter a name") })

        NameTags { onNameSelect(it) }

        Spacer(modifier = Modifier.height(20.dp))

        MenuItem(
            text = "Host new game",
            icon = Icons.Default.Add,
            onClick = { onMenuClick(navCreateRoom) }
        )
        MenuItem(
            text = "Join a game",
            icon = Icons.Default.ArrowRightAlt,
            onClick = { onMenuClick(navJoinRoom) }
        )
        MenuItem(
            text = "Credits",
            icon = Icons.Default.Info,
            onClick = {
                onMenuClick(navCredits)
            }
        )

        AnimatedVisibility(visible = errorStatus.isNotEmpty()) {
            Text(
                text = errorStatus,
                color = colorScheme.error,
                fontSize = 25.sp
            )
        }
    }
}

@Preview
@Composable
fun NameTags(onNameSelect: (name: String) -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Text(text = "or choose a name")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(items = getNameTags()) {
                NameTag(name = it) {
                    onNameSelect(it)
                }
            }
        }
    }
}

@Composable
fun NameTag(name: String, onClick: () -> Unit) {
    SuggestionChip(
        onClick = onClick,
        label = { Text(text = name, fontSize = 20.sp) }
    )
}

fun getNameTags(): List<String> = listOf(
    "pico",
    "mini",
    "mike",
    "tiny",
    "mario",
    "big",
    "max",
)

@Composable
fun MenuItem(
    icon: ImageVector = Icons.Outlined.Info,
    text: String,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick, modifier = Modifier
            .background(
                shape = RoundedCornerShape(10.dp), color = colorScheme.primary
            )
            .height(60.dp)
            .width(250.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorScheme.onPrimary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = text,
            color = colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}
