package com.triplerock.tictactoe.ui.screens.game

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.ui.screens.common.TicSurface
import com.triplerock.tictactoe.ui.screens.common.solidShadow
import com.triplerock.tictactoe.ui.screens.getRooms

@Preview
@Composable
fun PreviewStats() {
    TicSurface {
        Box {
            Stats()
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewStatsDark() {
    TicSurface {
        Stats()
    }
}

@Composable
fun Stats(
    modifier: Modifier = Modifier,
    room: Room = getRooms().first(),
) {
    Box(
        modifier
            .padding(horizontal = 10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(colorScheme.background)
        ) {
            Stat("${room.history.oWins} wins") { IconO(iconSize = 40.dp) }
            Stat("${room.history.draws} draws") {
                Icon(
                    Icons.Outlined.Balance, null,
                    modifier = Modifier.size(40.dp)
                )
            }
            Stat("${room.history.xWins} wins") { IconX(iconSize = 40.dp) }
        }
    }
}

@Composable
fun Stat(
    label: String = "3 Wins",
    icon: @Composable () -> Unit = {},
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        icon()
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = label, fontSize = 18.sp,
            color = colorScheme.onBackground
        )
    }
}