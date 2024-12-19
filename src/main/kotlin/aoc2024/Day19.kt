package aoc2024

import java.math.BigInteger

val cache = mutableMapOf<String, List<String>>()

fun main() {
    fun towels(input: List<String>) = input.first().split(", ")
    fun designs(input: List<String>) = input.last().lines()

    fun part1(input: List<String>): Int {
        val towels = towels(input)
        val designs = designs(input)

        cache.clear()

        return designs.count { design -> design.waysToMakeFromTowels(towels.filter { design.contains(it) }) > BigInteger.ZERO }
    }

    fun part2(input: List<String>): BigInteger {
        val towels = towels(input)
        val designs = designs(input)

        cache.clear()

        val result =
            designs.map { design -> design.waysToMakeFromTowels(towels.filter { design.contains(it) }) }

        return result.sumOf { it }
    }

    val testInput = readAsBlocks("Day19_test")
    check(part1(testInput) == 6)
    check(part2(testInput) == BigInteger.valueOf(16))

    val input = readAsBlocks("Day19")

    part1(input).println() // 360 is ok
    part2(input).println() // 171805373 is too low, 7094486668417617499097789 is too high
}

private fun String.waysToMakeFromTowels(towels: List<String>): BigInteger {
    val queue = ArrayDeque<String>()
    queue.add(this)

    val combinations = mutableMapOf<String, BigInteger>()
    combinations[this] = BigInteger.ONE

    while (queue.isNotEmpty()) {
        val design = queue.removeFirst()

        val possibleTowels = cache.getOrPut(design) { towels.filter { t -> design.startsWith(t) } }

        possibleTowels.forEach { towel ->
            val remaining = design.drop(towel.length)
            combinations[remaining] = combinations.getOrDefault(remaining, BigInteger.ZERO).plus((combinations.getOrDefault(design, BigInteger.ZERO)))

            if (remaining !in queue) queue.add(remaining)
        }
    }

    val result = combinations.getOrDefault("", BigInteger.ZERO)
    return result
}