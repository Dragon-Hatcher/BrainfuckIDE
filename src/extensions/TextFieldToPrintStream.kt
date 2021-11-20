package extensions

import java.io.OutputStream
import java.io.PrintStream
import java.lang.Exception
import javax.swing.JTextArea
import javax.swing.JTextPane
import javax.swing.SwingUtilities

fun JTextArea.getPrintStream() = TextPanePrintStream(this)


class TextPanePrintStream(private val textPane: JTextArea) : OutputStream() {
    override fun write(b: Int) {
        SwingUtilities.invokeAndWait {
            textPane.text += (b.toChar().toString())
            textPane.caretPosition = textPane.document.length
        }
    }
}