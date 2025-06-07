package com.charan.stepstreak.presentation.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun<T> CustomDropDown(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    isExpanded : Boolean = false,
    onDismiss : () -> Unit = { }
) {
    MaterialExpressiveTheme(shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))){
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = {
                onDismiss()
            }

        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item.toString()) },
                    onClick = {
                        onItemSelected(item)
                        onDismiss()
                    },
                    modifier = if (item == selectedItem) {
                        Modifier.background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.3f))
                    } else {
                        Modifier
                    }
                )
            }
        }

    }

}