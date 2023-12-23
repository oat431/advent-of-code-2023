private lateinit var grid: Array<Array<Char>>
private val visited = mutableSetOf<Day23Point>()
private var max = 0
data class Day23Point(var x: Int, var y: Int) {
    operator fun plus(other: Day23Point) = Day23Point(x + other.x, y + other.y)
    operator fun plus(other: Day23Direction) = this + other.toDay23Point()
}
enum class Day23Direction(private val angle: Int) {
    NORTH(0), EAST(90), SOUTH(180), WEST(270);
    operator fun component1() = this.toDay23Point().x
    operator fun component2() = this.toDay23Point().y

    fun toDay23Point() = when (this) {
        NORTH -> Day23Point(-1, 0)
        EAST -> Day23Point(0, 1)
        SOUTH -> Day23Point(1, 0)
        WEST -> Day23Point(0, -1)
    }
    companion object {
        fun of(c: Char): Day23Direction {
            return when (c.uppercaseChar()) {
                'N', 'U', '^', '3' -> NORTH
                'E', 'R', '>', '0' -> EAST
                'S', 'D', 'V', '1' -> SOUTH
                'W', 'L', '<', '2' -> WEST
                else -> throw IllegalArgumentException("Invalid rotation: $c")
            }
        }
    }
}
fun main() {
    fun part1(input: Array<Array<Char>>) : Int {
        grid = input
        dfs(Day23Point(0, grid[0].indexOf('.')), Day23Point(grid.size - 1, grid.last().lastIndexOf('.')))
        return max
    }

    fun part2(input: Array<Array<Char>>) : Int {
        grid = input
        val start = Day23Point(0, grid[0].indexOf('.'))
        val end = Day23Point(grid.size - 1, grid.last().indexOf('.'))
        return dfsOptimized(makeAdjacencies(grid), start, end, mutableMapOf())!!
    }

    val input = readInput("input_day_23").map { it.toCharArray().toTypedArray() }.toTypedArray()
    part1(input).println()
    part2(input).println()
}

private fun dfs(cur: Day23Point, end: Day23Point, steps: Int = 0) {
    if (cur == end) {
        max = maxOf(max, steps)
        return
    }
    visited.add(cur)
    getNextPoints(cur).filter { it !in visited }.forEach { dfs(it, end, steps + 1) }
    visited.remove(cur)
}

private fun makeAdjacencies(grid: Array<Array<Char>>): Map<Day23Point, Map<Day23Point, Int>> {
    val adjacencies = grid.indices.flatMap { x ->
        grid[0].indices.mapNotNull { y ->
            if (grid[x][y] != '#') {
                Day23Point(x, y) to Day23Direction.entries.mapNotNull { (dx, dy) ->
                    val nx = x + dx
                    val ny = y + dy
                    if (nx in grid.indices && ny in grid[0].indices && grid[nx][ny] != '#') Day23Point(nx, ny) to 1 else null
                }.toMap(mutableMapOf())
            } else null
        }
    }.toMap(mutableMapOf())

    adjacencies.keys.toList().forEach { key ->
        adjacencies[key]?.takeIf { it.size == 2 }?.let { neighbors ->
            val left = neighbors.keys.first()
            val right = neighbors.keys.last()
            val totalSteps = neighbors[left]!! + neighbors[right]!!
            adjacencies.getOrPut(left) { mutableMapOf() }.merge(right, totalSteps, ::maxOf)
            adjacencies.getOrPut(right) { mutableMapOf() }.merge(left, totalSteps, ::maxOf)
            listOf(left, right).forEach { adjacencies[it]?.remove(key) }
            adjacencies.remove(key)
        }
    }
    return adjacencies
}

private fun dfsOptimized(graph: Map<Day23Point, Map<Day23Point, Int>>, cur: Day23Point, end: Day23Point, seen: MutableMap<Day23Point, Int>): Int? {
    if (cur == end) {
        return seen.values.sum()
    }

    var best: Int? = null
    (graph[cur] ?: emptyMap()).forEach { (neighbor, steps) ->
        if (neighbor !in seen) {
            seen[neighbor] = steps
            val res = dfsOptimized(graph, neighbor, end, seen)
            if (best == null || (res != null && res > best!!)) {
                best = res
            }
            seen.remove(neighbor)
        }
    }
    return best
}

private fun getNextPoints(point: Day23Point): List<Day23Point> {
    val cur = grid[point.x][point.y]
    return Day23Direction.entries.mapNotNull { dir ->
        val p = point + dir
        if (p.x in grid.indices && p.y in grid[0].indices && grid[p.x][p.y] != '#' && (cur == '.' || Day23Direction.of(cur) == dir)) p else null
    }
}