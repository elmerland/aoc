package puzzles

import Puzzle
import puzzles.Y22D02.Move.Companion.getPlay
import puzzles.Y22D02.Move.Companion.play
import puzzles.Y22D02.Move.Companion.toMove
import java.lang.IllegalArgumentException

class Y22D02(name: String, input: String, testInput: String) : Puzzle(name, input, testInput) {
    /**
     * A rock
     * B paper
     * C scissors
     *
     * X rock
     * Y paper
     * Z scissors
     *
     * rock 1
     * paper 2
     * scissors 3
     *
     * lost 0
     * draw 3
     * win 6
     *
     * X lose
     * Y draw
     * Z win
     */

    enum class Move(val value: Int) {
        R(1), P(2), S(3), ;

        companion object {
            val wins = setOf(R to S, P to R, S to P)
            val loose = setOf(R to P, P to S, S to R)
            fun String.toMove(): Move = when (this) {
                "A", "X" -> R
                "B", "Y" -> P
                "C", "Z" -> S
                else -> throw IllegalArgumentException(this)
            }

            fun Pair<Move, Move>.play(): Int = when {
                first == second -> 3 // draw
                wins.contains(this) -> 0 // opponent wins
                else -> 6 // player wins
            }

            fun getPlay(opponent: Move, result: String): Pair<Move, Int> = when (result) {
                "X" -> wins.find { it.first == opponent }!!.second to 0 // lose
                "Y" -> opponent to 3 // draw
                "Z" -> wins.find { it.second == opponent }!!.first to 6 // win
                else -> throw IllegalArgumentException((opponent to result).toString())
            }
        }
    }

    override fun part1(): String {

        val rounds = input()
            .map { it.split(" ").let { (m1, m2) -> m1.toMove() to m2.toMove() } }
            .map { it.play() + it.second.value }

        return rounds.sum().toString()
    }

    override fun part2(): String {
        val rounds = input()
            .map { it.split(" ").let { (m1, result) -> getPlay(m1.toMove(), result) } }
            .map { it.first.value + it.second }

        return rounds.sum().toString()
    }
}