import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText
import kotlin.math.absoluteValue

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()
fun readText(name: String) = Path("src/$name.txt").readText()

fun <T> runNTimes(func: (T) -> T, init: T, n: Long): T {
    val seen = mutableListOf<T>()
    var cur = init
    var times = 0L
    while (cur !in seen) {
        seen += cur
        cur = func(cur)
        if (++times == n) return cur
    }
    val offset = seen.indexOf(cur)
    val loop = seen.size - offset
    val afterLoop = (n - offset) % loop
    return (1..afterLoop).fold(cur) { total, _ -> func(total) }
}
/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

