import kotlin.math.absoluteValue

typealias Day18Point = Pair<Int, Int>
val EAST = Day18Point(0, 1)
val WEST = Day18Point(0, -1)
val NORTH = Day18Point(-1, 0)
val SOUTH = Day18Point(1, 0)

enum class Dir(val point: Day18Point) { R(EAST), D(SOUTH), L(WEST), U(NORTH) }
fun main() {
    fun part1(input : Sequence<Triple<Dir, Int, Int>>) : Long {
        return input.map { (a, b, _) -> a to b }.solve()
    }

    fun part2(input : Sequence<Triple<Dir, Int, Int>>) : Long {
        return input.map { (_, _, c) -> Dir.entries[c % 16] to c / 16 }.solve()
    }

    val input = readText("input_day_18").lineSequence().map {
        line ->
        line.split(" ").let { (a, b, c) ->
            Triple(Dir.valueOf(a), b.toInt(), c.substring(2..7).toInt(16))
        }
    }
    part1(input).println()
    part2(input).println()
}

fun Sequence<Pair<Dir, Int>>.solve() =
        runningFold(0 to 0) { point, (dir, amount) -> point + dir.point * amount }
                .zipWithNext { (y1, x1), (_, x2) -> (x2 - x1) * y1.toLong() }
                .sum().absoluteValue + sumOf { it.second } / 2 + 1

operator fun Day18Point.plus(other: Day18Point) = first + other.first to second + other.second
operator fun Day18Point.times(other: Int) = first * other to second * other
