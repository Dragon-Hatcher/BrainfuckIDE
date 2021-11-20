package interpreter

class CompiledProgram(val commands: List<Char>, val annotations: List<Int>, val sourceLocations: List<Int>)