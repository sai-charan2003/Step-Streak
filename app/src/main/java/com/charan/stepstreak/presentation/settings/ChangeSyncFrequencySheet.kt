package com.charan.stepstreak.presentation.settings

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
import com.charan.stepstreak.data.model.SyncTime
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChangeSyncFrequencySheet(
    currentFrequency : Long,
    onValueChange: (Long) -> Unit,
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
            SyncTime.entries.forEach {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .selectable(
                            selected = currentFrequency == it.minutes,
                            onClick = { onValueChange(it.minutes) },
                            role = Role.RadioButton
                        )
                        .padding(16.dp)
                ) {

                    RadioButton(
                        selected = currentFrequency == it.minutes,
                        onClick = null
                    )
                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = it.getName().toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
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
                    Text(text = "Set Frequency")
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