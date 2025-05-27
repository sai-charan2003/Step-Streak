package com.charan.stepstreak.presentation.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.charan.stepstreak.data.model.DataProviders
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChangeDataProviderSheet(
    dataProvider: List<DataProviders>,
    onValueChange: (DataProviders) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    sheetState : SheetState
){


    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onDismiss()

        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            dataProvider.forEach { provider ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .selectable(
                            selected = provider.isConnected,
                            onClick = { onValueChange(provider) },
                            role = Role.RadioButton
                        )
                        .padding(16.dp)
                ) {
                    Image(
                        painter = rememberDrawablePainter(provider.icon),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = provider.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )

                    RadioButton(
                        selected = provider.isConnected,
                        onClick = null
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    shapes = ButtonDefaults.shapes(),

                    onClick = { onSave() }
                ) {
                    Text(text = "Set goal")
                }

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    shapes = ButtonDefaults.shapes(),
                    onClick = { onDismiss() }
                ) {
                    Text(text = "Cancel")
                }
            }
        }

    }

}