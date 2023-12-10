class PartNumber(val value: Int, val startCoord: Pair<Int, Int>) {
    val border = mutableListOf<Pair<Int, Int>>();
    private val length = value.toString().length;

    init {
        border.add(startCoord.copy(second = startCoord.second - 1))
        border.add(startCoord.copy(second = startCoord.second + length))
        IntRange(startCoord.second - 1, startCoord.second + length)
            .forEach { x ->
                border.add(startCoord.first - 1 to x)
                border.add(startCoord.first + 1 to x)
            }
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val numRegex = Regex("\\d+")
        val symbolsRegex = Regex("[!@#$%^&*()+\\-=_/<>,`~|]")
        val possibleParts = mutableListOf<PartNumber>()
        val symbolsMap = mutableMapOf<Pair<Int, Int>, String>()

        input.forEachIndexed { lineNum, line ->
            possibleParts.addAll(numRegex.findAll(line).map {
                PartNumber(it.value.toInt(), lineNum to it.range.first)
            })
            symbolsRegex.findAll(line).forEach {
                symbolsMap[lineNum to it.range.first] = it.value
            }
        }

        val validParts = possibleParts.filter { part ->
            part.border.any { coord -> symbolsMap.containsKey(coord) }
        }

        return validParts.sumOf { it.value }
    }

    fun part2(input: List<String>): Int {
        val numRegex = Regex("\\d+")
        val symbolRegex = Regex("\\*")
        val possibleParts = mutableListOf<PartNumber>()
        val symbolsMap = mutableMapOf<Pair<Int, Int>, String>()
        input.forEachIndexed { lineNum, line ->
            possibleParts.addAll(numRegex.findAll(line).map {
                PartNumber(it.value.toInt(), lineNum to it.range.first)
            })
            symbolRegex.findAll(line).forEach {
                symbolsMap[lineNum to it.range.first] = it.value
            }
        }
        val gearRatios = symbolsMap.keys.mapNotNull { asterisk ->
            val adjacentParts = possibleParts.filter { part -> part.border.any { it == asterisk } }
            if (adjacentParts.size == 2) {
                adjacentParts[0].value * adjacentParts[1].value
            } else null
        }

        return gearRatios.sum();
    }

    val input = readInput("input_day_3")
    part1(input).println()
    part2(input).println()
}
