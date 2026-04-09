package com.example.bankapp.ui.components.filters

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import com.example.bankapp.R
import com.example.bankapp.ui.components.BankAppBottomSheet
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.ui.theme.LocalDeviceSpec


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSortChip(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
    showSheet: Boolean,
    onShowSheetChange: (Boolean) -> Unit,
    sheetContent: @Composable () -> Unit,
    fullHeight: Boolean
) {
    val deviceSpec = LocalDeviceSpec.current
    val shouldFullHeight =
    if (fullHeight && deviceSpec is DeviceSpec.TabPortrait){
        false
    }else
        fullHeight

    BadgedBox(
        badge = {
            if (badgeCount > 0) {
                Badge {
                    Text(badgeCount.toString())
                }
            }
        },
        modifier = modifier
    ) {
        AssistChip(
            onClick = {
                onShowSheetChange(true)
            },
            label = {
                Text(
                    label,
                    style = MaterialTheme.typography.labelLarge
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(dimensionResource(R.dimen.filter_icon_size))
                )
            },
            modifier = Modifier.size(
                width = dimensionResource(R.dimen.filter_button_width),
                height = dimensionResource(R.dimen.filter_button_height)
            )
        )
    }

    BankAppBottomSheet(
        showSheet = showSheet,
        onDismissRequest = { onShowSheetChange(false) },
        skipPartiallyExpanded = true,
        fullHeight = shouldFullHeight
    ) {
        sheetContent()
    }
}