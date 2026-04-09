package com.example.bankapp.ui.components.profileitems

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.bankapp.R
import com.example.bankapp.entities.uientities.uidata.UserNameFieldStrategy
import com.example.bankapp.temp.SimpleCropPreview
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.SmallSpacer
import com.example.bankapp.ui.components.UserAvatar
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.utilities.USERNAME_MAX_SIZE
import com.example.bankapp.viewmodels.ProfileViewModel

@Composable
fun UserPfpAndNameEditable(
    viewModel: ProfileViewModel,
    deviceSpec: DeviceSpec
) {
    val draftName by viewModel.userNameDraft.collectAsState()
    val user by viewModel.editableUser.collectAsState()
    val userNameError by viewModel.userNameError.collectAsState()

    var showImageOptions by remember { mutableStateOf(false) }
    var bitmapToCrop by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            bitmapToCrop = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(horizontal = AppSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            XLSpacer()
            Text(text = stringResource(R.string.edit_profile_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            XLSpacer()
        }

        item {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(dimensionResource(deviceSpec.profileAvatarSize))
                        .clip(CircleShape)
                        .clickable(enabled = !viewModel.isProcessingImage) { showImageOptions = true }
                ) {
                    UserAvatar(
                        name = user.userName,
                        pfpUrl = user.pfpURL,
                        size = dimensionResource(deviceSpec.profileAvatarSize),
                        editable = false,
                        imageUpdateKey = viewModel.imageUpdateTrigger
                    )
                }
                if (viewModel.isProcessingImage) {
                    CircularProgressIndicator(modifier = Modifier.size(AppSpacing.xl), color = MaterialTheme.colorScheme.primary, strokeWidth = 2.dp)
                }
            }
            SmallSpacer()
            TextButton(onClick = { showImageOptions = true }, enabled = !viewModel.isProcessingImage) {
                Text(text = stringResource(R.string.change_photo_label), style = MaterialTheme.typography.labelLarge)
            }
            LargeSpacer()
        }

        item {
            UnifiedOutlinedTextField(
                value = draftName,
                onValueChange = viewModel::onUserNameChange,
                labelText = stringResource(R.string.username_field_name),
                isError = userNameError != null,
                supportingText = { userNameError?.let { ErrorTextBuilder(it) }
                    ?: Text("${draftName.length}/${ProfileViewModel.USERNAME_MAX_SIZE}") },
                strategy = UserNameFieldStrategy,
                showTickCondition = { viewModel.isUserNameValid() },
                maxCharLimit = USERNAME_MAX_SIZE,
                showCharCount = true
            )

            XLSpacer()
        }

        item {
            Button(
                onClick = { viewModel.updateUserName() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isProcessingImage && userNameError == null
            ) {
                Text(stringResource(R.string.save_changes_button))
            }
            XLSpacer()
        }
    }

    if (showImageOptions) {
        AlertDialog(
            onDismissRequest = { showImageOptions = false },
            title = { Text(text = stringResource(R.string.profile_picture_options), style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ImageOptionRow(Icons.Default.PhotoLibrary, stringResource(R.string.upload_new_photo), MaterialTheme.colorScheme.primary) {
                        imagePickerLauncher.launch("image/*")
                        showImageOptions = false
                    }
                    if (user.pfpURL != null) {
                        ImageOptionRow(Icons.Default.Delete, stringResource(R.string.delete_photo), MaterialTheme.colorScheme.error) {
                            viewModel.deleteProfileImage()
                            showImageOptions = false
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    bitmapToCrop?.let { bitmap ->
        Dialog(onDismissRequest = { bitmapToCrop = null }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            SimpleCropPreview(
                bitmap = bitmap,
                onCropConfirmed = { cropped ->
                    viewModel.updateProfileImage(cropped, context)
                    bitmapToCrop = null
                },
                onCancel = { bitmapToCrop = null }
            )
        }
    }
}

@Composable
private fun ImageOptionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = AppSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        MediumSpacer()
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = color)
    }
}