package interpreter

import java.math.BigInteger
import kotlin.math.max

enum class MemoryForm {
    HEX, DEC, CHAR
}

interface BrainfuckMemory<T : Number> {
    var updater: ((Int?) -> Unit)?

    fun memSize(): Int
    fun ensureSize(cell: Int)
    fun isZero(cell: Int): Boolean
    fun getCell(cell: Int): T
    fun setChar(cell: Int, char: Char)
    fun asChar(cell: Int): Char
    fun changeBy(cell: Int, by: Int)

    fun maxWidth(form: MemoryForm): Int
    fun format(t: Number, toWidth: Int): String

}

class BrainfuckMemoryByte(requestedSize: Int?) : BrainfuckMemory<Byte> {
    override var updater: ((Int?) -> Unit)? = null
    private val memory: MutableList<Byte> = MutableList(requestedSize ?: 1024) { 0 }

    override fun memSize() = memory.size

    override fun ensureSize(cell: Int) {
        if (cell > memory.lastIndex) repeat(cell - memory.lastIndex) { memory.add(0) }
    }

    override fun isZero(cell: Int) = getCell(cell) == 0.toByte()

    override fun getCell(cell: Int): Byte {
        ensureSize(cell)
        return memory[cell]
    }

    override fun setChar(cell: Int, char: Char) {
        ensureSize(cell)
        memory[cell] = char.toByte()
    }

    override fun asChar(cell: Int) = getCell(cell).toChar()

    override fun changeBy(cell: Int, by: Int) {
        ensureSize(cell)
        memory[cell] = (memory[cell] + by.toByte()).toByte()
    }

    override fun maxWidth(form: MemoryForm) = when (form) {
        MemoryForm.HEX -> 2
        MemoryForm.DEC -> 3
        MemoryForm.CHAR -> 1
    }

    override fun format(t: Number, toWidth: Int) = Integer.toHexString((t.toInt() + 256) % 256).padStart(toWidth, '0')

}

class BrainfuckMemoryShort(requestedSize: Int?) : BrainfuckMemory<Short> {
    override var updater: ((Int?) -> Unit)? = null
    private val memory: MutableList<Short> = MutableList(requestedSize ?: 1024) { 0 }

    override fun memSize() = memory.size

    override fun ensureSize(cell: Int) {
        if (cell > memory.lastIndex) repeat(cell - memory.lastIndex) { memory.add(0) }
    }

    override fun isZero(cell: Int) = getCell(cell) == 0.toShort()

    override fun getCell(cell: Int): Short {
        ensureSize(cell)
        return memory[cell]
    }

    override fun setChar(cell: Int, char: Char) {
        ensureSize(cell)
        memory[cell] = char.toShort()
    }

    override fun asChar(cell: Int) = getCell(cell).toChar()

    override fun changeBy(cell: Int, by: Int) {
        ensureSize(cell)
        memory[cell] = (memory[cell] + by.toShort()).toShort()
    }

    override fun maxWidth(form: MemoryForm) = when (form) {
        MemoryForm.HEX -> 4
        MemoryForm.DEC -> 5
        MemoryForm.CHAR -> 1
    }

    override fun format(t: Number, toWidth: Int): String =
        (if (t.toShort() >= 0) Integer.toHexString(t.toInt()) else Integer.toHexString(0xffff + t.toInt() + 1)).padStart(
            toWidth,
            '0'
        )
}

class BrainfuckMemoryInt(requestedSize: Int?) : BrainfuckMemory<Int> {
    override var updater: ((Int?) -> Unit)? = null
    private val memory: MutableList<Int> = MutableList(requestedSize ?: 1024) { 0 }

    override fun memSize() = memory.size

    override fun ensureSize(cell: Int) {
        if (cell > memory.lastIndex) repeat(cell - memory.lastIndex) { memory.add(0) }
    }

    override fun isZero(cell: Int) = getCell(cell) == 0

    override fun getCell(cell: Int): Int {
        ensureSize(cell)
        return memory[cell]
    }

    override fun setChar(cell: Int, char: Char) {
        ensureSize(cell)
        memory[cell] = char.toInt()
    }

    override fun asChar(cell: Int) = getCell(cell).toChar()

    override fun changeBy(cell: Int, by: Int) {
        ensureSize(cell)
        memory[cell] += by
    }

    override fun maxWidth(form: MemoryForm): Int {
        val max = memory.maxOf { it }
        return when (form) {
            MemoryForm.HEX -> Integer.toHexString(max).length
            MemoryForm.DEC -> max.toString().length
            MemoryForm.CHAR -> 1
        }
    }

    override fun format(t: Number, toWidth: Int): String =
        (if (t.toInt() >= 0) Integer.toHexString(t.toInt()) else Integer.toHexString((0xffffffff + t.toInt() + 1).toInt())).padStart(
            toWidth,
            '0'
        )
}

class BrainfuckMemoryLong(requestedSize: Int?) : BrainfuckMemory<Long> {
    override var updater: ((Int?) -> Unit)? = null
    private val memory: MutableList<Long> = MutableList(requestedSize ?: 1024) { 0 }

    override fun memSize() = memory.size

    override fun ensureSize(cell: Int) {
        if (cell > memory.lastIndex) repeat(cell - memory.lastIndex) { memory.add(0) }
    }

    override fun isZero(cell: Int) = getCell(cell) == 0.toLong()

    override fun getCell(cell: Int): Long {
        ensureSize(cell)
        return memory[cell]
    }

    override fun setChar(cell: Int, char: Char) {
        ensureSize(cell)
        memory[cell] = char.toLong()
    }

    override fun asChar(cell: Int) = getCell(cell).toChar()

    override fun changeBy(cell: Int, by: Int) {
        ensureSize(cell)
        memory[cell] = (memory[cell] + by.toLong())
    }

    override fun maxWidth(form: MemoryForm): Int {
        val max = memory.maxOf { it - if(it >= 0) Long.MAX_VALUE/2 else - Long.MAX_VALUE/2 }
        return when (form) {
            MemoryForm.HEX -> java.lang.Long.toHexString(max).length
            MemoryForm.DEC -> max.toString().length
            MemoryForm.CHAR -> 1
        }
    }

    override fun format(t: Number, toWidth: Int): String = java.lang.Long.toHexString(t as Long).padStart(toWidth, '0')
}

class BrainfuckMemoryBigNum(requestedSize: Int?) : BrainfuckMemory<BigInteger> {
    override var updater: ((Int?) -> Unit)? = null
    private val memory: MutableList<BigInteger> = MutableList(requestedSize ?: 1024) { BigInteger.ZERO }

    override fun memSize() = memory.size

    override fun ensureSize(cell: Int) {
        if (cell > memory.lastIndex) repeat(cell - memory.lastIndex) { memory.add(BigInteger.ZERO) }
    }

    override fun isZero(cell: Int) = getCell(cell) == BigInteger.ZERO

    override fun getCell(cell: Int): BigInteger {
        ensureSize(cell)
        return memory[cell]
    }

    override fun setChar(cell: Int, char: Char) {
        ensureSize(cell)
        memory[cell] = BigInteger.valueOf(char.toLong())
    }

    override fun asChar(cell: Int) = getCell(cell).toInt().toChar()

    override fun changeBy(cell: Int, by: Int) {
        ensureSize(cell)
        memory[cell] = memory[cell].plus(BigInteger.valueOf(by.toLong()))
    }

    override fun maxWidth(form: MemoryForm): Int {
        val maximum = memory.maxOf { it }
        val minimum = memory.minOf { it }
        return when(form) {
            MemoryForm.HEX -> max(maximum.toString(16).length, minimum.toString(16).length)
            MemoryForm.DEC -> max(maximum.toString().length, minimum.toString().length)
            MemoryForm.CHAR -> 1
        }
    }

    override fun format(t: Number, toWidth: Int): String {
        val isNegative = t.toInt() < 0
        val padLength = if(isNegative) toWidth - 1 else toWidth
        var ret = (t as BigInteger).abs().toString(16)
        ret = ret.padStart(padLength, '0')
        if(isNegative) ret = "-$ret"
        return ret
}

}
