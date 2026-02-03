package de.nif.utils.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionDialog(
    modifier: Modifier = Modifier,
    rationaleText: String,
    isDeniedPermanently: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
) {


    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        ) {
        Card() {
            Column(modifier = Modifier.padding(8.dp)) {

                val title = if (isDeniedPermanently) {
                    "Permission denied permanently!"
                } else {
                    "Permission needed!"
                }

                val text = if (isDeniedPermanently) {
                    "You can always grant or decline permissions in the app settings!"
                } else {
                    rationaleText
                }

                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = text, style = MaterialTheme.typography.bodyMedium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    TextButton(onClick = onDismiss) {
                        Text(text = "Dismiss", style = MaterialTheme.typography.bodySmall)
                    }

                    if (isDeniedPermanently) {

                        TextButton(onClick = onGoToAppSettingsClick) {
                            Text(text = "App Settings", style = MaterialTheme.typography.bodySmall)
                        }

                    } else {
                        TextButton(onClick = onOkClick) {
                            Text(text = "OK", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }

}