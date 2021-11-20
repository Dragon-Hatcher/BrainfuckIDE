package uiComponents

import codeArea.BFCodeArea
import doBreaks
import extensions.changeListener
import extensions.recursivelyAddKeyListener
import extensions.setFilter
import interpreter.BrainfuckMemory
import interpreter.BrainfuckMemoryByte
import interpreter.BrainfuckRunner
import interpreter.compileBrainfuck
import jetbrainsMono
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument
import settings
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.File
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.filechooser.FileNameExtensionFilter


class BFScreen(menuBar: BFMenuBar) : JPanel(), KeyListener {

    var currentFile: File? = null
    var lastSavedText: String? = null
    var fileChooserDirectory = File("")

    private val buttons = JPanel()
    private val runB = JButton("run")
    private val pauseB = JButton("pause")
    private val playB = JButton("play")
    private val speed = JSlider(JSlider.HORIZONTAL, 1, 500, 250)
    private val actualSpeed
        get() = 2*speed.value*speed.value
    private val breakCheck = JCheckBox("", true)
    private val editBox = BFCodeArea()
    private val memoryView = BFMemoryViewer(BrainfuckMemoryByte(1024))
    private val outputBox = BFOutput()
    private val stepB = JButton("step")

    private var runner: BrainfuckRunner? = null

    val extraPanel = JPanel()

    init {
        this.border = EmptyBorder(10, 10, 10, 10)
        editBox.border = EmptyBorder(0, 10, 5, 5)
        memoryView.border = EmptyBorder(0, 5, 5, 10)
        extraPanel.layout = BorderLayout()
        extraPanel.add(editBox, BorderLayout.CENTER)
        extraPanel.add(memoryView, BorderLayout.EAST)

        outputBox.border = EmptyBorder(5, 10, 10, 10)

        val splitPanel = JSplitPane(JSplitPane.VERTICAL_SPLIT, extraPanel, outputBox)
        splitPanel.resizeWeight = 0.8

        buttons.border = EmptyBorder(5, 10, 0, 10)
        buttons.layout = FlowLayout(FlowLayout.LEFT)
        buttons.add(runB)
        buttons.add(pauseB)
        buttons.add(playB)
        buttons.add(JLabel("Speed:").also { it.font = jetbrainsMono })
        buttons.add(speed)
        buttons.add(JLabel("Do breakpoints:").also { it.font = jetbrainsMono })
        buttons.add(breakCheck)
        buttons.add(stepB)

        runB.font = jetbrainsMono
        runB.addActionListener {
            run(true)
        }
        pauseB.font = jetbrainsMono
        pauseB.isEnabled = false
        pauseB.addActionListener {
            pause()
        }
        playB.font = jetbrainsMono
        playB.isEnabled = false
        playB.addActionListener {
            Thread {
                runner?.stop = false
                runner?.run()
            }.start()
            playB.isEnabled = false
            pauseB.isEnabled = true
        }
        speed.addChangeListener {
            runner?.speed = actualSpeed
        }
        breakCheck.addActionListener {
            doBreaks = breakCheck.isSelected
        }
        stepB.font = jetbrainsMono
        stepB.addActionListener {
            if(runner == null) {
                run(false)
            }
            pause()
            Thread {runner?.step()}.start()
        }

        this.layout = GridBagLayout()
        val gbc = GridBagConstraints()
        gbc.weightx = 1.0
        gbc.weighty = 0.0
        gbc.fill = GridBagConstraints.BOTH
        this.add(buttons, gbc)
        gbc.gridy = 1
        gbc.weighty = 1.0
        this.add(splitPanel, gbc)

        this.preferredSize = Dimension(
            this.preferredSize.width,
            this.preferredSize.height.coerceAtMost((Toolkit.getDefaultToolkit().screenSize.height * 0.75).toInt())
        )

        editBox.codeArea.addHyperlinkListener {
            memoryView.highlightNumber(Integer.parseInt(it.description.removePrefix("no protocol: ")))
        }

        menuBar.save.addActionListener {
            if(currentFile == null) saveAsChoose()
            saveFile()
        }
        menuBar.saveAs.addActionListener {
            saveAsChoose()
            saveFile()
        }
        menuBar.open.addActionListener {
            if(checkDiscardChanges()) {
                chooseFile()
                currentFile?.readText()?.replace("\r\n", "\n").let {
                    editBox.codeArea.text = it
                    lastSavedText = it
                }
            }
        }
        menuBar.new.addActionListener {
            if(checkDiscardChanges()) {
                editBox.codeArea.text = ""
                currentFile = null
            }
        }
        menuBar.changeDialect.addActionListener {
//            DialectsPopup()
            launchSettings()
        }

        recursivelyAddKeyListener(this)
    }

    private fun checkDiscardChanges(): Boolean {
        return if(editBox.codeArea.text != lastSavedText && !(currentFile == null && editBox.codeArea.text == "")) {
            val pick = JOptionPane.showOptionDialog(
                this,
                "You have unsaved work. Would you like to discard it?",
                "Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                arrayOf("Discard", "Cancel"),
                "Cancel"
            )
            pick == 0
        } else {
            true
        }
    }

    private fun saveFile() {
        currentFile?.writeText(editBox.codeArea.text)
        lastSavedText = editBox.codeArea.text
    }

    private fun chooseFile() {
        val fileChooser = JFileChooser()
        fileChooser.dialogTitle = "Open"
        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
        fileChooser.fileFilter = FileNameExtensionFilter("BF Files", "txt", "bf", "brainfuck")
        fileChooser.currentDirectory = fileChooserDirectory
        val selection = fileChooser.showOpenDialog(this)
        if(selection == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.selectedFile
            runner?.stop = true
            runner = null
            println(currentFile)
        }
        fileChooserDirectory = fileChooser.currentDirectory
    }

    private fun saveAsChoose() {
        val fileChooser = JFileChooser()
        fileChooser.dialogTitle = "Save As"
        val selection = fileChooser.showSaveDialog(this)
        if(selection == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.selectedFile
            println(currentFile)
        }
        fileChooserDirectory = fileChooser.currentDirectory
    }

    private fun switchRunner(runner: BrainfuckRunner) {
        memoryView.memory = runner.memory
    }

    private fun pause() {
        runner?.stop = true
        runB.isEnabled = true
        playB.isEnabled = true
        pauseB.isEnabled = false
    }

    private fun run(start: Boolean) {
        runner = settings.dialectsSettingScreen.dialect.getRunnerForCode(editBox.codeArea.text, outputBox.outputArea, editBox.codeArea, actualSpeed)
        if(runner == null) {
            editBox.scrollArea.putClientProperty("JComponent.outline", "error")
            editBox.scrollArea.repaint()
//            outputBox.outputArea.text = "31[mNon matching brackets"
        } else {
            editBox.scrollArea.putClientProperty("JComponent.outline", "")
            editBox.scrollArea.repaint()
            Thread {
                runB.isEnabled = false
                pauseB.isEnabled = true
                playB.isEnabled = false
                outputBox.outputArea.text = ""
                runner!!.end = {
                    runB.isEnabled = true
                    pauseB.isEnabled = false
                    runner = null
                }
                runner!!.breakM = ::pause
                switchRunner(runner!!)
                runner!!.stop = !start
                runner!!.run()
            }.start()
        }
    }

    override fun keyTyped(e: KeyEvent?) {
        e ?: return
        if(runner?.accepting == true) {
            runner?.input = e.keyChar
        }
    }

    override fun keyPressed(e: KeyEvent?) {}
    override fun keyReleased(e: KeyEvent?) {}


}

fun launchSettings() {
    settings.isVisible = true
}