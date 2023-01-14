import kotlin.math.abs
import kotlin.math.sign

data class Point(val x: Int, val y: Int) {

    fun getNeighbors(isInBounds: (Point) -> Boolean): List<Point> {
        return listOf(
            Point(x - 1, y),
            Point(x + 1, y),
            Point(x, y - 1),
            Point(x, y + 1),
        ).filter { isInBounds(it) }
    }

    fun getNeighbors(): List<Point> {
        return listOfNotNull(
            Point(x - 1, y),
            Point(x + 1, y),
            Point(x, y - 1),
            Point(x, y + 1),
        )
    }

    fun getUnitVectorTo(other: Point) = Point((other.x - x).sign, (other.y - y).sign)

    fun copy(xOverride: Int? = null, yOverride: Int? = null): Point {
        return Point(xOverride ?: x, yOverride ?: y)
    }

    fun absDistanceFrom(other: Point) = abs(other.x - x) + abs(other.y - y)

    override fun toString(): String {
        return "($x, $y)"
    }

    companion object {
        fun Collection<Point>.minX() = this.minBy { it.x }
        fun Collection<Point>.minY() = this.minBy { it.y }
        fun Collection<Point>.maxX() = this.maxBy { it.x }
        fun Collection<Point>.maxY() = this.maxBy { it.y }

        fun getBoundsFunction(xRange: IntRange, yRange: IntRange): (Point) -> Boolean {
            return { p: Point -> xRange.contains(p.x) && yRange.contains(p.y) }
        }
    }
}

class Bounds<T>(grid: List<List<T>>) {
    private val xBounds = grid.indices
    private val yBounds = grid[0].indices

    fun isInBounds(point: Point) = xBounds.contains(point.x) && yBounds.contains(point.y)
}

class Node<T, R>(
    var name: T,
    var value: R? = null,
    val parents: MutableList<Node<T, R>> = emptyList<Node<T, R>>().toMutableList(),
    val children: MutableList<Node<T, R>> = emptyList<Node<T, R>>().toMutableList(),
) {

    override fun toString(): String {
        return "Node($name, $value, ${parents.map { it.name }}, ${children.map { it.name }})"
    }

}

