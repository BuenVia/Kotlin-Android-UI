package com.example.spanishflashcards.ui.screens.subject

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spanishflashcards.data.model.subject.Subject
import com.example.spanishflashcards.data.model.language.CurrentLanguage
import com.example.spanishflashcards.data.remote.RetrofitInstance
import com.example.spanishflashcards.ui.theme.CustomColors
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.example.spanishflashcards.ui.theme.primaryButtonColors
import com.example.spanishflashcards.ui.theme.secondaryButtonColors
import com.example.spanishflashcards.ui.utils.AdminLink
import com.example.spanishflashcards.ui.utils.AppScreen
import com.example.spanishflashcards.ui.utils.EditModeToggle
import com.example.spanishflashcards.ui.utils.LoadError
import com.example.spanishflashcards.ui.utils.ScreenTitle
import com.example.spanishflashcards.ui.utils.prepareLessonMarkdown

// In edit mode the admin controls are small text links placed next to the thing they change,
// so the page reads the same as normal mode with a few extra links rather than a wall of buttons.
@Composable
fun SubjectViewFunction(
    subjectId: Long,
    isAdmin: Boolean,
    onEditModeChange: ((Boolean) -> Unit)?,
    onBack: () -> Unit,
    onRevise: () -> Unit,
    onPractice: () -> Unit,
    onCreateVocab: () -> Unit,
    onEditVocab: (vocabId: Long) -> Unit,
    onCreateLesson: () -> Unit,
    onEditLesson: (lessonId: Long) -> Unit,
    onEditSubject: () -> Unit
) {

    var subject by remember {
        mutableStateOf<Subject?>(null)
    }

    var loadFailed by remember {
        mutableStateOf(false)
    }

    // Bumped by the Retry button to run the load again
    var loadAttempt by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(subjectId, loadAttempt) {
        loadFailed = false
        try {
            subject = RetrofitInstance.api.getSubject(CurrentLanguage.pathSegment, subjectId)
        } catch (e: Exception) {
            e.printStackTrace()
            loadFailed = true
        }
    }

    val subjectLesson = subject?.lesson
    val vocabList = subject?.vocabs

    AppScreen(
        onBack = onBack,
        actions = onEditModeChange?.let { onChange ->
            { EditModeToggle(editMode = isAdmin, onEditModeChange = onChange) }
        }
    ) {

        if (loadFailed && subject == null) {
            LoadError(
                message = "Unable to load this subject. Please try again.",
                onRetry = { loadAttempt++ },
                modifier = Modifier.align(Alignment.Center)
            )
            return@AppScreen
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Subject heading
            ScreenTitle(text = subject?.subjectName ?: "Loading...")

            if (isAdmin && subject != null) {
                AdminLink(text = "Edit subject", onClick = onEditSubject)
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Lesson
            if (subjectLesson != null) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (subjectLesson.lessonTitle.isNullOrBlank()) {"EMPTY"} else {
                            subjectLesson.lessonTitle
                        },
                        modifier = Modifier.weight(1f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = CustomColors.textColor
                    )

                    if (isAdmin) {
                        AdminLink(text = "Edit", onClick = { onEditLesson(subjectLesson.id) })
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Markdown(
                    modifier = Modifier.fillMaxWidth(),
                    content = prepareLessonMarkdown(subjectLesson.body.toString()),
                    colors = markdownColor(
                        text = CustomColors.textColor
                    )
                )

            } else if (isAdmin && subject != null) {
                AdminLink(
                    text = "+ Add a lesson",
                    onClick = onCreateLesson,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Choose how you want to study",
                fontSize = 14.sp,
                color = CustomColors.secondaryTextColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Revise and Practice buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onRevise() },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = primaryButtonColors()
                ) {
                    Text(
                        text = "Revise",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = { onPractice() },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = secondaryButtonColors()
                ) {
                    Text(
                        text = "Practice",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Vocabulary section heading, with the add link alongside it in edit mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vocabulary",
                    modifier = Modifier.weight(1f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = CustomColors.textColor
                )

                if (isAdmin) {
                    AdminLink(text = "+ Add", onClick = onCreateVocab)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vocabulary items - no nested LazyColumn
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                vocabList.orEmpty().forEach { vocab ->
                    // Same row in both modes - edit mode only makes it tappable and adds an "Edit" hint
                    VocabRow(
                        text = vocab.term,
                        onClick = if (isAdmin) { { onEditVocab(vocab.id) } } else null
                    )
                }

                if (subject != null && vocabList.isNullOrEmpty()) {
                    Text(
                        text = "No vocabulary yet.",
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 14.sp,
                        color = CustomColors.secondaryTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// onClick is null in normal mode, where the row is display only
@Composable
private fun VocabRow(text: String, onClick: (() -> Unit)?) {
    val shape = RoundedCornerShape(12.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(CustomColors.surfaceColor, shape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = CustomColors.textColor
        )

        if (onClick != null) {
            Text(
                text = "Edit",
                fontSize = 13.sp,
                color = CustomColors.secondaryTextColor
            )
        }
    }
}
