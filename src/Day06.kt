fun main() {
    fun part1(times : List<Int>, distances : List<Int>) : Int {
        val waysToBeatPerRound = mutableListOf<Int>()

        repeat(times.size) {
            val time = times[it]
            val distance = distances[it]
            var validCounter = 0

            (1..time).forEach { timeHolding ->
                val speed = timeHolding
                val remainingTime = time - timeHolding
                val totalDistance = speed * remainingTime

                if (totalDistance > distance) {
                    validCounter++
                }
            }

            waysToBeatPerRound.add(validCounter)
        }
        return waysToBeatPerRound.reduce { acc, i -> acc * i }
    }

    fun part2(times : Long , distances: Long) : Int {
        var validCounter = 0
        (1..times).forEach { timeHolding ->
            val speed = timeHolding
            val remainingTime = times - timeHolding
            val totalDistance = speed * remainingTime

            if (totalDistance > distances) {
                validCounter++
            }
        }

        return validCounter
    }

    val input = readInput("input_day_6")
    val time = input[0]
        .substringAfter(":")
        .split(" ")
        .filter { it.isNotBlank() }

    val distances = input[1]
        .substringAfter(":")
        .split(" ")
        .filter { it.isNotBlank() }

    part1(
        time.map { it.trim().toInt() },
        distances.map { it.trim().toInt() }
    ).println()

    part2(
        time.joinToString("").toLong(),
        distances.joinToString("").toLong()
    ).println()

}