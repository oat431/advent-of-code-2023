import kotlin.math.absoluteValue

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = copy(x = x + other.x, y = y + other.y)

    operator fun plus(other: Direction) = plus(other.move)

    fun distanceTo(other: Point) = (x - other.x).absoluteValue + (y - other.y).absoluteValue

    fun neighbours() = setOf(
        copy(y = y - 1),
        copy(x = x + 1, y = y - 1),
        copy(x = x + 1),
        copy(x = x + 1, y = y + 1),
        copy(y = y + 1),
        copy(x = x - 1, y = y + 1),
        copy(x = x - 1),
        copy(x = x - 1, y = y - 1),
    )

    fun orthogonalNeighbours() = setOf(
        copy(x = x - 1),
        copy(x = x + 1),
        copy(y = y - 1),
        copy(y = y + 1),
    )
}


enum class Direction(val move: Point) {
    UP(move = Point(0, -1)),
    DOWN(move = Point(0, 1)),
    LEFT(move = Point(-1, 0)),
    RIGHT(move = Point(1, 0));

    companion object
}

typealias DirectedPoint = Pair<Point, Direction>

fun main() {
    fun part1(solution : Solution) : Int {
        return solution.getBeamVisits(Point(0,0) to Direction.RIGHT)
    }

    fun part2(solution : Solution) : Int {
        return listOf(
            (0..<solution.getX()).maxOf { x -> solution.getBeamVisits(Point(x, 0) to Direction.DOWN) },
            (0..<solution.getX()).maxOf { x -> solution.getBeamVisits(Point(x, solution.getY() - 1) to Direction.UP) },
            (0..<solution.getY()).maxOf { y -> solution.getBeamVisits(Point(0, y) to Direction.LEFT) },
            (0..<solution.getY()).maxOf { y -> solution.getBeamVisits(Point(solution.getX() - 1, y) to Direction.RIGHT) },
        ).max()
    }

    val input = readInput("input_day_16")
    val solution = Solution(input)
    part1(solution).println()
    part2(solution).println()

}

private class Solution(private val input: List<String>) {
    val DirectedPoint.state get() = value to second
    val DirectedPoint.value get() = input[first.y][first.x]
    val DirectedPoint.valid get() = first.x in 0..<maxX && first.y in 0..<maxY
    private val maxY = input.size
    private val maxX = input.first().length
    private operator fun DirectedPoint.plus(direction: Direction) = (first + direction) to direction

    @Suppress("CyclomaticComplexMethod")
    fun getBeamVisits(start: DirectedPoint): Int {
        val queue = ArrayDeque<DirectedPoint>().apply { add(start) }
        val visited = hashSetOf<DirectedPoint>()

        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()

            if (!node.valid || node in visited) continue

            when (node.state) {
                '|' to Direction.RIGHT, '|' to Direction.LEFT -> listOf(Direction.UP, Direction.DOWN)
                '-' to Direction.UP, '-' to Direction.DOWN -> listOf(Direction.LEFT, Direction.RIGHT)
                '/' to Direction.RIGHT -> listOf(Direction.UP)
                '/' to Direction.LEFT -> listOf(Direction.DOWN)
                '/' to Direction.UP -> listOf(Direction.RIGHT)
                '/' to Direction.DOWN -> listOf(Direction.LEFT)
                '\\' to Direction.RIGHT -> listOf(Direction.DOWN)
                '\\' to Direction.LEFT -> listOf(Direction.UP)
                '\\' to Direction.UP -> listOf(Direction.LEFT)
                '\\' to Direction.DOWN -> listOf(Direction.RIGHT)
                else -> listOf(node.second)
            }.forEach { dir ->
                queue.add(node + dir)
            }.also {
                visited.add(node)
            }
        }
        return visited.distinctBy { it.first }.size
    }

    fun getX() : Int {
        return maxX
    }

    fun getY() : Int {
        return maxY
    }
}
