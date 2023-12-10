fun main() {
    fun part1(input : List<String>) : Int {
        val steps = input.first()
        val map = input.drop(2).associate { line ->
            val (from, left, right) = """([A-Z]{3}) = \(([A-Z]{3}), ([A-Z]{3})\)""".toRegex().matchEntire(line)!!.groupValues.drop(1)
            from to listOf(left, right)
        }
        var current = "AAA"
        var count = 0
        while (current != "ZZZ") {
            steps.forEach { current = if (it == 'R') map[current]!![1] else map[current]!![0] }
            count += steps.length
        }
        return count
    }

    fun part2(input : List<String>) : Long {
        val steps = input.first()
        val map = input.drop(2).associate { line ->
            val (from, left, right) = """([A-Z]{3}) = \(([A-Z]{3}), ([A-Z]{3})\)""".toRegex().matchEntire(line)!!.groupValues.drop(1)
            from to listOf(left, right)
        }
        val counts = map.keys.filter { it.endsWith("A") }.map { startingPoint ->
            var current = startingPoint
            var count = 0L
            while (!current.endsWith("Z")) {
                steps.forEach { current = if (it == 'R') map[current]!![1] else map[current]!![0] }
                count += steps.length
            }
            count
        }
        return counts.reduce { acc, i -> leastCommonMultiple(acc, i) }
    }

    val input = readInput("input_day_8")
    part1(input).println()
    part2(input).println()
}

fun leastCommonMultiple(a: Long, b: Long): Long {
    return a * b / greatestCommonDivisor(a, b)
}

fun greatestCommonDivisor(a: Long, b: Long): Long {
    return if (b == 0L) a else greatestCommonDivisor(b, a % b)
}
