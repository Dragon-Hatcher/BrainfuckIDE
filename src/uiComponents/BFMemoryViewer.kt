package uiComponents

import ideaDarkTheme
import interpreter.BrainfuckMemory
import interpreter.MemoryForm
import jetbrainsMono
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rtextarea.RTextScrollPane
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import kotlin.math.min


class BFMemoryViewer(memory: BrainfuckMemory<out Number>) : JPanel() {

    var memory: BrainfuckMemory<out Number> = memory
        set(value) {
            offset = 0
            scrollPane.gutter.lineNumberingStartIndex = 0
            value.updater = { updateString(it) }
            field = value
            updateString(null)
        }
    private val editorPane = RSyntaxTextArea()//object : RSyntaxTextArea() {

    //        override fun scrollRectToVisible(aRect: Rectangle?) {
//        }
//    }
    private val scrollPane = RTextScrollPane(editorPane)

    var offset = 0

    init {
        scrollPane.lineNumbersEnabled = true
        scrollPane.gutter.lineNumberingStartIndex = 0

        editorPane.columns = 54
        editorPane.font = jetbrainsMono
        editorPane.isEditable = false
        editorPane.syntaxEditingStyle = "text/memory"
        ideaDarkTheme.apply(editorPane)

        updateString(null)
        scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER

        this.layout = BorderLayout()
        this.add(scrollPane, BorderLayout.CENTER)
    }

    private fun updateString(loc: Int?) {
        val numWidth = memory.maxWidth(MemoryForm.HEX)
//        println("numWidth:$numWidth")
        val partWidth = numWidth + 1
        editorPane.columns = 18 * partWidth

        if (loc != null) {
            offset = when {
                loc < offset -> (loc / 16) * 16
                loc >= offset + 1024 -> roundUp(loc + 1, 16) * 16 - 1024
                else -> offset
            }
            scrollPane.gutter.lineNumberingStartIndex = offset % 16
        }

        val sb = StringBuilder()
        if (loc != null) memory.getCell(loc)
//            for (index in 0 until memory!!.memSize()) {
        for (index in offset until min(offset + 1024, memory.memSize())) {
            val b = memory.getCell(index)
            sb.append(memory.format(b, numWidth))
            sb.append(if ((index + 1) % 16 == 0) '\n' else ' ')
        }
        if (loc != null) {
            val start = (loc - offset) * partWidth + numWidth
            sb.replace(start, start + if ((loc + 1) % 16 == 0) 0 else 1, "\u00A0")
        }
        sb.replace(sb.length - 1, sb.length, "")
        val start = editorPane.selectionStart
        val end = editorPane.selectionEnd
        editorPane.text = sb.toString()
        editorPane.select(start, end)
        if (loc != null) {
            editorPane.caretPosition = (loc - offset) * partWidth + numWidth
            scrollPane.gutter.lineNumberingStartIndex = offset / 16
        }
        scrollPane.horizontalScrollBar.model.value = 0
    }

    fun highlightNumber(n: Int) {
        editorPane.select((n - offset) * 3, (n - offset) * 3 + 2)
//        editorPane.requestFocus()
    }
}

fun roundUp(num: Int, divisor: Int): Int {
    return (num + divisor - 1) / divisor
}