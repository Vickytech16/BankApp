import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bankapp.R
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.textFieldFontSize


@Composable
fun AmountOutlinedTextField(
    amount: String,
    onAmountChange: (String) -> Unit,
    fieldName: String,
    amountError: FormError?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Text(
            text = fieldName,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        TextField(
            value = amount,
            onValueChange = onAmountChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.amount_textfield_height)),
            shape = RoundedCornerShape(dimensionResource(R.dimen.amount_input_corner_radius)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
                errorIndicatorColor = MaterialTheme.colorScheme.error,
                errorContainerColor = MaterialTheme.colorScheme.errorContainer
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = TextStyle(
                fontSize = textFieldFontSize,
                fontWeight = FontWeight.Medium
            ),
            isError = amountError != null,
            trailingIcon = {
                Text(
                    text = stringResource(R.string.rupee_symbol),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = AppSpacing.md)
                )
            },
            singleLine = true
        )

        if (amountError != null) {
            ErrorTextBuilder(amountError)
        }
    }
}