import java.io.File

abstract class Puzzle(val name: String, val inputFileName: String, val testInputFileName: String) {

    var useTestInput: Boolean = true
    val input = { File(if (useTestInput) testInputFileName else inputFileName).bufferedReader().readLines() }

    abstract fun part1(): String
    abstract fun part2(): String

    fun solve() {
        listOf(true, false).forEach { useTestInput ->
            this.useTestInput = useTestInput

            println("# Running $name with ${"TEST".takeIf { useTestInput } ?: "REAL"} input")
            listOf(1 to ::part1, 2 to ::part2).forEach { (index, func) ->
                val result = try {
                    func()
                } catch (err: NotImplementedError) {
                    "not implemented"
                }

                println("\tpart $index = $result")
            }

            println()
        }
    }
}