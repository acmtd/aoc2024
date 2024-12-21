package aoc2024

import aoc2024.Day21.*
import kotlin.math.min

class Day21 {
    data class Connection(val from: String, val to: String, val direction: Char)
    data class Keypad(val connections: List<Connection>) {
        val routes = routes()

        private fun routes(): Map<Pair<String, String>, Set<String>> {
            val buttons = connections.map { it.from }.toSet()

            return buildMap {
                buttons.forEach { from ->
                    buttons.forEach { to ->
                        put(Pair(from, to), shortestPaths(from, to))
                    }
                }
            }
        }

        private fun shortestPaths(from: String, to: String): Set<String> {
            if (from == to) return setOf("")

            val directConnection = connections.filter { it.from == from && it.to == to }

            if (directConnection.isNotEmpty()) return setOf(directConnection.first().direction.toString())

            val queue = ArrayDeque<Pair<Connection, String>>()
            connections.filter { it.from == from }.forEach { queue.add(Pair(it, it.direction.toString())) }

            val visited = mutableSetOf<String>()
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

                    if (up.isNotEmpty()) this.add(Connection(label, up, '^'))
                    if (down.isNotEmpty()) this.add(Connection(label, down, 'v'))
                    if (left.isNotEmpty()) this.add(Connection(label, left, '<'))
                    if (right.isNotEmpty()) this.add(Connection(label, right, '>'))
                }
            }
        }
    }

    return Keypad(connections)
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

    val input = readAsLines("Day21")
    part1(input, routes).println() // 176452
}

private fun routes(): Map<Pair<String, String>, Set<String>> {
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

private fun part1(input: List<String>, routes: Map<Pair<String, String>, Set<String>>): Long {
    return input.sumOf { code ->
        var sequences = setOf(code)

        repeat(3) { sequences = nextSequence(sequences, routes) }

        sequences.minOf { it.length } * code.removePrefix("0").removeSuffix("A").toLong()
    }
}

private fun nextSequence(sequences: Set<String>, routes: Map<Pair<String, String>, Set<String>>): Set<String> {
    val nextSequences = sequences.flatMap { seq ->
        ("A$seq").zipWithNext().map { (from, to) ->
            routes[Pair(from.toString(), to.toString())]!!.map { it + "A" }
        }.reduce { l1, l2 ->
            buildList { l1.forEach { s1 -> l2.forEach { s2 -> add(s1 + s2) } } }
        }
    }

    val shortest = nextSequences.minOf { it.length }
    return nextSequences.filter { seq -> seq.length == shortest }.toSet()
}