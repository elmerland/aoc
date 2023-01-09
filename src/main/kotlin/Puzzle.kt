import java.io.File

abstract class Puzzle(private val year: Int, private val day: Int) {

    val input = File("src/main/kotlin/input/input${year}d$day.txt").bufferedReader().readLines()

    fun inputLine() = input[0]

    abstract fun part1(): String
    abstract fun part2(): String

    fun solve() {
        listOf(1 to ::part1, 2 to ::part2).forEach { (index, func) ->
            val result = try {
                func()
            } catch (err: NotImplementedError) {
                "not implemented"
            }

            println("$year-$day part $index = $result")
        }
    }
}