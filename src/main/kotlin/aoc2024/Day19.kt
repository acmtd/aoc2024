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

        val result =
            designs.count { design ->
                design.combinations(towels.filter { design.contains(it) }).any { it.key.second == 0 }
            }

        return result
    }

    fun part2(input: List<String>): BigInteger {
        val towels = towels(input)
        val designs = designs(input)

        cache.clear()

        return designs.map { design ->
            waysToReach(design.combinations(towels.filter { design.contains(it) }))
        }.sumOf { it }
    }

    val testInput = readAsBlocks("Day19_test")
    check(part1(testInput) == 6)
    check(part2(testInput) == BigInteger.valueOf(16))

    val input = readAsBlocks("Day19")

    part1(input).println() // 360
    part2(input).println() // 577474410989846
}

private fun String.combinations(towels: List<String>): MutableMap<Pair<Int, Int>, BigInteger> {
    val queue = ArrayDeque<String>()
    queue.add(this)

    val longestTowel = towels.maxOf { it.length }

    val towelCombos = mutableMapOf<Pair<Int, Int>, MutableSet<String>>()

    while (queue.isNotEmpty()) {
        val design = queue.removeFirst()

        val possibleTowels = cache.getOrPut(design.take(longestTowel)) { towels.filter { t -> design.startsWith(t) } }

        possibleTowels.forEach { towel ->
            val remaining = design.drop(towel.length)

            val fromTo = Pair(design.length, remaining.length)
            towelCombos[fromTo] = towelCombos.getOrDefault(fromTo, mutableSetOf()).apply { add(towel) }

            if (remaining !in queue && remaining.isNotEmpty()) queue.add(remaining)
        }
    }

    return buildMap {
        towelCombos.forEach { (key, value) -> put(key, BigInteger.valueOf(value.size.toLong())) }
    }.toMutableMap()
}

tailrec fun waysToReach(combinations: MutableMap<Pair<Int, Int>, BigInteger>): BigInteger {
    if (combinations.isEmpty()) return BigInteger.ZERO

    val highestValue = combinations.keys.maxOf { it.second }

    if (highestValue == 0) {
        return combinations.map { it.value }.sumOf { it }
    } else {
        val entriesToRemove = combinations.filterKeys { it.second == highestValue }
        val entriesToMultiply = combinations.filter { it.key.first == highestValue }

        val multiplier = entriesToRemove.map { it.value }.sumOf { it }

        entriesToRemove.keys.forEach { combinations.remove(it) }
        entriesToMultiply.keys.forEach { combinations[it] = combinations[it]!! * multiplier }

        return waysToReach(combinations)
    }
}