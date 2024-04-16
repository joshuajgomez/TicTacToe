package com.triplerock.tictactoe.ui.screens.game

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.triplerock.tictactoe.data.Room
import com.triplerock.tictactoe.ui.screens.getRooms

@Preview
@Composable
fun Stats(
    modifier: Modifier = Modifier,
    room: Room = getRooms().first()
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
            .border(
                width = 1.dp,
                color = colorScheme.onBackground,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(vertical = 10.dp)
    ) {
        Stat("${room.history.oWins} wins", Icons.Outlined.Circle)
        Stat("${room.history.xWins} wins", Icons.Outlined.Cancel)
        Stat("${room.history.draws} draws", Icons.Outlined.Balance)
    }
}

@Composable
fun Stat(label: String, icon: ImageVector = Icons.Default.Close) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            tint = colorScheme.onBackground
        )
        Text(
            text = label, fontSize = 18.sp,
            color = colorScheme.onBackground
        )
    }
}