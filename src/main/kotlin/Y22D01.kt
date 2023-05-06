class Y22D01(name: String, input: String, testInput: String) : Puzzle(name, input, testInput) {
    override fun part1(): String {
        val backpacks = input()
            .map { it.toIntOrNull() }
            .fold(mutableListOf(mutableListOf<Int>())) { acc, calories ->
                calories?.let { acc.last().add(it) } ?: acc.add(mutableListOf())
                acc
            }

        return backpacks.maxOfOrNull { it.sum() }.toString()
    }

    override fun part2(): String {
        val backpacks = input()
            .map { it.toIntOrNull() }
            .fold(mutableListOf(mutableListOf<Int>())) { acc, calories ->
                calories?.let { acc.last().add(it) } ?: acc.add(mutableListOf())
                acc
            }

        return backpacks.map { it.sum() }.sortedDescending().take(3).sum().toString()
    }
}