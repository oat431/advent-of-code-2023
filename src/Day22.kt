data class Point3D(val x: Int, val y: Int, val z: Int)

operator fun Point3D.plus(other: Point3D) = Point3D(x + other.x, y + other.y, z + other.z)

fun <T> List<T>.asPair() = this[0] to this[1]
inline fun <T, R> Iterable<T>.mapToSet(block: (T) -> R): Set<R> = mapTo(hashSetOf(), block)
fun Point3D.mapZ(block: (z: Int) -> Int) = copy(z = block(z))

fun <T> Iterable<T>.countContains(value: T) = count { it == value }
fun main() {
    fun part1(input : List<Int>) : Int {
        return input.countContains(0)
    }

    fun part2(input : List<Int>) : Int {
        return input.sum()
    }

    val input = readInput("input_day_22").map {
        l ->
        l.split("~").map { p ->
            val (a, b, c) = p.split(",").map(String::toInt)
            Point3D(a, b, c)
        }.asPair()
    }

    val bp = input.map { (a, b) ->
        val ps = hashSetOf<Point3D>()

        for (x in a.x..b.x) {
            for (y in a.y..b.y) {
                for (z in a.z..b.z) {
                    ps += Point3D(x, y, z)
                }
            }
        }
        ps
    }

    val (s) = bp.step()
    val t = s.map { b ->
        val n = s.minusElement(b)
        n.step().second
    }

    part1(t).println()
    part2(t).println()

}

fun List<Set<Point3D>>.step(): Pair<List<Set<Point3D>>, Int> {
    val new = mutableListOf<Set<Point3D>>()
    val fallen = hashSetOf<Point3D>()
    var a = 0

    for (b in this.sortedBy { b -> b.minOf { it.z } }) {
        var cb = b
        while (true) {
            val down = cb.mapToSet { p -> p.mapZ { it - 1 } }
            if (down.any { it in fallen || it.z <= 0 }) {
                new += cb
                fallen += cb
                if (cb != b) a++
                break
            }

            cb = down
        }
    }

    return new to a
}