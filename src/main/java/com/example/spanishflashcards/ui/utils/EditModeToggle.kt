package com.example.spanishflashcards.ui.utils

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.ui.theme.CustomColors

// Small switch for the top bar that turns the admin editing controls on and off.
// Pass it to AppScreen's actions so it sits in the same place on every screen that uses it.
@Composable
fun EditModeToggle(
    editMode: Boolean,
    onEditModeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Edit mode",
            fontSize = 13.sp,
            color = CustomColors.secondaryTextColor
        )

        Spacer(modifier = Modifier.width(4.dp))

        Switch(
            checked = editMode,
            onCheckedChange = onEditModeChange,
            // Default switch is large next to a 13sp label
            modifier = Modifier.scale(0.8f),
            colors = SwitchDefaults.colors(
                checkedThumbColor = CustomColors.onPrimaryColor,
                checkedTrackColor = CustomColors.primaryColor,
                uncheckedThumbColor = CustomColors.secondaryTextColor,
                uncheckedTrackColor = CustomColors.disabledContainerColor,
                uncheckedBorderColor = CustomColors.disabledContainerColor
            )
        )
    }
}
