package puzzles

import Point
import Point.Companion.maxX
import Point.Companion.maxY
import Point.Companion.minX
import Point.Companion.minY
import Puzzle
import puzzles.Y22D17.Rock.Companion.move
import puzzles.Y22D17.Rock.Companion.toPoints

class Y22D17(name: String, input: String, testInput: String) : Puzzle(name, input, testInput) {
    override fun part1(): String {
        val moveQueue = input()[0].map {
            when (it) {
                '>' -> Move.RIGHT
                '<' -> Move.LEFT
                else -> throw IllegalArgumentException()
            }
        }
        var moveIndex = 0

        fun getNextMove(): Move {
            val move = moveQueue[moveIndex]
            moveIndex = (moveIndex + 1) % moveQueue.size
            return move
        }

        val rockQueue = listOf(Rock.MINUS, Rock.PLUS, Rock.ANGLE, Rock.BAR, Rock.SQUARE)
        var rockIndex = 0

        fun getNextRock(): Rock {
            val rock = rockQueue[rockIndex]
            rockIndex = (rockIndex + 1) % rockQueue.size
            return rock
        }


        val totalRocks = 2022
        var stoppedRockCount = 0
        var currRock: List<Point>? = null
        var currBottomLeft: Point? = null
        var currFloor = 0


        val floorGrid = mutableSetOf<Point>()

        fun printGrid(yRange: IntRange? = null) {
            val range = if (yRange != null) yRange else {
                val top = currRock?.maxY()?.y ?: floorGrid.maxY().y
                (0..top)
            }
            val result = range.map { y ->
                (0..6).map { x ->
                    val p = Point(x, y)
                    when {
                        floorGrid.contains(p) -> '#'
                        currRock?.contains(p) ?: false -> '@'
                        else -> '.'
                    }
                }
            }.mapIndexed { index, chars -> "%03d ".format(index) + chars.joinToString("", prefix = "|", postfix = "|") }
                .reversed()
                .joinToString("\n")
            println("$result\n    +-------+\n")
        }

        return ""
        fun moveIfPossible(currPos: List<Point>, dir: Point): List<Point>? {
            val nextMove = currPos.move(dir)
            val leftmost = nextMove.minX().x
            val rightmost = nextMove.maxX().x
            val downmost = nextMove.minY().y
            val isInBounds = leftmost >= 0 && rightmost <= 6 && downmost >= 0
            val doesNotIntersectsWithStoppedRocks = floorGrid.intersect(nextMove).isEmpty()
            return if (isInBounds && doesNotIntersectsWithStoppedRocks) {
                nextMove
            } else null
        }

        while (stoppedRockCount < totalRocks) {
            if (currRock == null) {
                currBottomLeft = Point(2, currFloor + 3)
                currRock = getNextRock().toPoints(currBottomLeft)
            }

            // Jet of gas pushes rock
            currRock = when (getNextMove()) {
                Move.LEFT -> {
                    moveIfPossible(currRock, Point.LEFT) ?: currRock
                }

                Move.RIGHT -> {
                    moveIfPossible(currRock, Point.RIGHT) ?: currRock
                }
            }

            // Move rock down if possible
            val downMove = moveIfPossible(currRock, Point.DOWN)

            if (downMove == null) {
                // Cannot move down. Rock is stopped
                floorGrid.addAll(currRock)
                currRock = null
                stoppedRockCount += 1
                currFloor = floorGrid.maxY().y + 1
            } else {
                currRock = downMove
            }
        }

        return (floorGrid.maxY().y + 1).toString()
    }

    override fun part2(): String {
        val moveQueue = input()[0].map {
            when (it) {
                '>' -> Move.RIGHT
                '<' -> Move.LEFT
                else -> throw IllegalArgumentException()
            }
        }
        var moveIndex = 0

        fun getNextMove(): Move {
            val move = moveQueue[moveIndex]
            moveIndex = (moveIndex + 1) % moveQueue.size
            return move
        }

        val rockQueue = listOf(Rock.MINUS, Rock.PLUS, Rock.ANGLE, Rock.BAR, Rock.SQUARE)
        var rockIndex = 0

        fun getNextRock(): Rock {
            val rock = rockQueue[rockIndex]
            rockIndex = (rockIndex + 1) % rockQueue.size
            return rock
        }

//        val queueSize = 10091
        val chamber = Chamber()

        fun printGrid(yRange: IntRange? = null) {
            val range = yRange ?: 0..chamber.height + 2
            val result = range.map { y ->
                (0..6).map { x ->
                    val p = Point(x, y)
                    when {
                        chamber.contains(p) -> '#'
//                        currRock?.contains(p) ?: false -> '@'
                        else -> '.'
                    }
                }
            }.mapIndexed { index, chars -> "%03d ".format(index) + chars.joinToString("", prefix = "|", postfix = "|") }
                .reversed()
                .joinToString("\n")
            println("$result\n    +-------+\n")
        }

        data class State(val rock: Rock, val moveIndex: Int, val skyline: List<Point>)
        val seenStates = mutableMapOf<State, Pair<Long, Int>>()
//            MutableHashMap<State, Pair<Long, Int>>()
        val otherStates = mutableMapOf<Pair<Rock, Int>, Int>()
        var heightFromCycles = 0L

//        val totalRocks = 2022L
        val totalRocks = 1_000_000_000_000L

        while (totalRocks > chamber.rocksAdded) {
            val rock = getNextRock()
            chamber.addRock(rock, ::getNextMove)
            if (chamber.height < 8) continue
            val rockCount = chamber.rocksAdded

            val state = State(rock, moveIndex, chamber.getSkyline(8))
            val foundState = seenStates[state]
            if (foundState != null) {
                val (prevRocksAdded, prevHeight) = foundState
                println("cycle found!")
                val rocksInCycle = rockCount - prevRocksAdded
                val cyclesRemaining = (totalRocks - rockCount) / rocksInCycle

                chamber.rocksAdded += cyclesRemaining * rocksInCycle
                val cycleHeight = chamber.height - prevHeight
                heightFromCycles = cyclesRemaining * (cycleHeight)
                seenStates.clear()
            } else {
                seenStates[state] = rockCount to chamber.height
            }

//            val otherState = rock to moveIndex
//            otherStates[otherState]?.let { prevHeight ->
//                println("found other cycle - rock count $rockCount ${chamber.height} ${seenStates.size}")
//
//                chamber.printGrid(prevHeight)
//
//                chamber.printGrid(chamber.height)
//
//                throw IllegalStateException("bruh")
//            } ?: run { otherStates[otherState] = chamber.height}

            if (rockCount % 1_000_000L == 0L) {
                println("rock count $rockCount ${chamber.height} ${seenStates.size}")
//                chamber.clearSome()
            }
        }

        return (heightFromCycles + chamber.height + 1).toString()
    }

    enum class Move { RIGHT, LEFT; }

    enum class Rock {
        MINUS,
        PLUS,
        ANGLE,
        BAR,
        SQUARE;

        fun width() = when (this) {
            MINUS -> 4
            PLUS -> 3
            ANGLE -> 3
            BAR -> 1
            SQUARE -> 2
        }

        companion object {
            fun Rock.toPoints(): List<Point> {
                return when (this) {
                    MINUS -> listOf(
                        Point(0, 0),
                        Point(1, 0),
                        Point(2, 0),
                        Point(3, 0),
                    )

                    PLUS -> listOf(
                        Point(1, 0),
                        Point(0, 1),
                        Point(1, 1),
                        Point(2, 1),
                        Point(1, 2),
                    )

                    ANGLE -> listOf(
                        Point(0, 0),
                        Point(1, 0),
                        Point(2, 0),
                        Point(2, 1),
                        Point(2, 2),
                    )

                    BAR -> listOf(
                        Point(0, 0),
                        Point(0, 1),
                        Point(0, 2),
                        Point(0, 3),
                    )

                    SQUARE -> listOf(
                        Point(0, 0),
                        Point(1, 0),
                        Point(0, 1),
                        Point(1, 1),
                    )
                }
            }

            fun Rock.toPoints(bottomLeft: Point) = toPoints().withOffset(bottomLeft)

            fun List<Point>.withOffset(bottomLeft: Point) =
                this.map { Point(it.x + bottomLeft.x, it.y + bottomLeft.y) }

            fun List<Point>.move(dir: Point, magnitude: Int = 1) = this.map { it + (dir * magnitude) }

            fun List<Point>.getBottomLeft() = Point(this.minX().x, this.minY().y)
        }
    }

    class Chamber {
        private val rocks = mutableMapOf<IntRange, MutableSet<Point>>()
        private val rangeSize = 20
        var height = -1
        var rocksAdded = 0L

        fun addRock(rock: Rock, getNextMove: () -> Move) {
            val currBottomLeft = Point(2, height + 4)
            var currRock = rock.toPoints(currBottomLeft)

            while (true) {
                // Jet of gas pushes rock
                currRock = when (getNextMove()) {
                    Move.LEFT -> {
                        moveIfPossible(currRock, Point.LEFT) ?: currRock
                    }

                    Move.RIGHT -> {
                        moveIfPossible(currRock, Point.RIGHT) ?: currRock
                    }
                }

                // Move rock down if possible
                val downMove = moveIfPossible(currRock, Point.DOWN)
                if (downMove == null) {
                    // Cannot move down. Rock is stopped
                    addRockToChamber(currRock)
                    rocksAdded++
                    break
                } else {
                    currRock = downMove
                }
            }
        }

        private fun addRockToChamber(rock: List<Point>) {
            rock.maxY().y.let { newHeight ->
                if (newHeight > height) height = newHeight
            }
            rock.map { getYRange(it.y) to it }
                .forEach { (range, point) ->
                    getRocksForRange(range).add(point)
                }
        }

        fun contains(point: Point) = getRocksForRange(getYRange(point.y)).contains(point)

        private fun intersectsWithRocks(rock: List<Point>, minY: Int, maxY: Int): Boolean {
            val r1 = getYRange(minY)
            val r2 = getYRange(maxY)
            return (getRocksForRange(r1) + getRocksForRange(r2)).intersect(rock.toSet()).isNotEmpty()
        }

        fun getSkyline(size: Int): List<Point> {
            val startY = height - size

            return (startY..height)
                .asSequence()
                .map { getYRange(it) }
                .toSet()
                .map { getRocksForRange(it) }
                .flatten()
                .filter { it.y >= (startY) }
                .map { it.copy(yOverride = it.y - startY) }
        }

        private fun getRocksForRange(range: IntRange) = rocks.getOrPut(range) { mutableSetOf() }

        private fun getYRange(y: Int): IntRange {
            val rangeStart = y - (y % rangeSize)
            return (rangeStart until rangeStart + rangeSize)
        }

        private fun moveIfPossible(currPos: List<Point>, dir: Point): List<Point>? {
            val nextMove = currPos.move(dir)
            val leftmost = nextMove.minX().x
            val rightmost = nextMove.maxX().x
            val downmost = nextMove.minY().y
            val upmost = nextMove.maxY().y
            val isInBounds = leftmost >= 0 && rightmost <= 6 && downmost >= 0
            val doesNotIntersectsWithStoppedRocks = !intersectsWithRocks(nextMove, downmost, upmost)
            return if (isInBounds && doesNotIntersectsWithStoppedRocks) {
                nextMove
            } else null
        }

        fun printGrid(atHeight: Int) {
            val result = (atHeight - 20..atHeight + 2).map { y ->
                (0..6).map { x ->
                    val p = Point(x, y)
                    when {
                        contains(p) -> '#'
                        else -> '.'
                    }
                }
            }.mapIndexed { index, chars -> "%03d ".format(index) + chars.joinToString("", prefix = "|", postfix = "|") }
                .reversed()
                .joinToString("\n")
            println("$result\n    +-------+\n")
        }

        fun clearSome() {
            val start = height - 100_000
            (0 until 2500).forEach {
                rocks.remove(20*it until 20*it + 20)
            }
        }
    }
}