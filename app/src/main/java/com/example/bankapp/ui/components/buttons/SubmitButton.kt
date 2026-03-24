package com.example.bankapp.ui.components.buttons

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.bankapp.R

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun SubmitButton(
    onClick: ()->Unit,
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.submit_button),
    enabled: Boolean = true,
    isLoading: Boolean = false,

){
    Button(
            onClick = { onClick() },
            modifier = modifier
                       .widthIn(max = dimensionResource(R.dimen.submit_button_max_width))
                       .fillMaxWidth(),
            enabled = enabled && !isLoading
    ) {
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
        ) {
            if (!isLoading)
                Text(text)
            else {
                CircularProgressIndicator(
                    modifier = Modifier.size(dimensionResource(R.dimen.button_circular_progress_indicator_size)),
                    strokeWidth = dimensionResource(R.dimen.button_circular_progress_indicator_stroke),
                    color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
