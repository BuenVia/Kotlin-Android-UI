package com.example.spanishflashcards.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.data.model.subject.SubjectNames
import com.example.spanishflashcards.ui.theme.ButtonShape
import com.example.spanishflashcards.ui.theme.CardShape
import com.example.spanishflashcards.ui.theme.CustomColors

// One subject on Home: name and category on the left, CEFR level badge on the right
@Composable
fun SubjectCard(
    subject: SubjectNames,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = CustomColors.surfaceColor,
            contentColor = CustomColors.textColor
        ),
        // Outlined rather than shadowed - shadows barely show on a dark background
        border = BorderStroke(1.dp, CustomColors.borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = subject.subjectName?.takeIf { it.isNotBlank() } ?: "Unnamed subject",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = subject.category?.categoryName?.takeIf { it.isNotBlank() } ?: "No category",
                    fontSize = 14.sp,
                    color = CustomColors.secondaryTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            subject.level?.let { level ->
                Spacer(modifier = Modifier.width(12.dp))
                LevelBadge(level)
            }
        }
    }
}

@Composable
private fun LevelBadge(level: String) {
    Box(
        modifier = Modifier
            .background(CustomColors.accentColor, ButtonShape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = level,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = CustomColors.onAccentColor
        )
    }
}
