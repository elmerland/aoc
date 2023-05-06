package puzzles

import Puzzle
import puzzles.Y22D14.Coord.Companion.toCoord
import kotlin.math.sign

class Y22D14(name: String, input: String, testInput: String) : Puzzle(name, input, testInput) {
    override fun part1(): String {
        val lines = input().map { line ->
            line
                .split(" -> ")
                .map { coordinate ->
                    coordinate.toCoord()
                }
        }

        val sandSource = Coord(500, 0)
        val down = Coord(0, 1)
        val left = Coord(-1, 0)
        val right = Coord(1, 0)

        val rocks = lines.map { line ->
            line.zipWithNext().map { (start, end) ->
                val result = mutableListOf<Coord>()
                val (xDist, yDist) = (end.x - start.x) to (end.y - start.y)
                val dir = Coord(xDist.sign, yDist.sign)
                var curr = start
                while (curr != end) {
                    result.add(curr)
                    curr += dir
                }
                result.add(end)
                result
            }.flatten()
        }.flatten().toSet()

        val sand = mutableSetOf<Coord>()

//        val grid =
//            (0..10).joinToString("\n") { y ->
//            (490..505).joinToString("") { x ->
//                val c = Coord(x, y)
//                if (c == sandSource) {
//                    "+"
//                } else if (rocks.contains(c)) {
//                    "#"
//                } else {
//                    "."
//                }
//            }
//        }
//        println(grid)

        val lowestYPosition = rocks.maxBy { it.y }.y

        fun nextMove(pos: Coord): Coord? {
            val d = pos + down
            val l = pos + down + left
            val r = pos + down + right
            return if (!rocks.contains(d) && !sand.contains(d)) {
                d
            } else if (!rocks.contains(l) && !sand.contains(l)) {
                l
            } else if (!rocks.contains(r) && !sand.contains(r)) {
                r
            } else {
                null
            }
        }

        var grainsAtRest = 0

        while (true) {
            var grain = sandSource

            while (true) {
                val newPos = nextMove(grain)
                if (newPos == null) {
                    grainsAtRest++
                    sand.add(grain)
                    break
                } else {
                    if (newPos.y >= lowestYPosition) {
                        return grainsAtRest.toString()
                    }
                    grain = newPos
                }
            }
        }
    }

    override fun part2(): String {
        val lines = input().map { line ->
            line
                .split(" -> ")
                .map { coordinate ->
                    coordinate.toCoord()
                }
        }

        val sandSource = Coord(500, 0)
        val down = Coord(0, 1)
        val left = Coord(-1, 0)
        val right = Coord(1, 0)

        val rocks = lines.map { line ->
            line.zipWithNext().map { (start, end) ->
                val result = mutableListOf<Coord>()
                val (xDist, yDist) = (end.x - start.x) to (end.y - start.y)
                val dir = Coord(xDist.sign, yDist.sign)
                var curr = start
                while (curr != end) {
                    result.add(curr)
                    curr += dir
                }
                result.add(end)
                result
            }.flatten()
        }.flatten().toSet()

        val sand = mutableSetOf<Coord>()

        val lowestYPosition = rocks.maxBy { it.y }.y + 2

        fun nextMove(pos: Coord): Coord? {
            val d = pos + down
            val l = pos + down + left
            val r = pos + down + right
            return if (pos.y == lowestYPosition - 1) {
                null
            } else if (!rocks.contains(d) && !sand.contains(d)) {
                d
            } else if (!rocks.contains(l) && !sand.contains(l)) {
                l
            } else if (!rocks.contains(r) && !sand.contains(r)) {
                r
            } else {
                null
            }
        }

        fun printGrid() {
            val grid =
                (0..170).joinToString("\n") { y ->
                    "${"%03d".format(y)} " + (300..800).joinToString("") { x ->
                        val c = Coord(x, y)
                        if (rocks.contains(c)) {
                            "#"
                        } else if (sand.contains(c)) {
                            "o"
                        } else if (c == sandSource) {
                            "+"
                        } else {
                            "."
                        }
                    }
                }
            println(grid)
        }

        var grainsAtRest = 0

        while (true) {
            var grain = sandSource

            while (true) {
                val newPos = nextMove(grain)
                if (newPos == null) {
                    grainsAtRest++
                    if (grain == sandSource) {
//                        printGrid()
                        return grainsAtRest.toString()
                    }
                    sand.add(grain)
                    break
                } else {
                    grain = newPos
                }
            }
        }
    }

    data class Coord(val x: Int, val y: Int) {
        operator fun plus(other: Coord) = Coord(x + other.x, y + other.y)

        companion object {
            fun String.toCoord() = this.split(",").let { Coord(it[0].toInt(), it[1].toInt()) }
        }
    }
}