package com.example.bankapp.entities.uientities.uidata

import com.example.bankapp.entities.uientities.uitypes.AlertButtonStyle

data class AlertButtonConfig(
    val label: String,
    val onClick: () -> Unit = {},
    val style: AlertButtonStyle = AlertButtonStyle.PRIMARY
)