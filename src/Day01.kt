fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { it ->
            val first = it.first { it.isDigit() }
            val last = it.last { it.isDigit() }
            "$first$last".toInt()
        }
    }

    fun part2(input: List<String>): Int {

        val validNumbers = mapOf(
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9
        )

        return input.sumOf { line ->
            val firstDigit = line.firstOrNull { it.isDigit() }?.let { it.toString() to line.indexOf(it) }
            val firstWord = validNumbers.keys.mapNotNull { key ->
                if (line.contains(key)) {
                    return@mapNotNull validNumbers[key]!!.toString() to line.indexOf(key)
                }
                null
            }.minByOrNull { it.second }

            val lastDigit = line.lastOrNull { it.isDigit() }?.let { it.toString() to line.lastIndexOf(it) }
            val lastWord = validNumbers.keys.mapNotNull { key ->
                if (line.contains(key)) {
                    return@mapNotNull validNumbers[key]!!.toString() to line.lastIndexOf(key)
                }
                null
            }.maxByOrNull { it.second }

            val first = listOf(firstDigit, firstWord).minBy { it?.second ?: Int.MAX_VALUE }!!.first
            val last = listOf(lastDigit, lastWord).maxBy { it?.second ?: Int.MIN_VALUE }!!.first

            "$first$last".toInt()
        }
    }

    val input = readInput("input_day_1")
    part1(input).println()
    part2(input).println()
}
