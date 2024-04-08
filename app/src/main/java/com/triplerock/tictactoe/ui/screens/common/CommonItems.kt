package com.triplerock.tictactoe.ui.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.triplerock.tictactoe.ui.theme.TicTacToeTheme
import com.triplerock.tictactoe.viewmodels.sampleNames
import com.triplerock.tictactoe.viewmodels.sampleRoomNames

@Preview
@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null
        )
    }
}

@Preview
@Composable
fun CustomTextButton(
    modifier: Modifier = Modifier,
    text: String = "Click me",
    onClick: () -> Unit = {},
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.background(
            shape = RoundedCornerShape(30.dp),
            color = colorScheme.primary
        )
    ) {
        Text(
            text = text,
            color = colorScheme.onPrimary
        )
    }
}

@Preview
@Composable
fun Loading(message: String = "Loading") {
    TicTacToeTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Text(text = message)
        }
    }
}

@Preview
@Composable
fun PreviewTitleBar() {
    TicTacToeTheme {
        TitleBar()
    }
}

@Composable
fun TitleBar(
    title: String = "TicTacToe title",
    onBackClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomButton(
            onClick = onBackClick
        )
        Text(
            text = title,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary,
        )
    }
}

@Preview
@Composable
fun PreviewNameTags(onNameSelect: (name: String) -> Unit = {}) {
    TicTacToeTheme {
        NameTags()
    }
}

@Preview
@Composable
fun PreviewRoomNameTags(onNameSelect: (name: String) -> Unit = {}) {
    TicTacToeTheme {
        RoomNameTags()
    }
}

@Composable
fun NameTags(
    names: List<String> = sampleNames,
    onNameSelect: (name: String) -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Text(text = "choose another name")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(items = names) {
                NameTag(name = it) {
                    onNameSelect(it)
                }
            }
        }
    }
}

@Composable
fun RoomNameTags(
    names: List<String> = sampleRoomNames,
    onNameSelect: (name: String) -> Unit = {},
) {
    LazyColumn {
        items(items = names) {
            NameTag(name = it) {
                onNameSelect(it)
            }
        }
    }
}

@Composable
private fun NameTag(name: String, onClick: () -> Unit) {
    SuggestionChip(
        onClick = onClick,
        label = { Text(text = name, fontSize = 20.sp) }
    )
}