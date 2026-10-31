package com.example.spanishflashcards

import com.example.spanishflashcards.ui.utils.prepareLessonMarkdown
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// Parses with the same GFM parser the Markdown renderer uses, to check tables are recognised
class LessonMarkdownTest {

    private fun hasTable(markdown: String): Boolean =
        MarkdownParser(GFMFlavourDescriptor())
            .buildMarkdownTreeFromString(prepareLessonMarkdown(markdown))
            .children.any { it.type.name == "TABLE" }

    private val table = "| Spanish | English |\n| --- | --- |\n| hola | hello |"

    @Test
    fun validTableIsRecognised() {
        assertTrue(hasTable(table))
    }

    @Test
    fun tableWithWindowsLineEndingsIsRecognised() {
        assertTrue(hasTable(table.replace("\n", "\r\n")))
    }

    @Test
    fun tableDirectlyUnderTextIsRecognised() {
        assertTrue(hasTable("Some greetings:\n$table"))
    }

    @Test
    fun textWithoutTablesIsUnchanged() {
        val text = "# Title\n\nA paragraph.\n- a list item"
        assertEquals(text, prepareLessonMarkdown(text))
    }

    @Test
    fun blankLineIsOnlyAddedOnceBeforeATable() {
        assertEquals("Intro\n\n$table", prepareLessonMarkdown("Intro\n$table"))
    }
}
