package com.example.bankapp.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.ui.theme.screenPadding

@Composable
fun Modifier.screenModifier(contentPadding: PaddingValues, scrollState: ScrollState, ): Modifier {
    val deviceSpec = LocalDeviceSpec.current
    if(deviceSpec is DeviceSpec.MobilePortrait || deviceSpec is DeviceSpec.MobileLandscape){
        return this
            .safeContentPadding()
            .verticalScroll(scrollState)
            .padding(contentPadding)
            .padding(screenPadding)
            .fillMaxHeight()
    }
    else{
        return this
            .safeContentPadding()
            .padding(contentPadding)
            .padding(screenPadding)
            .fillMaxHeight()
            .verticalScroll(scrollState)
    }
}


@Composable
fun Modifier.homeScreenModifier(
    contentPadding: PaddingValues,
    scrollState: ScrollState
): Modifier {
    val deviceSpec = LocalDeviceSpec.current

    val scaffoldBottomPadding = contentPadding.calculateBottomPadding()

    return this
        .fillMaxHeight() // 1. Set the height constraint first
        .safeContentPadding() // 2. Handle system bars (status/nav) // 3. Offset for Bottom Bar
        .padding(screenPadding) // 4. Your 24dp horizontal padding
        .verticalScroll(scrollState) // 5. Apply scroll LAST so it knows the total height
}