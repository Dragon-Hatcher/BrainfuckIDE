package interpreter

import doBreaks
import java.io.InputStream
import java.io.OutputStream
import javax.swing.JTextArea
import javax.swing.JTextPane
import javax.swing.SwingUtilities
import kotlin.math.abs

const val MILLIS_IN_SECOND = 1000
const val BYTE_ZERO: Byte = 0

class BrainfuckRunner(
    val program: CompiledProgram,
    val memory: BrainfuckMemory<out Number>,
    val output: JTextArea,
    val codeBox: JTextArea,
    var speed: Int
) {

    var stop = false

    var end: (() -> Unit)? = null
    var breakM: (() -> Unit)? = null

    var memPointer = 0
    var oldLoc = 0
    var compiledLocation = 0
        set(value) {
            oldLoc = compiledLocation
            field = value
        }

    var accepting = false
    var input: Char? = null
        set(value) {
            field = value
            accepting = false
        }

    var outputText = StringBuilder()

    fun run() {
        codeBox.isEditable = false
        while (true) {
            for (i in 1..(speed)) {
                if (stop ||
                    compiledLocation >= program.commands.size) {
                    SwingUtilities.invokeAndWait { codeBox.isEditable = true }
                    if(oldLoc < program.commands.size) updateUI()
                    if(compiledLocation >= program.commands.size) end?.let { it() }
                    return
                }
                oneCommand()
            }
            updateUI()
            Thread.sleep(1)
        }
    }

    fun step() {
        if (compiledLocation >= program.commands.size) {
            SwingUtilities.invokeAndWait { codeBox.isEditable = true }
            if(oldLoc < program.commands.size) updateUI()
            end?.let { it() }
            return
        }
        oneCommand()
        updateUI()
    }

    private fun updateUI() {
        SwingUtilities.invokeAndWait {
            if(outputText.toString() != output.text) output.text = outputText.toString()
            memory.updater?.let{it(memPointer)}
            codeBox.selectionStart = program.sourceLocations[oldLoc]
            codeBox.selectionEnd = program.sourceLocations[oldLoc] + 1
            if(program.commands[oldLoc] in setOf('+', '-', '<', '>')) {
                codeBox.selectionEnd += abs(program.annotations[oldLoc]) - 1
            }
        }
    }

    private fun oneCommand() {
        when (program.commands[compiledLocation]) {
            '+', '-' -> {
                memory.changeBy(memPointer, program.annotations[compiledLocation])
                compiledLocation++
            }
            '>', '<' -> {
                memPointer += program.annotations[compiledLocation]
                compiledLocation++
            }
            '[' -> {
                if (memory.isZero(memPointer)) {
                    compiledLocation = program.annotations[compiledLocation]
                } else {
                    compiledLocation++
                }
            }
            ']' -> {
                compiledLocation = program.annotations[compiledLocation]
            }
            '.' -> {
                outputText.append(memory.asChar(memPointer))
                compiledLocation++
            }
            ',' -> {
                updateUI()
                if(input != null) {
                    memory.setChar(memPointer, input!!)
                    outputText.append(input!!)
                    compiledLocation++
                    input = null
                } else {
                    accepting = true
                }
            }
            'b' -> {
                if(doBreaks) {
                    stop = true
                    breakM?.let { it() }
                }
                compiledLocation++
            }

        }
    }

}