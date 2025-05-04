package com.charan.stepstreak.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomeTopBar(
    greeting : String,
    motivationText : String
) {

    Column {
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineLarge.copy(
                color = MaterialTheme.colorScheme.tertiary
            )
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = motivationText
        )
    }
}




@Preview
@Composable
fun HomeTopBarPreview() {
    HomeTopBar(greeting = "Good Morning", motivationText = "Come on you can do this")

}