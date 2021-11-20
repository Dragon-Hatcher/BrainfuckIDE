package interpreter

fun compileBrainfuck(code: String, breakChar: Char?): CompiledProgram? {
//    val code = codeIn.replace(Regex("[^+\\-<>\\[\\].,${breakChar ?: ""}]"), "")

    val repeatCommands = listOf('+', '-', '<', '>')
    val otherCommands = listOf('[', ']', '.', ',', breakChar)

    val commands: MutableList<Char> = mutableListOf()
    val annotations: MutableList<Int> = mutableListOf()
    val sourceLocations: MutableList<Int> = mutableListOf()

    //get commands
    var i = 0
    while(i < code.length) {
        when (val c = code[i]) {
            in repeatCommands -> {
                commands.add(c)
                sourceLocations.add(i)
                var repeat = i
                while(repeat < code.length && code[repeat] == c) {repeat++}
                annotations.add((if(c == '-' || c == '<') -1 else 1) *(repeat - i))
                i = repeat
            }
            in otherCommands -> {
                commands.add(if(c == breakChar) 'b' else c)
                annotations.add(0)
                sourceLocations.add(i)
                i++
            }
            else -> i++
        }
    }

    //match brackets
    val locations: MutableList<Int> = mutableListOf()
    for((index, char) in commands.withIndex()) {
        when(char) {
            '[' -> locations.add(index)
            ']' -> {
                val last = locations.removeLastOrNull() ?: return null
                annotations[index] = last
                annotations[last] = index + 1
            }
        }
    }
    if(locations.isNotEmpty()) return null

    return CompiledProgram(commands, annotations, sourceLocations)
}