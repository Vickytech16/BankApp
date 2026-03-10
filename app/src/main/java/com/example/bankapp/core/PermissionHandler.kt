package com.example.bankapp.core

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresApi
import androidx.compose.ui.res.stringResource
import com.example.bankapp.R
import com.example.bankapp.ui.screens.authscreens.PermissionDialog


@Composable
fun notificationPermissionHandler(
    onPermissionGranted:  () -> Unit,
    onDenied: () -> Unit
): () -> Unit {

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission())
    {
        granted ->
        if (granted) {
            onPermissionGranted() }
        else {
            onDenied() }
    }
    return {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onPermissionGranted()
        } else {
            val isGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (isGranted) {
                onPermissionGranted() }
            else {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowDialogOnPermissionDenial(context: Context, action: (Boolean)-> Unit, backBehaviour: ()-> Unit)
{
    PermissionDialog(
        stringResource(R.string.enable_notifications_title),
        stringResource(R.string.enable_notifications_text),
        {
            val intent =
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
            context.startActivity(intent)
            action(false)
        },
        {
            action(false)
        }
    )
}