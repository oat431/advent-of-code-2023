fun main() {
    fun part1(input : List<String>) : Int {
        return input.map(::hash).sum()
    }

    fun part2(input : List<String>) : Int {
        return input.fold(Array(256) { mutableMapOf<String, Int>() }) { acc, line ->
            val (value, focalLength) = line.split("=", "-")
            when {
                "-" in line -> acc[hash(value)] -= value
                else -> acc[hash(value)][value] = focalLength.toInt()
            }
            acc
        }.withIndex().sumOf { (i, map) -> (i + 1) * map.values.withIndex().sumOf { (j, value) -> (j + 1) * value } }
    }

    val input = readText("input_day_15").split(",")
    part1(input).println()
    part2(input).println()
}

fun hash(input: String): Int = input.fold(0) { hash, c -> ((hash + c.code) * 17) % 256 }
