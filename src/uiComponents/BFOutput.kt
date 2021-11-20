package uiComponents

import ideaDarkTheme
import jetbrainsMono
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rtextarea.RTextScrollPane
import java.awt.BorderLayout
import javax.swing.JPanel

class BFOutput : JPanel() {
    val outputArea = RSyntaxTextArea(10, 50)
    private val scrollArea = RTextScrollPane(outputArea)

    init {
        ideaDarkTheme.apply(outputArea)
        outputArea.font = jetbrainsMono.deriveFont(13f)
        outputArea.isEditable = false
        outputArea.syntaxEditingStyle = "text/nostyle"
        scrollArea.lineNumbersEnabled = false

        this.layout = BorderLayout()
        this.add(scrollArea, BorderLayout.CENTER)
    }
}