package puzzles

import Puzzle

class Y22D03(name: String, input: String, testInput: String) : Puzzle(name, input, testInput) {
    override fun part1(): String {
        val rucksacks = input()
            .map {
                it.take(it.length / 2 ).toSet().intersect(it.takeLast(it.length / 2).toSet()).first()
            }.map { it.toPriority() }

        return rucksacks.sum().toString()
    }

    override fun part2(): String {
        val badges = input().chunked(3).map { (f, s, t)  ->
            f.toSet().intersect(s.toSet().intersect(t.toSet())).first().toPriority()
        }

        return badges.sum().toString()
    }

    private fun Char.toPriority(): Int = when {
        this < 'a' -> 27 + (this - 'A')
        else -> 1 + (this - 'a')
    }
}