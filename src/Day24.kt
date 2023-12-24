import java.math.BigDecimal
import java.util.*

data class Input(
    val x: Long,
    val y: Long,
    val z: Long,
    val xDiff: Long,
    val yDiff: Long,
    val zDiff: Long
) {
    data class Equation(
        val m: BigDecimal,
        val b: BigDecimal
    )

    val equation = run {
        val x2 = x + xDiff
        val y2 = y + yDiff
        val m = (y2 - y).toBigDecimal() / (x2 - x).toBigDecimal()
        val b = y.toBigDecimal() - m * x.toBigDecimal()
        Equation(m, b)
    }
}

data class Stone(val pos: List<Double>, val vel: List<Double>)

data class Line(val d: Double, val inter: Double, val stone: Stone)
fun Line.eval(x: Double) = d * x + inter

fun <T> Iterable<T>.permutations() = toList().permutations()
fun <T> Iterable<T>.permutations(r: Int) = toList().permutations(r)
fun <T> List<T>.permutations(r: Int = size) = sequence {
    if (r > size || isEmpty()) return@sequence

    val ind = indices.toMutableList()
    val cyc = (size downTo size - r).toMutableList()
    yield(take(r + 1))

    while (true) {
        for (i in r - 1 downTo 0) {
            if (--cyc[i] == 0) {
                ind.add(ind.removeAt(i))
                cyc[i] = size - i
            } else {
                Collections.swap(ind, i, size - cyc[i])
                yield(slice(ind.take(r)))
                break
            }

            if (i == 0) return@sequence
        }
    }
}

fun <T> Iterable<T>.combinations() = toList().combinations()
fun <T> Iterable<T>.combinations(r: Int) = toList().combinations(r)
fun <T> List<T>.combinations(r: Int = size) =
    indices.permutations(r).filter { it.sorted() == it }.map { p -> p.map { this[it] } }

fun main() {
    fun part1(stones : List<Stone>) : Int {
        val ls = stones.map { (pos, vel) ->
            val (x0, y0) = pos
            val (vx0, vy0) = vel
            val d = (vy0) / (vx0)
            val inter = y0 - d * x0
            Line(d, inter, Stone(pos, vel))
        }

        val r = 200000000000000.0..400000000000000.0
        return ls.combinations(2).count { (a, b) ->
            if (a.d == b.d) return@count false
            val xi = (a.d * a.stone.pos[0] - a.stone.pos[1] + b.stone.pos[1] - b.d * b.stone.pos[0]) / ((a.d - b.d))
            val yi = a.eval(xi)

            ((xi in r && yi in r) &&
                    (if (a.stone.vel[0] < 0) xi < a.stone.pos[0] else xi > a.stone.pos[0]) &&
                    (if (a.stone.vel[1] < 0) yi < a.stone.pos[1] else yi > a.stone.pos[1]) &&
                    (if (b.stone.vel[0] < 0) xi < b.stone.pos[0] else xi > b.stone.pos[0]) &&
                    (if (b.stone.vel[1] < 0) yi < b.stone.pos[1] else yi > b.stone.pos[1]))
        }
    }

    // https://microsoft.github.io/z3guide/playground/Freeform%20Editing
    fun part2(parsed : List<Input>): String {
        return buildString {
            appendLine("(declare-const fx Int)")
            appendLine("(declare-const fy Int)")
            appendLine("(declare-const fz Int)")
            appendLine("(declare-const fdx Int)")
            appendLine("(declare-const fdy Int)")
            appendLine("(declare-const fdz Int)")
            parsed.take(3).forEachIndexed{ index, input ->
                appendLine("(declare-const t$index Int)")
                appendLine("(assert (>= t$index 0))")
                appendLine("(assert (= (+ ${input.x} (* ${input.xDiff} t$index)) (+ fx (* fdx t$index))))")
                appendLine("(assert (= (+ ${input.y} (* ${input.yDiff} t$index)) (+ fy (* fdy t$index))))")
                appendLine("(assert (= (+ ${input.z} (* ${input.zDiff} t$index)) (+ fz (* fdz t$index))))")
            }
            appendLine("(check-sat)")
            appendLine("(get-model)")
            appendLine("(eval (+ fx fy fz))")
        }
    }

    val input = readText("input_day_24").lines().map {
        it.split(" @ ").let { (a, b) ->
            val (x, y, z) = a.split(", ").map { it.toLong() }
            val (xDiff, yDiff, zDiff) = b.split(", ").map { it.toLong() }
            Input(x, y, z, xDiff, yDiff, zDiff)
        }
    }

    val stones = readText("input_day_24").lines().map { l ->
        val (a, b) = l.split(" @ ").map {
            "-?\\d+".toRegex().findAll(it).map { p -> p.value.toDouble() }.toList()
        }
        Stone(a, b)
    }

    part1(stones).println()
    part2(input).println()

}