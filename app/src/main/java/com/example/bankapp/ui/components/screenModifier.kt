package com.example.bankapp.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.Modifier
import com.example.bankapp.ui.theme.screenPadding

fun Modifier.screenModifier(windowSizeClass: WindowSizeClass, contentPadding: PaddingValues, scrollState: ScrollState, ): Modifier {
    if(windowSizeClass.widthSizeClass == WindowWidthSizeClass.Companion.Compact || windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact){
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