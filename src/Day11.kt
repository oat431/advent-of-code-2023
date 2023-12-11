import kotlin.math.abs

private const val EMPTY_SPACE : Char = '.'
private const val GALAXY : Char = '#'
private typealias Coord = Pair<Long, Long>

fun main() {
    fun part1(input: String) : Long {
        return parse(input, 2)
            .uniquePairs()
            .fold(0L) { acc, pair -> acc + manhattanDistance(pair.first, pair.second) }
    }

    fun part2(input: String) : Long {
        return parse(input, 1000000)
            .uniquePairs()
            .fold(0L) { acc, pair -> acc + manhattanDistance(pair.first, pair.second) }
    }

    val input = readText("input_day_11")
    part1(input).println()
    part2(input).println()
}

private fun manhattanDistance(c1: Coord, c2: Coord): Long = abs(c1.first - c2.first) + abs(c1.second - c2.second)

private fun calculateAdjustments(data: List<String>, emptyValue: Long): List<Long> {
    val emptyIndices = data
        .withIndex()
        .filter { (_, lst) -> lst.all { it == EMPTY_SPACE } }
        .map(IndexedValue<String>::index)
        .toSet()
    return data.indices.fold(listOf(0L)) { acc, index ->
        acc + (acc.last() + if (index in emptyIndices) emptyValue - 1 else 0)
    }.drop(1)
}

private fun parse(input: String, emptyValue: Long = 2): List<Coord> {
    val rows = input.trim().lines()
    val cols = (0..<rows.first().length).map { colIdx ->
        rows.joinToString(separator = "") { it[colIdx].toString() }
    }

    val rowAdjustments = calculateAdjustments(rows, emptyValue)
    val colAdjustments = calculateAdjustments(cols, emptyValue)

    return rows.withIndex().flatMap { (rIdx, row) ->
        row.withIndex().mapNotNull { (cIdx, ch) -> if (ch == GALAXY) (rIdx to cIdx) else null }
    }.map {
        val (rIdx, cIdx) = it
        rIdx.toLong() + rowAdjustments[rIdx] to cIdx.toLong() + colAdjustments[cIdx]
    }
}

private fun <T> List<T>.uniquePairs(): List<Pair<T, T>> =
    indices.flatMap { i ->
        ((i + 1)..<size).map { j ->
            this[i] to this[j]
        }
    }