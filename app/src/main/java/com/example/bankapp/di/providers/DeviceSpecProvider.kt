package com.example.bankapp.di.providers

import android.content.res.Configuration
import com.example.bankapp.ui.theme.DeviceSpec

object DeviceSpecProvider {

    fun getCurrentDeviceSpec(configuration: Configuration): DeviceSpec {

        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val isTablet = configuration.smallestScreenWidthDp >= 500

        return when {
            isTablet && isLandscape -> DeviceSpec.TabLandscape()
            isTablet && !isLandscape -> DeviceSpec.TabPortrait()
            !isTablet && isLandscape -> DeviceSpec.MobileLandscape()
            else -> DeviceSpec.MobilePortrait()
        }
    }
}