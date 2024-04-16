package com.triplerock.tictactoe.ui.screens.game

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
        TurnBox(currentPlayer = PlayerO)
    }
}

@Composable
fun TurnBox(
    modifier: Modifier = Modifier,
    room: Room = getRooms().random(),
    currentPlayer: String = PlayerX,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TurnItem(
            mark = Mark(
                icon = { IconX(iconSize = 30.dp, stroke = 10f) },
            ),
            playerName = room.player1Name,
            isTurn = room.nextTurn == PlayerX,
            isCurrentPlayer = currentPlayer == PlayerX
        )
        TurnItem(
            mark = Mark(
                icon = { IconO(iconSize = 30.dp, stroke = 10f) },
            ),
            playerName = room.player2Name,
            isTurn = room.nextTurn == PlayerO,
            isCurrentPlayer = currentPlayer == PlayerO
        )
    }
}

@Composable
fun TurnItem(
    mark: Mark = Mark(icon = { IconX() }),
    playerName: String = "yoo",
    isTurn: Boolean = true,
    isCurrentPlayer: Boolean = false,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.height(80.dp)
    ) {
        if (isCurrentPlayer) {
            Text(
                text = "You", fontSize = 20.sp,
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier
                .border(
                    2.dp,
                    colorScheme.onBackground,
                    RoundedCornerShape(30.dp)
                )
                .clip(RoundedCornerShape(30.dp))
                .background(if (isTurn) colorScheme.onBackground else colorScheme.background)
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .width(120.dp)
        ) {
            mark.icon()
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = playerName, fontSize = 25.sp,
                color = if (isTurn) colorScheme.onPrimary else colorScheme.onBackground,
            )
        }
    }
}
