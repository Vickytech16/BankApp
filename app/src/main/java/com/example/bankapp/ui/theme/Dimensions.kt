package com.example.bankapp.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object AppSpacing {
    val oneDp = 1.dp
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
}

val textFieldFontSize = 18.sp
val AppPadding = Modifier.safeContentPadding()
val screenPadding = PaddingValues(horizontal = 24.dp)

fun Modifier.submitButtonModifier(deviceSpec: DeviceSpec): Modifier{
    if(deviceSpec is DeviceSpec.TabPortrait || deviceSpec is DeviceSpec.TabLandscape){
       return this.widthIn(max = 400.dp).fillMaxWidth()
    }
    else{
        return this.fillMaxWidth()
    }
}