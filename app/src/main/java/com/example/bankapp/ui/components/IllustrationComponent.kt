package com.example.bankapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.example.bankapp.R

@Composable
fun IllustrationComponent(drawableRes: Int){
    Image(
        painter = painterResource(id = drawableRes),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.illustration_height))
            .padding(bottom = dimensionResource(R.dimen.illustration_bottom_padding)),
        contentScale = ContentScale.Fit
    )
}