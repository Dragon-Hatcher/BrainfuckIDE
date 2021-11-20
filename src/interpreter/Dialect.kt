package interpreter

import java.math.BigInteger
import javax.swing.JTextArea

data class Dialect(
    val memoryCells: Int?,
    val cellSize: CellSizes,
    val eofAction: EOFActions,
    val cellsWrap: Boolean,
    val printInput: Boolean,
    val breakChar: Char?,
) {
    enum class CellSizes(val text: String) {
        EIGHT("8 bit"),
        SIXTEEN("16 bit"),
        THIRTY_TWO("32 bit"),
        SIXTY_FOUR("64 bit"),
        UNLIMITED("Unlimited")
    }

    enum class EOFActions(val text: String) {
        ZERO("Zero"),
        NEGATIVE_ONE("Negative One"),
        NO_CHANGE("No Change")
    }

    fun getRunnerForCode(code: String, output: JTextArea, codeBox: JTextArea, speed: Int): BrainfuckRunner? {
        val compiledCode = compileBrainfuck(code, breakChar)
        compiledCode ?: return null

        println("mem cells: $memoryCells")
        val memory = when (cellSize) {
            CellSizes.EIGHT -> BrainfuckMemoryByte(memoryCells)
            CellSizes.SIXTEEN -> BrainfuckMemoryShort(memoryCells)
            CellSizes.THIRTY_TWO -> BrainfuckMemoryInt(memoryCells)
            CellSizes.SIXTY_FOUR -> BrainfuckMemoryLong(memoryCells)
            CellSizes.UNLIMITED -> BrainfuckMemoryBigNum(memoryCells)
        }

        return BrainfuckRunner(compiledCode, memory, output, codeBox, speed)
    }
}