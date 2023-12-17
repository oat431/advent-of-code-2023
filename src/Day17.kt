import java.util.*
import kotlin.math.min

data class SeenVertex<K>(val cost: Int, val prev: K?)
class GraphSearchResult<K>(val start: K, private val end: K?, private val result: Map<K, SeenVertex<K>>) {
    private fun getScore(vertex: K) = result[vertex]?.cost ?: throw IllegalStateException("Result for $vertex not available")
    fun getScore() = end?.let { getScore(it) } ?: throw IllegalStateException("No path found")

}

data class ScoredVertex<K>(val vertex: K, val score: Int, val heuristic: Int) : Comparable<ScoredVertex<K>> {
    override fun compareTo(other: ScoredVertex<K>): Int = (score + heuristic).compareTo(other.score + other.heuristic)
}

data class Points(val x: Int, val y: Int) {

    operator fun minus(other: Points): Points = Points(x - other.x, y - other.y)

    operator fun plus(other: Points) = Points(x + other.x, y + other.y)
    operator fun times(value: Int) = Points(x * value, y * value)

    companion object {
    }
}

data class PointInDirection(val point: Points, val direction: Directions, val line: Int) {
    fun neighbours(): List<PointInDirection> {
        return buildList {
            if (line < 3) {
                add(PointInDirection(point + direction.pointPositiveDown, direction, line + 1))
            }
            add(PointInDirection(point + direction.right.pointPositiveDown, direction.right, 1))
            add(PointInDirection(point + direction.left.pointPositiveDown, direction.left, 1))
        }
    }

    fun ultraNeighbours(): List<PointInDirection> {
        return buildList {
            if (line < 10) {
                add(PointInDirection(point + direction.pointPositiveDown, direction, line + 1))
            }
            if (line >= 4) {
                add(PointInDirection(point + direction.right.pointPositiveDown, direction.right, 1))
                add(PointInDirection(point + direction.left.pointPositiveDown, direction.left, 1))
            }
        }
    }
}

enum class Orientation {
    HORIZONTAL,
    VERTICAL
}

enum class Directions(private val char: Char, val orientation: Orientation) {

    UP('^', Orientation.VERTICAL) {
        override val left get() = LEFT
        override val right get() = RIGHT
        override val opposite get() = DOWN
    },
    RIGHT('>', Orientation.HORIZONTAL) {
        override val left get() = UP
        override val right get() = DOWN
        override val opposite get() = LEFT
    },
    DOWN('v', Orientation.VERTICAL) {
        override val left get() = RIGHT
        override val right get() = LEFT
        override val opposite get() = UP
    },
    LEFT('<', Orientation.HORIZONTAL) {
        override val left get() = DOWN
        override val right get() = UP
        override val opposite get() = RIGHT
    };

    abstract val left: Directions
    abstract val right: Directions
    abstract val opposite: Directions

    val pointPositiveDown: Points
        get() = DirectionPointMapping.downPositive[this]

    val pointPositiveUp: Points
        get() = DirectionPointMapping.upPositive[this]

    override fun toString(): String = "$char"

    companion object {
        val NORTH = UP
        val EAST = RIGHT
        val SOUTH = DOWN
        val WEST = LEFT
    }
}

class DirectionPointMapping(positiveY: Directions, positiveX: Directions) {

    private val up = if (positiveY == Directions.UP) Points(0, 1) else Points(0, -1)
    private val down = Points(0, -up.y)
    private val left = if (positiveX == Directions.LEFT) Points(1, 0) else Points(-1, 0)
    private val right = Points(-left.x, 0)

    operator fun get(direction: Directions): Points {
        return when (direction) {
            Directions.UP -> up
            Directions.DOWN -> down
            Directions.LEFT -> left
            Directions.RIGHT -> right
        }
    }

    companion object {
        val downPositive = DirectionPointMapping(Directions.DOWN, Directions.RIGHT)
        val upPositive = DirectionPointMapping(Directions.UP, Directions.RIGHT)
    }
}

typealias NeighbourFunction<K> = (K) -> Iterable<K>
typealias CostFunction<K> = (K, K) -> Int
typealias HeuristicFunction<K> = (K) -> Int
fun main() {
    fun part1(input : String) : Int {
        val map = input.lines().map { line -> line.map { it.digitToInt() } }
        val start = PointInDirection(Points(0, 0), Directions.RIGHT, 0)
        val end = Points(map[0].lastIndex, map.lastIndex)

        val path = findShortestPathByPredicate(
            start,
            { (p, _) -> p == end },
            { it.neighbours().filter { (n) -> n in map } },
            { _, (point) -> map[point] })
        return path.getScore()
    }

    fun part2(input : String) : Int {
        val map = input.lines().map { line -> line.map { it.digitToInt() } }
        val pathA = searchWithStartDirection(map, Directions.DOWN)
        val pathB = searchWithStartDirection(map,  Directions.RIGHT)
        return min(pathA.getScore(), pathB.getScore())
    }

    val input = readText("input_day_17")
    part1(input).println()
    part2(input).println()
}
fun <K> findShortestPathByPredicate(
    start: K,
    endFunction: (K) -> Boolean,
    neighbours: NeighbourFunction<K>,
    cost: CostFunction<K> = { _, _ -> 1 },
    heuristic: HeuristicFunction<K> = { 0 }
): GraphSearchResult<K> {
    val toVisit = PriorityQueue(listOf(ScoredVertex(start, 0, heuristic(start))))
    var endVertex: K? = null
    val seenPoints: MutableMap<K, SeenVertex<K>> = mutableMapOf(start to SeenVertex(0, null))

    while (endVertex == null) {
        if (toVisit.isEmpty()) {
            return GraphSearchResult(start, null, seenPoints)
        }

        val (currentVertex, currentScore) = toVisit.remove()
        endVertex = if (endFunction(currentVertex)) currentVertex else null

        val nextPoints = neighbours(currentVertex)
            .filter { it !in seenPoints }
            .map { next -> ScoredVertex(next, currentScore + cost(currentVertex, next), heuristic(next)) }

        toVisit.addAll(nextPoints)
        seenPoints.putAll(nextPoints.associate { it.vertex to SeenVertex(it.score, currentVertex) })
    }

    return GraphSearchResult(start, endVertex, seenPoints)
}
private fun searchWithStartDirection(
    map: List<List<Int>>,
    startDirection: Directions
): GraphSearchResult<PointInDirection> {
    val start = PointInDirection(Points(0, 0), startDirection, 0)
    val end = Points(map[0].lastIndex, map.lastIndex)
    return findShortestPathByPredicate(
        start,
        { (p, _, line) -> p == end && line >= 4 },
        { it.ultraNeighbours().filter { (p) -> p in map } },
        { _, (point) -> map[point] }
    )
}


@JvmName("strGet")
operator fun List<String>.get(point: Points): Char = this[point.y][point.x]
@JvmName("strContains")
operator fun Collection<String>.contains(point: Points): Boolean =
    this.isNotEmpty() && point.y in this.indices && point.x in this.first().indices


operator fun <E> List<List<E>>.get(point: Points) = this[point.y][point.x]
operator fun <E> List<MutableList<E>>.set(point: Points, value: E) {
    this[point.y][point.x] = value
}
operator fun <E> Collection<Collection<E>>.contains(point: Points): Boolean =
    this.isNotEmpty() && point.y in this.indices && point.x in this.first().indices

operator fun <E> Array<Array<E>>.get(point: Points) = this[point.y][point.x]
operator fun <E> Array<Array<E>>.set(point: Points, value: E) {
    this[point.y][point.x] = value
}
operator fun <E> Array<Array<E>>.contains(point: Points): Boolean = this.isNotEmpty() && point.y in this.indices && point.x in this.first().indices

