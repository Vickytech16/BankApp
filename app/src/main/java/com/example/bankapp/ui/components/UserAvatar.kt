package com.example.bankapp.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bankapp.R
import java.io.File

import kotlin.math.absoluteValue

@Composable
fun UserAvatar(
    name: String,
    pfpUrl: String? = null,
    size: Dp = dimensionResource(R.dimen.drawer_user_avatar_size),
    editable: Boolean = false,
    clickAction: (()->Unit)? = null,
    editAction: (()->Unit)? = null,
    imageUpdateKey: Int = 0
) {
    val firstLetter = name.firstOrNull()?.uppercase() ?: "?"
    val userAvatarFontSize = (size.value * 0.4f).sp

    val bitmap = remember(pfpUrl, imageUpdateKey) {
        if (!pfpUrl.isNullOrEmpty() && File(pfpUrl).exists()) {
            BitmapFactory.decodeFile(pfpUrl)
        } else {
            null
        }
    }

    Box(
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(avatarColor(name))
                .then(
                    if (clickAction != null) Modifier.clickable { clickAction() } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = name,
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            else {
                Text(
                    text = firstLetter,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = userAvatarFontSize
                )
            }
        }

        if (editable && editAction != null) {
            Box(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.user_avatar_edit_background))
                    .clip(CircleShape)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .clickable { editAction() }
                    .offset(
                        x = dimensionResource(R.dimen.user_avatar_edit_offset),
                        y = dimensionResource(R.dimen.user_avatar_edit_offset)
                    ),
                contentAlignment = Alignment.Center

            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.user_avatar_edit_content_description),
                    modifier = Modifier.size(dimensionResource(R.dimen.user_avatar_edit_icon_size)),
                    tint = Color.White
                )
            }
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