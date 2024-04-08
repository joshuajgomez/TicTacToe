package com.triplerock.tictactoe.utils

import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import java.text.SimpleDateFormat
import java.util.Date

fun getPrettyTime(instance: Long): String {
    val dateFormat = SimpleDateFormat("h.mm a")
    val date = Date(instance)
    return dateFormat.format(date).toLowerCase(Locale.current)
}