class Y22D20 : Puzzle(22, 20) {
    override fun part1(): String {
        val nums = input.mapIndexed { index, s -> P(index, s.toLong()) }.toMutableList()
        val result = nums.decrypt().getCoordinate()
        return result.toString()
    }

    override fun part2(): String {
        val decryptionKey = 811589153L
        val nums = input.mapIndexed { index, s -> P(index, s.toLong() * decryptionKey) }.toMutableList()
        repeat(10) { nums.decrypt() }
        val result = nums.getCoordinate()
        return result.toString()
    }

    private fun MutableList<P>.decrypt(): MutableList<P> {
        indices.forEach { originalIndex ->
            val currentIndex = indexOfFirst { it.originalIndex == originalIndex }
            val p = removeAt(currentIndex)
            add((currentIndex + p.value).mod(size), p)
        }
        return this
    }

    private fun MutableList<P>.getCoordinate(): Long {
        val zeroIndex = indexOfFirst { it.value == 0L }
        return listOf(1_000, 2_000, 3_000)
            .sumOf { this[(zeroIndex + it) % size].value }
    }

    class P(var originalIndex: Int, val value: Long) {
        override fun toString(): String {
            return "$value"
        }
    }
}