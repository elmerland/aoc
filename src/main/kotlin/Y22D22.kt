import Y22D22.Dir.RIGHT
import java.lang.IllegalStateException

class Y22D22(name: String, input: String, testInput: String) : Puzzle(name, input, testInput) {
    override fun part1(): String {
        val maze = parseMaze()

        val player = Player(maze.startPos.first, maze.startPos.second, RIGHT)

        maze.path.forEach { action ->
            val steps = action.toIntOrNull()
            if (steps != null) {
                maze.move(player, steps)
            } else {
                player.dir = player.dir.move(action)
            }
        }
        return ((1_000 * (player.row + 1)) + (4 * (player.col + 1)) + (player.dir.num)).toString()
    }

    override fun part2(): String {
        TODO("Not yet implemented")
    }

    private fun parseMaze(): Maze {
        val mazeLines = input().subList(0, input().size - 2)

        val maxCol = mazeLines.maxOf { it.length }
        val mazeInput = mazeLines.map { it.padEnd(maxCol, ' ').toCharArray().map(Char::toString) }
        val startingRow =  mazeInput.indexOfFirst { it.contains(".") }
        val startingCol = mazeInput[startingRow].indexOfFirst { it == "." }
        val path = input().last().replace(Regex("[RL]")) { "|${it.value}|" }.split("|")
//        println(mazeInput.map { it.map { if (it == " ") "_" else it } }.joinToString("\n") {it.joinToString("") })
//        println(mazeInput.size to mazeInput[0].size)
//        println("$startingRow, $startingCol")
//        println(path)
        return Maze(mazeInput, startingRow to startingCol, path)
    }



    data class Maze(val content: List<List<String>>, val startPos: Pair<Int, Int>, val path: List<String>) {
        private val rows = content.size
        private val cols = content[0].size
        fun move(player: Player, steps: Int) {
            repeat(steps) {
                var newLoc = player.loc.move(player.dir)
//                println(newLoc)
                while (newLoc.isOutOfBounds()) {
                    newLoc = newLoc.move(player.dir)
//                    println(newLoc)
                }
                if (newLoc.isWall()) {
                    return
                } else {
                    player.loc = newLoc
                }
            }
        }

        private fun Pair<Int, Int>.isWall() = content[first][second] == "#"
        private fun Pair<Int, Int>.isOutOfBounds() = content[first][second] == " "

        private fun Pair<Int, Int>.move(dir: Dir) : Pair<Int, Int> {
            return (first + dir.row).mod(rows) to (second + dir.col).mod(cols)
        }
    }
    class Player(var row: Int, var col: Int, var dir: Dir) {
        var loc: Pair<Int, Int>
            get() = row to col
            set(newLoc) {
                row = newLoc.first
                col = newLoc.second
            }

        override fun toString(): String {
            return "Player($row, $col, $dir)"
        }
    }

    enum class Dir(val row: Int, val col: Int, val num: Int) {
        UP(-1, 0, 3),
        RIGHT(0, 1, 0),
        DOWN(1, 0, 1),
        LEFT(0, -1, 2);

        override fun toString(): String {
            return "Dir($name, $num)"
        }

        fun right() = when (this) {
            UP -> RIGHT
            RIGHT -> DOWN
            DOWN -> LEFT
            LEFT -> UP
        }

        fun left() = when (this) {
            UP -> LEFT
            RIGHT -> UP
            DOWN -> RIGHT
            LEFT -> DOWN
        }

        fun move(action: String) = when (action) {
            "R" -> this.right()
            "L" -> this.left()
            else -> throw IllegalStateException()
        }
    }

}

//fun Pair<Int, Int>.move(dir: Y22D22.Dir, rowBound: Int, colBound: Int) : Pair<Int, Int> {
//    return (first + dir.row).mod(rowBound) to (second + dir.col).mod(colBound)
//}

//private fun <A, B> Pair<A, B>.move(dir: Y22D22.Dir) {
//
//}
