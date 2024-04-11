package com.triplerock.tictactoe.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.triplerock.tictactoe.R
import com.triplerock.tictactoe.ui.navMenu
import com.triplerock.tictactoe.ui.screens.common.TicSurface
import com.triplerock.tictactoe.ui.screens.common.TitleBar
import com.triplerock.tictactoe.ui.theme.Blue10
import com.triplerock.tictactoe.ui.theme.textCredits

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewCreditsContainerDark() {
    TicSurface {
        CreditsContainer(navController = NavController(LocalContext.current))
    }
}

@Preview
@Composable
fun PreviewCreditsContainerLight() {
    TicSurface {
        CreditsContainer(navController = NavController(LocalContext.current))
    }
}

@Composable
fun CreditsContainer(navController: NavController) {
    Column(Modifier.fillMaxSize()) {
        TitleBar(
            title = textCredits,
            onBackClick = { navController.navigate(navMenu) }
        )
        Credits()
    }
}

const val emojiHeart = "❤\uFE0F"

@Composable
private fun Credits() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        LogoBelt()
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            fontSize = 20.sp,
            color = colorScheme.onSurface,
            text = buildAnnotatedString {
                append("Created with $emojiHeart by  ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("joshuajgomez")
                }
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        val localUriHandler = LocalUriHandler.current
        Text(
            text = "github.com/joshuajgomez",
            fontSize = 20.sp,
            color = Blue10,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable {
                localUriHandler.openUri("https://www.github.com/joshuajgomez")
            }
        )
    }
}

@Composable
fun LogoBelt() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
    ) {
        items(6) {
            Image(
                painter = painterResource(id = R.drawable.ic_tictactoe_blue_foreground),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
        }
    }
}
