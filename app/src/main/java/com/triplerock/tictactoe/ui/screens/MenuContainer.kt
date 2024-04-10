package com.triplerock.tictactoe.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.ArrowCircleRight
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.triplerock.tictactoe.R
import com.triplerock.tictactoe.ui.navCreateRoom
import com.triplerock.tictactoe.ui.navCredits
import com.triplerock.tictactoe.ui.navJoinRoom
import com.triplerock.tictactoe.ui.screens.common.NameTags
import com.triplerock.tictactoe.ui.screens.common.TicSurface
import com.triplerock.tictactoe.ui.theme.TicTacToeTheme
import com.triplerock.tictactoe.ui.theme.Yellow10
import com.triplerock.tictactoe.ui.theme.coolveticaFamily
import com.triplerock.tictactoe.viewmodels.MenuViewModel
import org.koin.androidx.compose.koinViewModel

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewMenuDark() {
    TicSurface {
        Menu()
    }
}

@Preview
@Composable
fun PreviewMenuLight() {
    TicSurface {
        Menu()
    }
}

@Composable
fun MenuContainer(
    menuViewModel: MenuViewModel = koinViewModel(),
    navController: NavController,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var name by remember { mutableStateOf(menuViewModel.name) }
        val errorStatus by remember { mutableStateOf("") }
        Menu(
            name = name,
            errorStatus = errorStatus,
            onMenuClick = {
                menuViewModel.setName(name)
                navController.navigate(it)
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
    name: String = "mini",
    errorStatus: String = "",
    onMenuClick: (destination: String) -> Unit = {},
    onNameSelect: (name: String) -> Unit = {},
) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painterResource(id = R.drawable.ic_xoxo_round ),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Text(
            text = "TicTacToe",
            fontSize = 60.sp,
            color = Yellow10
        )

        NameBox(name)

        NameTags { onNameSelect(it) }

        Spacer(modifier = Modifier.height(20.dp))

        MenuItem(
            text = "Host Game",
            icon = Icons.Rounded.AddCircle,
            onClick = { onMenuClick(navCreateRoom) }
        )
        MenuItem(
            text = "Join Game",
            icon = Icons.Rounded.ArrowCircleRight,
            onClick = { onMenuClick(navJoinRoom) }
        )
        MenuItem(
            text = "Credits",
            icon = Icons.Default.Info,
            onClick = {
                onMenuClick(navCredits)
            },
            color = colorScheme.secondary
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

@Composable
fun NameBox(name: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = name,
            fontSize = 40.sp,
            color = colorScheme.primary
        )
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null
        )
    }
}

@Composable
fun MenuItem(
    icon: ImageVector = Icons.Outlined.Info,
    text: String,
    onClick: () -> Unit,
    color: Color = colorScheme.primary,
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .background(
                shape = RoundedCornerShape(10.dp),
                color = color
            )
            .height(60.dp)
            .width(250.dp),
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp)
        ) {
            val (iconRef, textRef) = createRefs()
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colorScheme.onPrimary,
                modifier = Modifier
                    .size(30.dp)
                    .constrainAs(iconRef) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            )
            Text(
                text = text,
                color = colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                fontSize = 25.sp,
                modifier = Modifier.constrainAs(textRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
            )
        }
    }
}
