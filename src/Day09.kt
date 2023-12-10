fun main() {
    fun part1(input : List<List<Int>>) : Int {
        return input.sumOf { it.extrapolate() }
    }

    fun part2(input : List<List<Int>>) : Int {
        return input.sumOf { it.reversed().extrapolate() }
    }

    val input = readInput("input_day_9").map { it.split(" ").map { it.toInt() } }
    part1(input).println()
    part2(input).println()
}

fun List<Int>.extrapolate(): Int = if (all { it == 0 }) 0 else {
    last() + windowed(2) { it.last() - it.first() }.extrapolate()
}