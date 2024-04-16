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
        Box(modifier = Modifier.padding(20.dp)) {
            Stats()
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewStatsDark() {
    TicSurface {
        Box(modifier = Modifier.padding(20.dp)) {
            Stats()
        }
    }
}

@Composable
fun Stats(
    modifier: Modifier = Modifier,
    room: Room = getRooms().first()
) {
    Box(
        modifier
            .padding(horizontal = 20.dp)
            .solidShadow(radius = 30f, offset = 5.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(colorScheme.background)
                .border(
                    width = 1.dp,
                    color = colorScheme.onBackground,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(vertical = 15.dp)
        ) {
            Stat("${room.history.oWins} wins", Icons.Outlined.Circle)
            Stat(
                "${room.history.xWins} wins", Icons.Outlined.Cancel,
                iconTint = colorScheme.error
            )
            Stat("${room.history.draws} draws", Icons.Outlined.Balance)
        }
    }
}

@Composable
fun Stat(
    label: String,
    icon: ImageVector = Icons.Default.Close,
    iconTint: Color = colorScheme.onBackground,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            tint = iconTint
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = label, fontSize = 18.sp,
            color = colorScheme.onBackground
        )
    }
}