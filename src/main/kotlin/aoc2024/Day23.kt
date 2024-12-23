package aoc2024

fun main() {
    fun parse(input: List<String>): Map<String, MutableSet<String>> {
        return buildMap {
            input.map { it.split("-") }.map { computer ->
                (0..1).forEach { getOrPut(computer[it]) { mutableSetOf() }.add(computer[it.xor(1)]) }
            }
        }
    }

    fun nextClusters(clusters: Set<List<String>>, connections: Map<String, MutableSet<String>>): Set<List<String>> {
        return buildSet {
            clusters.forEach { computerList ->
                val newConnectionsToCheck =
                    computerList.flatMap { computer -> connections[computer]!! }.filter { it !in computerList }.toSet()

                newConnectionsToCheck.forEach { newComputer ->
                    val foundInAll = computerList.all { computer -> newComputer in connections[computer]!! }

                    if (foundInAll) add((computerList + newComputer).sorted())
                }
            }
        }
    }

    fun initialClusters(connections: Map<String, MutableSet<String>>): Set<List<String>> =
        connections.flatMap { (key, value) -> value.map { listOf(key, it).sorted() } }.toSet()

    fun part1(input: List<String>): Int {
        val connections = parse(input)

        return nextClusters(initialClusters(connections), connections)
            .filter { it.any { computer -> computer.startsWith("t") } }
            .size
    }

    fun part2(input: List<String>): String {
        val connections = parse(input)

        var clusters = initialClusters(connections)

        while (clusters.size > 1) {
            clusters = nextClusters(clusters, connections)
        }

        return clusters.first().joinToString(",")
    }

    val testInput = readAsLines("Day23_test")
    check(part1(testInput) == 7)
    check(part2(testInput) == "co,de,ka,ta")

    val input = readAsLines("Day23")
    part1(input).println()
    part2(input).println()
}
