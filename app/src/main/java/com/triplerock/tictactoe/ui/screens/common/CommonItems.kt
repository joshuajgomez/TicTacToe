package com.triplerock.tictactoe.ui.screens.common

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.triplerock.tictactoe.data.sampleNames
import com.triplerock.tictactoe.data.sampleRoomNames
import com.triplerock.tictactoe.ui.theme.TicTacToeTheme

@Preview
@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.padding(all = 5.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            modifier = Modifier.size(30.dp)
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
        modifier = modifier
            .background(
                shape = RoundedCornerShape(30.dp),
                color = colorScheme.primary
            )
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = text,
            fontSize = 22.sp,
            color = colorScheme.onPrimary
        )
    }
}


@Preview
@Composable
fun PreviewLoading() {
    TicTacToeTheme {
        Loading()
    }
}

@Composable
fun Loading(message: String = "Loading") {
    Column(
        verticalArrangement = Arrangement.spacedBy(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
    ) {
        CircularProgressIndicator()
        Text(text = message, fontSize = 25.sp)
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
    title: String = "TicTacToe",
    onBackClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        CustomButton(
            onClick = onBackClick,
        )
        Spacer(modifier = Modifier.width(20.dp))
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


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewCustomTextFieldDark() {
    TicTacToeTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colorScheme.background
        ) {
            CustomTextField(Modifier.padding(all = 20.dp))
        }
    }
}

@Preview()
@Composable
fun PreviewCustomTextFieldLight() {
    TicTacToeTheme {
        Surface(color = colorScheme.background) {
            CustomTextField(modifier = Modifier.padding(all = 20.dp))
        }
    }
}

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    text: String = "Heyy",
    onTextChanged: (text: String) -> Unit = {},
) {
    BasicTextField(
        modifier = modifier
            .background(
                color = colorScheme.tertiary.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.extraLarge,
            )
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxWidth(),
        value = text,
        onValueChange = {
            if ("\n" !in it) onTextChanged(it)
        },
        cursorBrush = SolidColor(colorScheme.primary),
        textStyle = LocalTextStyle.current.copy(
            color = colorScheme.primary,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        ),
    )
}