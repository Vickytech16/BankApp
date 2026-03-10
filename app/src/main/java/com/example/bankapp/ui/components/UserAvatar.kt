package com.example.bankapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.bankapp.R
import com.example.bankapp.ui.theme.userAvatarFontSize

import kotlin.math.absoluteValue

@Composable
fun UserAvatar(
    name: String,
    pfpUrl: String? = null,
    size: Dp = dimensionResource(R.dimen.drawer_user_avatar_size),
    clickAction: (()->Unit)? = null
) {
    val firstLetter = name.firstOrNull()?.uppercase() ?: "?"

    var modifier = Modifier.size(size).clip(CircleShape).background(avatarColor(name))

    val userAvatarFontSize = (size.value * 0.4f).sp

    if (clickAction != null) {
        modifier = modifier.clickable { clickAction() }
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
        ) {
        if (pfpUrl != null) {
            Text(
                text = firstLetter,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        } else {
            Text(
                text = firstLetter,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = userAvatarFontSize
            )
        }
    }
}

private fun avatarColor(name: String): Color {
    val colors = listOf(
        Color(0xFFE57373),
        Color(0xFF64B5F6),
        Color(0xFF81C784),
        Color(0xFFFFB74D),
        Color(0xFFBA68C8)
    )

    return colors[name.hashCode().absoluteValue % colors.size]
}