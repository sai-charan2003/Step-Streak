package com.charan.stepstreak.presentation.settings.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SetStepGoalDialog(
    targetSteps: Long,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    sheetState : SheetState
) {

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onDismiss()

        }
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 25.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically

                ) {
                FilledTonalIconButton (
                    onClick = { onDecrement() },
                    enabled = targetSteps>1L,
                    shapes = IconButtonDefaults.shapes()

                    ) {
                    Icon(Icons.Default.Remove, contentDescription = "Remove")
                }
                BasicTextField(
                    value = "%,d".format(targetSteps),
                    onValueChange = {
                        val filteredInput = it.filter { char -> char.isDigit() }
                        onValueChange(filteredInput)

                    },
                    textStyle = MaterialTheme.typography.headlineLarge.copy(textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.width(IntrinsicSize.Min).padding(horizontal = 15.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface)

                )

                FilledTonalIconButton(
                    onClick = { onIncrement()},
                    shapes = IconButtonDefaults.shapes()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
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