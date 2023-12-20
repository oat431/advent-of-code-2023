private sealed class Module {
    abstract val name: String

    open fun receive(source: String, pulse: Boolean) {}

    open fun output(receivedPulse: Boolean): Boolean? = receivedPulse

    data class Broadcaster(override val name: String) : Module()

    data class FlipFlop(override val name: String, var state: Boolean) : Module() {
        override fun receive(source: String, pulse: Boolean) {
            if (!pulse) {
                state = !state
            }
        }

        override fun output(receivedPulse: Boolean) = if (receivedPulse) null else state
    }

    data class Conjunction(override val name: String, val states: MutableMap<String, Boolean>) : Module() {
        override fun receive(source: String, pulse: Boolean) {
            states[source] = pulse
        }

        override fun output(receivedPulse: Boolean) = states.values.all { it }.not()
    }

    data class Untyped(override val name: String) : Module() {
        override fun output(receivedPulse: Boolean) = null
    }

    companion object {
        fun from(str: String): Module {
            return when (str.first()) {
                '%'  -> FlipFlop(name = str.drop(1), state = false)
                '&'  -> Conjunction(name = str.drop(1), states = mutableMapOf())
                else -> Broadcaster(name = str)
            }
        }
    }
}
fun main() {
    fun part1(modules :  Map<String, Module> , destinations: Map<String, List<String>>) : Int {
        var low = 0
        var high = 0

        repeat(1000) {
            var current = listOf(modules.getValue("broadcaster") to false)

            while (current.isNotEmpty()) {
                current = buildList {
                    for ((module, receivedPulse) in current) {
                        if (receivedPulse) high++ else low++

                        val output = module.output(receivedPulse) ?: continue
                        for (destination in destinations.getValue(module.name).map { modules.getValue(it) }) {
                            destination.receive(module.name, output)
                            add(destination to output)
                        }
                    }
                }
            }
        }

        return low * high
    }

    fun part2(modules :  Map<String, Module> , destinations: Map<String, List<String>>) : Long {
        val monitored = mutableMapOf("hh" to 0L, "lk" to 0L, "fn" to 0L, "fh" to 0L)

        var i = 0L
        while (monitored.values.any { it == 0L }) {
            i++
            var current = listOf(modules.getValue("broadcaster") to false)

            while (current.isNotEmpty()) {
                current = buildList {
                    for ((module, receivedPulse) in current) {
                        val output = module.output(receivedPulse) ?: continue

                        if (module.name in monitored && output) {
                            monitored[module.name] = i
                        }

                        for (destination in destinations.getValue(module.name).map { modules.getValue(it) }) {
                            destination.receive(module.name, output)
                            add(destination to output)
                        }
                    }
                }
            }
        }

        return monitored.values.lcm()
    }

    val input = readInput("input_day_20")

    val destinations = input.map { it.split(" -> ") }.associate { (name, destinations) ->
        name.replace("[%&]".toRegex(), "") to destinations.split(", ")
    }

    val modules = input.map { Module.from(it.split(" -> ").first()) }.associateBy { it.name } +
            destinations.values.flatten().filter { it !in destinations.keys }.associateWith { Module.Untyped(it) }

    for ((source, destinations) in destinations) {
        for (destination in destinations) {
            val module = modules.getValue(destination)
            if (module is Module.Conjunction) {
                module.states[source] = false
            }
        }
    }

    part1(modules, destinations).println()
    part2(modules, destinations).println()
}

fun Collection<Long>.lcm() = reduce { a, b -> lcm(a, b) }

fun lcm(a: Long, b: Long): Long = (a * b) / gcd(a, b)

fun gcd(a: Long, b: Long): Long = if (a == 0L) b else gcd(b % a, a)
