package extensions

import javax.swing.JComponent
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.*

fun JTextField.setFilter(test: (String) -> Boolean) {
    (this.document as PlainDocument).documentFilter = TextFieldFilter(test)
}

internal class TextFieldFilter(val test: (String) -> Boolean) : DocumentFilter() {

    @Throws(BadLocationException::class)
    override fun insertString(fb: FilterBypass, offset: Int, string: String?,
                              attr: AttributeSet?) {

        val doc: Document = fb.document
        val sb = StringBuilder()
        sb.append(doc.getText(0, doc.length))
        sb.insert(offset, string)
        if (test(sb.toString())) {
            super.insertString(fb, offset, string, attr)
        }
    }


    @Throws(BadLocationException::class)
    override fun replace(fb: FilterBypass, offset: Int, length: Int, text: String?,
                         attrs: AttributeSet?) {
        val doc: Document = fb.document
        val sb = StringBuilder()
        sb.append(doc.getText(0, doc.length))
        sb.replace(offset, offset + length, text)
        if (test(sb.toString())) {
            super.replace(fb, offset, length, text, attrs)
        }
    }

    @Throws(BadLocationException::class)
    override fun remove(fb: FilterBypass, offset: Int, length: Int) {
        val doc: Document = fb.document
        val sb = StringBuilder()
        sb.append(doc.getText(0, doc.length))
        sb.delete(offset, offset + length)

        if (test(sb.toString())) {
            super.remove(fb, offset, length)
        }
    }
}

fun JTextField.changeListener(listener: (DocumentEvent?) -> Unit) {
    this.document.addDocumentListener(object : DocumentListener {
        override fun insertUpdate(e: DocumentEvent?) {
            listener(e)
        }

        override fun removeUpdate(e: DocumentEvent?) {
            listener(e)
        }

        override fun changedUpdate(e: DocumentEvent?) {
            listener(e)
        }
    })
}