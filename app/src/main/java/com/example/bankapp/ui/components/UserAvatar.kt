package com.example.bankapp.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.window.Dialog
import com.example.bankapp.R
import com.example.bankapp.ui.theme.AppSpacing
import java.io.File
import kotlin.math.absoluteValue

@Composable
fun UserAvatar(
    name: String,
    pfpUrl: String? = null,
    size: Dp = dimensionResource(R.dimen.drawer_user_avatar_size),
    editable: Boolean = false,
    showEnlargeOnClick: Boolean = false,
    isEnlarged: Boolean = false,
    onEnlargeToggle: (Boolean) -> Unit = {},
    clickAction: (() -> Unit)? = null,
    editAction: (() -> Unit)? = null,
    imageUpdateKey: Int = 0
) {
    val firstLetter = name.firstOrNull()?.uppercase() ?: "?"
    val userAvatarFontSize = (size.value * 0.4f).sp
    val isResource = pfpUrl?.startsWith("res://") == true

    val bitmap = remember(pfpUrl, imageUpdateKey) {
        if (!isResource && !pfpUrl.isNullOrEmpty() && File(pfpUrl).exists()) {
            BitmapFactory.decodeFile(pfpUrl)
        } else {
            null
        }
    }

    // Determine if the component should be clickable at all
    val isClickable = clickAction != null || showEnlargeOnClick

    Box(contentAlignment = Alignment.BottomEnd) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(avatarColor(name))
                .then(
                    if (isClickable) {
                        Modifier.clickable {
                            if (showEnlargeOnClick) onEnlargeToggle(true)
                            clickAction?.invoke()
                        }
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                isResource -> {
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.bank_logo),
                        contentDescription = name,
                        modifier = Modifier.size(size),
                        contentScale = ContentScale.Fit
                    )
                }
                bitmap != null -> {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = name,
                        modifier = Modifier.size(size).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    Text(
                        text = firstLetter,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = userAvatarFontSize
                    )
                }
            }
        }

        if (editable && editAction != null) {
            Box(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.user_avatar_edit_background))
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
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

    if (showEnlargeOnClick && isEnlarged) {
        Dialog(onDismissRequest = { onEnlargeToggle(false) }) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(AppSpacing.md)
            ) {
                UserAvatar(
                    name = name,
                    pfpUrl = pfpUrl,
                    size = 300.dp,
                    editable = false,
                    showEnlargeOnClick = false,
                    imageUpdateKey = imageUpdateKey
                )
                IconButton(
                    onClick = { onEnlargeToggle(false) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

private fun avatarColor(name: String): Color {
    val colors = listOf(
        Color(0xFFE57373), Color(0xFF64B5F6), Color(0xFF81C784),
        Color(0xFFFFB74D), Color(0xFFBA68C8)
    )
    return colors[name.hashCode().absoluteValue % colors.size]
}