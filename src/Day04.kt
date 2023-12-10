import kotlin.math.pow

fun main() {
    fun part1(input : List<String>) : Int {
        val cards = input.map { line ->
            val cardParts = line.split(" | ")
            val winners = cardParts[0].findAllNumbers().drop(1);
            val numbers = cardParts[1].findAllNumbers()
            winners to numbers
        }

        val worth = cards.map {
            val matches = it.first.intersect(it.second.toSet())
            if (matches.isNotEmpty()) (2.0.pow(matches.size) / 2)
            else 0.0
        }

        return worth.sum().toInt()
    }

    fun part2(input : List<String>) : Int {
        val cardCountMap = input.indices.associateWith { 1 }.toMutableMap();

        val matchesList = input.map { line ->
            val cardParts = line.split(" | ")
            val winners = cardParts[0].findAllNumbers().drop(1);
            val numbers = cardParts[1].findAllNumbers()
            winners.intersect(numbers.toSet()).size
        }

        matchesList.forEachIndexed { currIndex, matchCount ->
            val currentCardCount = cardCountMap[currIndex]!!
            for (i in 1 .. matchCount) {
                cardCountMap.merge(currIndex + i, currentCardCount, Int::plus)
            }
        }
        return cardCountMap.values.sum();
    }

    val input = readInput("input_day_4")
    part1(input).println()
    part2(input).println()
}

fun String.findAllNumbers(singleDigit: Boolean = false): List<Int> {
    return Regex("-?\\d${if (singleDigit) "" else "+"}").findAll(this).toList().map { it.value.toInt() }
}
