package puzzles

import Point3D
import Puzzle

class Y22D18(name: String, input: String, testInput: String) : Puzzle(name, input, testInput) {
    override fun part1(): String {
        val cubes = input().map { it.split(",").map { num -> num.toInt() } }
            .map { Point3D(it[0], it[1], it[2]) }
            .toSet()

        val groups = findCubeClusters(cubes)

        val sumOfSides = groups.sumOf { cubes ->
            cubes.sumOf { 6 - it.getNeighbors().intersect(cubes).size }
        }

        return sumOfSides.toString()
    }

    override fun part2(): String {
        val cubes = input().map { it.split(",").map { num -> num.toInt() } }
            .map { Point3D(it[0], it[1], it[2]) }
            .toSet()

        val groups = findCubeClusters(cubes)
        val edgeClusters = findCubeClusters(generateEdges(cubes))

        val pockets = if (edgeClusters.size > 1) {
            val outsideEdge = edgeClusters.maxBy { it.size }
            (edgeClusters - setOf(outsideEdge)).flatten().toSet()
        } else emptySet()

        val sumOfSides = groups.sumOf { cubes ->
            cubes.sumOf {
                val neighbors = it.getNeighbors()
                6 - neighbors.intersect(cubes).size - neighbors.intersect(pockets).size
            }
        }

        return sumOfSides.toString()
    }

    private fun generateEdges(cubes: Set<Point3D>): Set<Point3D> {

        fun coordRange(selector: (Point3D) -> Int): IntRange =
            cubes.minOf { selector(it) } - 1..cubes.maxOf { selector(it) } + 1
        val xRange =  coordRange { it.x }
        val yRange =  coordRange { it.y }
        val zRange =  coordRange { it.z }

        val superCube =
            xRange.map { x -> yRange.map { y -> zRange.map { z -> Point3D(x, y, z) } }.flatten() }.flatten().toSet()

        return superCube - cubes
    }

    fun printCubes(cubes: Set<Point3D>) {
        fun coordRange(selector: (Point3D) -> Int): IntRange =
            cubes.minOf { selector(it) }..cubes.maxOf { selector(it) }
        val zRange =  coordRange { it.z }

        val result = zRange.joinToString("\n") { z ->
            "z=$z\n" + (0..4).joinToString("\n") { x ->
                (0..4).reversed().joinToString("") { y ->
                    val p = Point3D(x, y, z)
                    when {
                        cubes.contains(p) -> "#"
                        else -> "."
                    }
                }
            }
        }
        println(result)
    }

    private fun findCubeClusters(cubes: Set<Point3D>): Set<Set<Point3D>> {
        val touchingCubes = mutableMapOf<Point3D, Set<Point3D>>()

        cubes.forEach {
            touchingCubes[it] = it.getNeighbors().intersect(cubes) + it
        }

        val groups = mutableSetOf<MutableSet<Point3D>>()

        touchingCubes.forEach { (_, cubes) ->
            val intersectingGroups = groups.filter { it.intersect(cubes).isNotEmpty() }
            if (intersectingGroups.isEmpty()) {
                groups.add(cubes as MutableSet<Point3D>)
            } else {
                if (intersectingGroups.size > 1) {
                    groups.removeIf { intersectingGroups.contains(it) }
                    groups.add(intersectingGroups.flatten().toMutableSet())
                } else {
                    intersectingGroups[0].addAll(cubes)
                }
            }
        }

        return groups
    }
}