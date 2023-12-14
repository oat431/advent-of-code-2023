const val EMPTY = 0
const val ROCK = 1
const val CUBE = 2

fun main() {
    fun part1(grid : List<IntArray>): String {
        rollNorth(grid)
        return getLoad(grid).toString()
    }

    fun part2(grid : List<IntArray>): String {
        rollWest(grid)
        rollSouth(grid)
        rollEast(grid)
        val oldGrids = mutableListOf(grid.map(IntArray::copyOf))
        for (cycle in 1 ..< 999999999) {
            rollNorth(grid)
            rollWest(grid)
            rollSouth(grid)
            rollEast(grid)
            val i = oldGrids.indexOfFirst { oldGrid -> grid.indices.all { i -> grid[i].contentEquals(oldGrid[i]) } }
            if (i == -1) { oldGrids.add(grid.map(IntArray::copyOf)); continue }
            val cycleLength = cycle - i
            val remaining = 999999999 - cycle
            val offset = remaining % cycleLength
            return getLoad(oldGrids[i + offset]).toString()
        }
        return getLoad(grid).toString()
    }

    val input = readInput("input_day_14")
    val grid: List<IntArray> = input.map { line ->
        IntArray(line.length) { i ->
            when (line[i]) {
                '.' -> EMPTY
                'O' -> ROCK
                '#' -> CUBE
                else -> error("Unexpected token ${line[i]}")
            }
        }
    }

    part1(grid).println()
    part2(grid).println()
}

fun rollNorth(grid : List<IntArray>) {
    for (max in grid.size downTo 1) for (i in 1 ..< max) for (j in grid[0].indices) {
        if (grid[i][j] == ROCK && grid[i - 1][j] == EMPTY) {
            grid[i][j] = EMPTY
            grid[i - 1][j] = ROCK
        }
    }
}

fun rollSouth(grid : List<IntArray>) {
    for (min in 0 .. grid.size - 2) for (i in grid.size - 2 downTo min) for (j in grid[0].indices) {
        if (grid[i][j] == ROCK && grid[i + 1][j] == EMPTY) {
            grid[i][j] = EMPTY
            grid[i + 1][j] = ROCK
        }
    }
}

fun rollEast(grid : List<IntArray>) {
    for (min in 0 .. grid[0].size) for (j in grid[0].size - 2 downTo min) for (i in grid.indices) {
        if (grid[i][j] == ROCK && grid[i][j + 1] == EMPTY) {
            grid[i][j] = EMPTY
            grid[i][j + 1] = ROCK
        }
    }
}

fun rollWest(grid : List<IntArray>) {
    for (max in grid[0].size downTo 1) for (j in 1 ..< max) for (i in grid.indices) {
        if (grid[i][j] == ROCK && grid[i][j - 1] == EMPTY) {
            grid[i][j] = EMPTY
            grid[i][j - 1] = ROCK
        }
    }
}

fun getLoad(grid: List<IntArray>): Int =
    grid.indices.sumOf { x -> (grid.size - x) * grid[x].count { n -> n == ROCK } }
