package uiComponents.settings

import extensions.ButtonGroup
import extensions.changeListener
import extensions.setFilter
import extensions.withTTT
import interpreter.Dialect
import jetbrainsMono
import net.miginfocom.swing.MigLayout
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument
import java.awt.Dimension
import javax.swing.*
import javax.swing.border.EmptyBorder


class DialectsSettingScreen : JPanel() {

    private val unlimitedRB = JRadioButton("Unlimited")
    private val customRB = JRadioButton()
    private val customMemSize = JSpinner()
    private val memorySizeButtonGroup = ButtonGroup(unlimitedRB, customRB)

    private val cellSize = JComboBox(Dialect.CellSizes.values().map { it.text }.toTypedArray())

    private val eofAction = JComboBox(Dialect.EOFActions.values().map { it.text }.toTypedArray())

    private val wrapYesRB = JRadioButton("Yes")
    private val wrapNoRB = JRadioButton("No")
    private val wrapButtonGroup = ButtonGroup(wrapYesRB, wrapNoRB)

    private val printYesRB = JRadioButton("Yes")
    private val printNoRB = JRadioButton("No")
    private val printButtonGroup = ButtonGroup(printYesRB, printNoRB)

    private val breakChar = JTextField()

    init {
        border = EmptyBorder(10, 10, 10, 10)
        layout = MigLayout("", "[]20", "[]rel[]rel[]rel[]paragraph[]rel[]rel[]paragraph[]rel[]")

        //----------------------
        add(JLabel("Memory Settings"), "split, span")
        add(JSeparator(), "pad ${JLabel("A").preferredSize.height / 2}, grow, wrap")
        //----------------------
        add(JLabel("Number of memory cells:"), "gap 20")
        add(unlimitedRB.withTTT("Memory expands dynamically."))
        add(customRB, "split 2, span 2")
        customMemSize.model = SpinnerNumberModel(1000, 1, 1000000000, 1000)
        customMemSize.preferredSize = Dimension(10, customMemSize.preferredSize.height)
        add(customMemSize, "wrap")
        //----------------------
        val maxTTT = "The maximum value of each cell"
        add(JLabel("Cell size:").withTTT(maxTTT), "gap 20")
        add(cellSize.withTTT(maxTTT), "span, wrap")
        //----------------------
        add(JLabel("Cells wrap:").withTTT("Whether cells under and overflow"), "gap 20")
        add(wrapYesRB.withTTT("Cells do wrap"))
        add(wrapNoRB.withTTT("Cells don't wrap"), "wrap")
        //----------------------
        add(JLabel("IO Settings"), "split, span")
        add(JSeparator(), "gap 0, pad ${JLabel("A").preferredSize.height / 2}, grow, wrap")
        //----------------------
        add(JLabel("Print input to console:").withTTT("Whether user input should be automatically printed"), "gap 20")
        add(printYesRB.withTTT("Do print input"))
        add(printNoRB.withTTT("Don't print input"), "wrap")
        //----------------------
        val eofTTT = "What the cell should be set to when the EOF character is inputted"
        add(JLabel("EOF character action:").withTTT(eofTTT), "gap 20")
        add(eofAction.withTTT(eofTTT), "span")
        //----------------------
        add(JLabel("Code Settings"), "split, span")
        add(JSeparator(), "gap 0, pad ${JLabel("A").preferredSize.height / 2}, grow, wrap")
        //----------------------
        val breakTTT = "What character the program should break on or blank for nothing"
        add(JLabel("Break character:").withTTT(breakTTT), "gap 20")
        breakChar.setFilter { it.length <= 1 && !it.contains(Regex("[,.\\[\\]<>+-]")) }
//        breakText.changeListener {
//            breakChar = if(breakText.text == "") null else breakText.text
//
//            val m = (editBox.codeArea.document as RSyntaxDocument).javaClass.getDeclaredMethod("updateSyntaxHighlightingInformation")
//            m.isAccessible = true
//            m.invoke(editBox.codeArea.document)
//        }
        breakChar.font = jetbrainsMono
        breakChar.columns = 2
        add(breakChar.withTTT(breakTTT), "")
    }

    var dialect: Dialect
        get() =
            Dialect(
                if (memorySizeButtonGroup.selection === unlimitedRB.model) null else customMemSize.value as Int,
                Dialect.CellSizes.values()[Dialect.CellSizes.values().map { it.text }
                    .indexOf(cellSize.selectedItem as String)],
                Dialect.EOFActions.values()[Dialect.EOFActions.values().map { it.text }
                    .indexOf(eofAction.selectedItem as String)],
                wrapButtonGroup.selection === wrapYesRB,
                printButtonGroup.selection === printYesRB,
                if(breakChar.text == "") null else breakChar.text[0]
            )
        set(value) {
            memorySizeButtonGroup.setSelected(
                when (value.memoryCells) {
                    null -> unlimitedRB
                    else -> customRB
                }.model, true
            )
            customMemSize.value = value.memoryCells ?: 30000
            cellSize.selectedItem = value.cellSize.text
            eofAction.selectedItem = value.eofAction.text
            wrapButtonGroup.setSelected((if(value.cellsWrap) wrapYesRB else wrapNoRB).model, true)
            printButtonGroup.setSelected((if(value.printInput) printYesRB else printNoRB).model , true)
            breakChar.text = value.breakChar?.toString() ?: ""
        }
}
