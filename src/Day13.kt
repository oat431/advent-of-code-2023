fun main() {
    fun part1(patterns: List<List<String>>): Int {
        return patterns.sumOf { pattern ->
            findHorizontalReflection(pattern, 0)?.let { n -> 100 * n }
                ?: findVerticalReflection(pattern, 0)
                ?: error("No reflection found")
        }
    }

    fun part2(patterns: List<List<String>>): Int {
        return patterns.sumOf { pattern ->
            findHorizontalReflection(pattern, 1)?.let { n -> 100 * n }
                ?: findVerticalReflection(pattern, 1)
                ?: error("No reflection found")
        }
    }
    val input = readText("input_day_13").trim().split("\r\n")
    val patterns: List<List<String>> = buildList {
        var offset = 0
        while (offset < input.size) {
            add(input.drop(offset).takeWhile(String::isNotBlank))
            offset += last().size.inc()
        }
    }
    part1(patterns).println()
    part2(patterns).println()

}

private fun findReflection(max: Int, count: Int, fn: (Int, Int) -> (Int)): Int? =
    (0 ..< max).find { i ->
        (0.. i.coerceAtMost(max - i - 1))
            .sumOf { offset -> fn(i - offset, i + 1 + offset) } == count
    }?.inc()

private fun findHorizontalReflection(pattern: List<String>, count: Int): Int? =
    findReflection(pattern.lastIndex, count) { i, j -> pattern[i].indices.count { y -> pattern[i][y] != pattern[j][y] } }

private fun findVerticalReflection(pattern: List<String>, count: Int): Int? =
    findReflection(pattern[0].lastIndex, count) { i, j -> pattern.indices.count { x -> pattern[x][i] != pattern[x][j] } }
