class Y22D12 : Puzzle(22, 12) {
    override fun part1(): String {
        var startCoord: Coord? = null
        var endCoord: Coord? = null
        val grid = input.mapIndexed { x, line ->
            line.mapIndexed { y, char ->
                when (char) {
                    'S' -> {
                        startCoord = Coord(x, y)
                        0
                    }
                    'E' -> {
                        endCoord = Coord(x, y)
                        25
                    }
                    else -> char - 'a'
                }
            }
        }

        val xBounds = grid.indices
        val yBounds = grid[0].indices

        val coordList = grid.mapIndexed { x, line ->
            line.mapIndexed { y, item -> Coord(x, y) to item }
        }.flatten()

        val adj = mutableMapOf<Coord, List<Coord>>()
        coordList.map { (coord, item) ->
            adj[coord] = coord.getNeighbors(xBounds, yBounds).filter {
                grid.get(it) <= (item + 1)
            }
        }

        val distanceChart = dijkstra(startCoord!!, adj)
        val result = distanceChart[endCoord]

        return result.toString()
    }

    override fun part2(): String {
        var endCoord: Coord? = null
        val grid = input.mapIndexed { x, line ->
            line.mapIndexed { y, char ->
                when (char) {
                    'S' -> {
                        0
                    }
                    'E' -> {
                        endCoord = Coord(x, y)
                        25
                    }
                    else -> char - 'a'
                }
            }
        }

        val xBounds = grid.indices
        val yBounds = grid[0].indices

        val coordList = grid.mapIndexed { x, line ->
            line.mapIndexed { y, item -> Coord(x, y) to item }
        }.flatten()

        val adj = mutableMapOf<Coord, List<Coord>>()
        coordList.map { (coord, item) ->
            adj[coord] = coord.getNeighbors(xBounds, yBounds).filter {
                grid.get(it) >= (item - 1)
            }
        }

        val startCandidates = coordList.filter { (_, value) -> value == 0 }.map { it.first }

        val distanceChart = dijkstra(endCoord!!, adj)
        val targetStartCoord = startCandidates.minBy { distanceChart[it] ?: Int.MAX_VALUE }
        val result = distanceChart[targetStartCoord]

        return result.toString()
    }

    data class Coord(val x: Int, val y: Int) {
        fun getNeighbors(xBounds: IntRange, yBounds: IntRange): List<Coord> {
            return listOfNotNull(
                Coord(x - 1, y).takeUnless { !it.inBounds(xBounds, yBounds) },
                Coord(x + 1, y).takeUnless { !it.inBounds(xBounds, yBounds) },
                Coord(x, y - 1).takeUnless { !it.inBounds(xBounds, yBounds) },
                Coord(x, y + 1).takeUnless { !it.inBounds(xBounds, yBounds) },
            )
        }

        private fun inBounds(xBounds: IntRange, yBounds: IntRange): Boolean {
            return xBounds.contains(x) && yBounds.contains(y)
        }
    }

    fun List<List<Int>>.get(coord: Coord) = this[coord.x][coord.y]

    private fun dijkstra(root: Coord, adj: Map<Coord, List<Coord>>): Map<Coord, Int> {
        val queue = ArrayDeque<Pair<Coord, Int>>()
        queue.add(root to 0)

        val distanceChart = mutableMapOf<Coord, Int>()

        while (queue.isNotEmpty()) {
            val node = queue.minBy { it.second }
            queue.remove(node)
            val coord = node.first
            val distance = node.second

            if (distance > (distanceChart[coord] ?: Int.MAX_VALUE)) {
                continue
            }

            adj[coord]?.forEach { neighbor ->
                val newDistance = distance + 1
                if ((distanceChart[neighbor] ?: Int.MAX_VALUE) > newDistance) {
                    distanceChart[neighbor] = newDistance
                    queue.add(neighbor to newDistance)
                }
            }
        }

        return distanceChart
    }

}