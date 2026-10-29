package com.example.spanishflashcards.ui.utils

// Shown under the lesson body field on the create and edit screens
const val LESSON_MARKDOWN_HINT =
    "Markdown supported. For a table, use one | --- | row under the header:\n" +
        "| Word | Meaning |\n| --- | --- |\n| hola | hello |"

// Tidies a lesson body before it is rendered as Markdown, fixing two things that stop
// tables showing even when the table itself is written correctly:
// - Windows line endings (\r\n): the parser doesn't recognise a table at all with them.
// - A table directly under a line of text: Markdown needs a blank line before a table,
//   otherwise the table is treated as part of the paragraph above it.
fun prepareLessonMarkdown(body: String): String {
    val lines = body.replace("\r\n", "\n").replace('\r', '\n').split('\n')
    val result = mutableListOf<String>()

    for (line in lines) {
        val previous = result.lastOrNull()
        val startsTable = isTableLine(line) && previous != null &&
            previous.isNotBlank() && !isTableLine(previous)

        if (startsTable) {
            result.add("")
        }
        result.add(line)
    }

    return result.joinToString("\n")
}

private fun isTableLine(line: String) = line.trimStart().startsWith("|")
