package aoc2024

import aoc2024.Day21.*
import kotlin.math.min

typealias MovePair = Pair<Char, Char>
typealias RouteList = Map<MovePair, Set<String>>

class Day21 {
    data class Connection(val from: Char, val to: Char, val direction: Char)
    data class Keypad(val connections: List<Connection>) {
        val routes = routes()

        private fun routes(): RouteList {
            val buttons = connections.map { it.from }.toSet()

            return buildMap {
                buttons.forEach { from ->
                    buttons.forEach { to ->
                        put(Pair(from, to), shortestPaths(from, to))
                    }
                }
            }
        }

        private fun shortestPaths(from: Char, to: Char): Set<String> {
            if (from == to) return setOf("")

            val directConnection = connections.filter { it.from == from && it.to == to }

            if (directConnection.isNotEmpty()) return setOf(directConnection.first().direction.toString())

            val queue = ArrayDeque<Pair<Connection, String>>()
            connections.filter { it.from == from }.forEach { queue.add(Pair(it, it.direction.toString())) }

            val visited = mutableSetOf<Char>()
            var best = Integer.MAX_VALUE

            val allPaths = buildList {
                while (queue.isNotEmpty()) {
                    val (c, route) = queue.removeFirst()
                    visited.add(c.from)

                    if (route.length <= best) {
                        if (c.to == to) {
                            add(route)
                            best = min(best, route.length)
                        } else {
                            connections.filter { it.from == c.to }
                                .filter { it.to !in visited }
                                .forEach { queue.add(Pair(it, route + it.direction.toString())) }
                        }
                    }
                }
            }

            return allPaths.filter { it.length == allPaths.minOf { p -> p.length } }.toSet()
        }
    }
}

fun String.parsePad(): Keypad {
    val rows = lines()
        .filterNot { it.contains("+") }
        .map { it.chunked(4).map { it.removePrefix("|").trim() }.dropLast(1) }

    val connections = rows.indices.flatMap { row ->
        rows[0].indices.flatMap { col ->
            val label = rows[row][col]

            buildList {
                if (label.isNotEmpty()) {
                    val up = if (row == 0) "" else rows[row - 1][col]
                    val down = if (row == rows.size - 1) "" else rows[row + 1][col]
                    val left = if (col == 0) "" else rows[row][col - 1]
                    val right = if (col == rows[0].size - 1) "" else rows[row][col + 1]

                    if (up.isNotEmpty()) this.add(Connection(label[0], up[0], '^'))
                    if (down.isNotEmpty()) this.add(Connection(label[0], down[0], 'v'))
                    if (left.isNotEmpty()) this.add(Connection(label[0], left[0], '<'))
                    if (right.isNotEmpty()) this.add(Connection(label[0], right[0], '>'))
                }
            }
        }
    }

    return Keypad(connections)
}

val cache = mutableMapOf<Pair<MovePair, Int>, Long>()

private fun shortestRoute(fromTo: MovePair, level: Int, routes: RouteList): Long {
    val cacheKey = Pair(fromTo, level)

    return cache.getOrPut(cacheKey) {
        // if no more levels to explore, reduces to counting the pairs
        if (level == 0) return 1L

        // every move begins with the robot either at the default position (A)
        // or follows a previous move, which always ends with pressing a button,
        // so we know that the directions we get from the routing table need
        // to be prefixed and suffixed with A.
        // Because we start and end at A every time, we also know that whenever
        // we encounter this same sequence we will get the same sequence of controlling
        // keys, so we can cache/memoize the output
        val next = routes[fromTo]!!.map { "A" + it + "A" }

        // then take these pairs and see what they map to at the next keypad
        next.minOf {
            it.zipWithNext().sumOf { p -> shortestRoute(p, level - 1, routes) }
        }
    }
}

private fun numberPart(code: String) = code.removePrefix("0").removeSuffix("A").toLong()

private fun nextSequence(sequences: Set<String>, routes: RouteList): Set<String> {
    val nextSequences = sequences.flatMap { seq ->
        ("A$seq").zipWithNext().map { fromTo ->
            routes[fromTo]!!.map { it + "A" }
        }.reduce { l1, l2 ->
            buildList { l1.forEach { s1 -> l2.forEach { s2 -> add(s1 + s2) } } }
        }
    }

    val shortest = nextSequences.minOf { it.length }
    return nextSequences.filter { seq -> seq.length == shortest }.toSet()
}

private fun routes(): RouteList {
    val numericKeypad = """
     +---+---+---+
     | 7 | 8 | 9 |
     +---+---+---+
     | 4 | 5 | 6 |
     +---+---+---+
     | 1 | 2 | 3 |
     +---+---+---+
         | 0 | A |
         +---+---+""".trimIndent().parsePad()

    val directionKeypad = """
        +---+---+
        | ^ | A |
    +---+---+---+
    | < | v | > |
    +---+---+---+
    """.trimIndent().parsePad()

    return numericKeypad.routes + directionKeypad.routes
}

fun main() {
    val testInput = """
        029A
        980A
        179A
        456A
        379A
    """.trimIndent().lines()

    val routes = routes()

    check(part1(testInput, routes) == 126384L)
    check(part2(testInput, routes, 3) == 126384L)

    val input = readAsLines("Day21")
    part1(input, routes).println()
    part2(input, routes, 26).println()
}

private fun part1(input: List<String>, routes: RouteList): Long {
    return input.sumOf { code ->
        var sequences = setOf(code)

        repeat(3) { sequences = nextSequence(sequences, routes) }

        sequences.minOf { it.length } * numberPart(code)
    }
}

private fun part2(input: List<String>, routes: RouteList, levels: Int): Long {
    return input.sumOf { code ->
        numberPart(code) * "A$code".zipWithNext().sumOf { shortestRoute(it, levels, routes) }
    }
}