package com.triplerock.tictactoe.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowCircleRight
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.House
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.OnlinePrediction
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.triplerock.tictactoe.data.PlayerX
import com.triplerock.tictactoe.ui.navHostGame
import com.triplerock.tictactoe.ui.navCredits
import com.triplerock.tictactoe.ui.navGame
import com.triplerock.tictactoe.ui.navJoinGame
import com.triplerock.tictactoe.ui.screens.common.XoMarqueeContainer
import com.triplerock.tictactoe.ui.screens.common.NameTags
import com.triplerock.tictactoe.ui.screens.common.TicSurface
import com.triplerock.tictactoe.ui.screens.common.solidShadow
import com.triplerock.tictactoe.ui.screens.common.solidShadow2
import com.triplerock.tictactoe.ui.theme.textAppTitle
import com.triplerock.tictactoe.ui.theme.textCredits
import com.triplerock.tictactoe.ui.theme.textHostGame
import com.triplerock.tictactoe.ui.theme.textJoinGame
import com.triplerock.tictactoe.viewmodels.MenuViewModel
import com.triplerock.tictactoe.viewmodels.modeMultiLocal
import com.triplerock.tictactoe.viewmodels.modeSingle
import com.triplerock.tictactoe.viewmodels.navKeyPlayer
import com.triplerock.tictactoe.viewmodels.navKeyRoomId
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
        Box(contentAlignment = Alignment.Center) {
            Column {
                XoMarqueeContainer()
                XoMarqueeContainer()
            }
            TitleLogo()
        }
        Spacer(modifier = Modifier.height(10.dp))

        NameBox(name)

        NameTags { onNameSelect(it) }

        Spacer(modifier = Modifier.height(20.dp))

        MenuBox {
            onMenuClick(it)
        }

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
fun MenuBox(
    onMenuClick: (destination: String) -> Unit = {},
) {
    val navController = rememberNavController()

    val navMenuHome = "menu_home"
    val navMenuMulti = "menu_multi"
    val navMenuMultiOnline = "menu_multi_online"

    NavHost(navController = navController, startDestination = navMenuHome) {
        val verticalSpace = Arrangement.spacedBy(20.dp)
        composable(navMenuHome) {
            Column(verticalArrangement = verticalSpace) {
                MenuItem(
                    text = "Single Player",
                    icon = Icons.Rounded.Person,
                    onClick = { onMenuClick("$navGame/$modeSingle/$PlayerX/..") }
                )
                MenuItem(
                    text = "Multiplayer",
                    icon = Icons.Rounded.Group,
                    onClick = { navController.navigate(navMenuMulti) }
                )
                MenuItem(
                    text = textCredits,
                    icon = Icons.Default.Info,
                    onClick = {
                        onMenuClick(navCredits)
                    },
                    invertColor = true
                )
            }
        }
        composable(navMenuMulti) {
            Column(verticalArrangement = verticalSpace) {
                MenuItem(
                    text = "Online",
                    icon = Icons.Rounded.Language,
                    onClick = { navController.navigate(navMenuMultiOnline) }
                )
                MenuItem(
                    text = "Local",
                    icon = Icons.Rounded.House,
                    onClick = { onMenuClick("$navGame/$modeMultiLocal/$PlayerX/..") }
                )
                MenuItem(
                    text = "Go Back",
                    icon = Icons.Rounded.ArrowBack,
                    onClick = { navController.navigate(navMenuHome) },
                    invertColor = true
                )
            }
        }
        composable(navMenuMultiOnline) {
            Column(verticalArrangement = verticalSpace) {
                MenuItem(
                    text = textHostGame,
                    icon = Icons.Rounded.AddCircle,
                    onClick = { onMenuClick(navHostGame) }
                )
                MenuItem(
                    text = textJoinGame,
                    icon = Icons.Rounded.ArrowCircleRight,
                    onClick = { onMenuClick(navJoinGame) }
                )
                MenuItem(
                    text = "Go Back",
                    icon = Icons.Rounded.ArrowBack,
                    onClick = { navController.navigate(navMenuHome) },
                    invertColor = true
                )
            }
        }
    }
}

@Composable
fun TitleLogo() {
    Surface(
        modifier = Modifier
            .solidShadow2(offset = 5.dp, radius = 0f, strokeWidth = 5f)
            .background(color = colorScheme.onBackground)
            .padding(vertical = 5.dp, horizontal = 10.dp),
        color = colorScheme.onBackground
    ) {
        Text(
            text = textAppTitle,
            fontSize = 50.sp,
            color = colorScheme.background
        )
    }
}

@Composable
fun NameBox(name: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .border(
                width = 2.dp, color = colorScheme.onBackground,
                shape = RoundedCornerShape(30.dp)
            )
            .solidShadow(offset = 5.dp)
            .background(
                color = colorScheme.surface,
                shape = RoundedCornerShape(30.dp)
            )
            .padding(horizontal = 30.dp)
    ) {
        Text(
            text = name,
            fontSize = 40.sp,
            color = colorScheme.onSurface
        )
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null,
            tint = colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun MenuItem(
    icon: ImageVector = Icons.Outlined.Info,
    text: String,
    onClick: () -> Unit,
    invertColor: Boolean = false,
) {
    val color: Color =
        if (invertColor) colorScheme.onBackground else colorScheme.background
    val backgroundColor: Color =
        if (invertColor) colorScheme.background else colorScheme.onBackground
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .solidShadow(offset = 3.dp, radius = 20f)
            .background(color = backgroundColor, shape = RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .border(
                width = 2.dp, color = color,
                shape = RoundedCornerShape(10.dp)
            ),
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp)
                .background(backgroundColor)
        ) {
            val (iconRef, textRef) = createRefs()
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
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
                color = color,
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
