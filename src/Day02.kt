fun main() {
    fun part1(input :List<String>) : Int {
        var possible = 0
        for (line in input) {
            val gameID = words(line)[1].replace(":", "").toInt()
            val game = line.split(":")[1]
            val reds = words(game).windowed(2).filter { it[1].trim().startsWith("red") }.map { it[0].toInt() }.max()
            val greens = words(game).windowed(2).filter { it[1].trim().startsWith("green") }.map { it[0].toInt() }.max()
            val blues = words(game).windowed(2).filter { it[1].trim().startsWith("blue") }.map { it[0].toInt() }.max()
            if (reds <= 12 && greens <= 13 && blues <= 14)
                possible += gameID
        }
        return possible
    }

    fun part2(input : List<String>) : Int {
        var sum = 0
        for (line in input) {
            val game = line.split(":")[1]
            val reds = words(game).windowed(2).filter { it[1].trim().startsWith("red") }.map { it[0].toInt() }.max()
            val greens = words(game).windowed(2).filter { it[1].trim().startsWith("green") }.map { it[0].toInt() }.max()
            val blues = words(game).windowed(2).filter { it[1].trim().startsWith("blue") }.map { it[0].toInt() }.max()
            val power = reds*greens*blues
            sum +=power
        }
        return sum
    }

    val input = readInput("input_day_2")
    part1(input).println()
    part2(input).println()
}

fun words(input: String) = input.trim().split("\\s+".toRegex())
