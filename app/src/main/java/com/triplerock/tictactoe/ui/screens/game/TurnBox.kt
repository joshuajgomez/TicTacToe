package com.triplerock.tictactoe.ui.screens.game

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.triplerock.tictactoe.data.PlayerO
import com.triplerock.tictactoe.data.PlayerX
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.ui.screens.common.TicSurface
import com.triplerock.tictactoe.ui.screens.getRooms

@Preview
@Composable
fun PreviewTurnBox() {
    TicSurface {
        TurnBox()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTurnBoxDark() {
    TicSurface {
        TurnBox()
    }
}

@Composable
fun TurnBox(
    modifier: Modifier = Modifier,
    room: Room = getRooms().random(),
    currentPlayer: String = PlayerX,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TurnItem(
            mark = Mark(
                icon = Icons.Outlined.Cancel,
                iconTint = colorScheme.error,
            ),
            playerName = room.player1Name,
            isTurn = room.nextTurn == PlayerX,
            isCurrentPlayer = currentPlayer == PlayerX
        )
        TurnItem(
            mark = Mark(
                icon = Icons.Outlined.Circle,
                iconTint = colorScheme.onBackground,
            ),
            playerName = room.player2Name,
            isTurn = room.nextTurn == PlayerO,
            isCurrentPlayer = currentPlayer == PlayerO
        )
    }
}

@Composable
fun TurnItem(
    mark: Mark,
    playerName: String,
    isTurn: Boolean,
    isCurrentPlayer: Boolean = false,
) {
    val modifier = Modifier
        .padding(vertical = 10.dp)
        .width(130.dp)
        .fillMaxHeight()
        .clip(shape = RoundedCornerShape(20.dp))
        .border(
            width = if (isTurn) 3.dp else 1.dp,
            color = if (isTurn) colorScheme.primary else colorScheme.onBackground,
            shape = RoundedCornerShape(20.dp)
        )
        .padding(vertical = 0.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(top = 10.dp),
    ) {
        Icon(
            imageVector = mark.icon,
            contentDescription = null,
            tint = mark.iconTint,
            modifier = mark.modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = playerName, fontSize = 25.sp,
            color = if (isTurn) colorScheme.primary else
                colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(15.dp))
        if (isCurrentPlayer) {
            Text(
                text = "you",
                textAlign = TextAlign.Center,
                color = colorScheme.onPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(color = colorScheme.primary),
            )
        }
    }
}