import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bankapp.R
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.screenModifier

@Composable
fun RecoveryKeyDisplayScreen(
    recoveryKey: String,
    onDone: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    var hasCopied by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold() {
        contentPadding ->
        Column(
            modifier = Modifier.screenModifier(contentPadding, scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.your_recovery_key),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            LargeSpacer()

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = recoveryKey,
                    modifier = Modifier.padding(vertical = 32.dp),
                    style = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LargeSpacer()

            OutlinedButton(
                onClick = {
                    val clipData = ClipData.newPlainText("Recovery Key", recoveryKey)
                    val clipEntry = ClipEntry(clipData)
                    clipboard.setClip(clipEntry)

                    hasCopied = true
                }
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = stringResource(R.string.copy_content_description))
                MediumSpacer()
                Text(if (hasCopied) "Copied!" else "Copy to Clipboard")
            }

            XLSpacer()

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    LargeSpacer()
                    Text(
                        text = stringResource(R.string.copy_recovery_text_warning),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            XLSpacer()
            XLSpacer()

            Button(
                onClick = onDone,
                enabled = hasCopied,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.saved_key_button))
            }
        }
    }
}