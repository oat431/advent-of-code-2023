fun main() {
    fun part1(input: List<String>) : Long {
        val seeds = input.first().substringAfter(" ").split(" ").map { it.toLong() }
        val maps = input.drop(2).joinToString("\n").split("\n\n").map { section ->
            section.lines().drop(1).associate {
                it.split(" ").map { it.toLong() }.let { (dest, source, length) ->
                    source..(source + length) to dest..(dest + length)
                }
            }
        }
        return seeds.minOf { seed ->
            maps.fold(seed) { aac, map ->
                map.entries.firstOrNull { aac in it.key }?.let { (source, dest) -> dest.first + (aac - source.first) } ?: aac
            }
        }
    }

    fun part2(input: List<String>) : Long {
        val seeds = input.first().substringAfter(" ").split(" ").map { it.toLong() }.chunked(2).map { it.first()..<it.first() + it.last() }
        val maps = input.drop(2).joinToString("\n").split("\n\n").map { section ->
            section.lines().drop(1).associate {
                it.split(" ").map { it.toLong() }.let { (dest, source, length) ->
                    source..(source + length) to dest..(dest + length)
                }
            }
        }
        return seeds.flatMap { seedsRange ->
            maps.fold(listOf(seedsRange)) { aac, map ->
                aac.flatMap {
                    map.entries.mapNotNull { (source, dest) ->
                        (maxOf(source.first, it.first) to minOf(source.last, it.last)).let { (start, end) ->
                            if (start <= end) (dest.first - source.first).let { (start + it)..(end + it) } else null
                        }
                    }
                }
            }
        }.minOf { it.first }
    }

    val input = readInput("input_day_5")
    part1(input).println()
    part2(input).println()
}