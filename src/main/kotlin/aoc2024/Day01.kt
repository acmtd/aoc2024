package aoc2024

import kotlin.math.abs

fun main() {
    fun getLists(input: List<String>): Pair<List<Int>, List<Int>> {
        val cols = input.filter { it.isNotEmpty() }.map { it.split("\\s+".toRegex()) }
        val left = cols.map { it[0].toInt() }.sorted()
        val right = cols.map { it[1].toInt() }.sorted()

        return Pair(left, right)
    }

    fun part1(input: List<String>): Int {
        val (left, right) = getLists(input)
        return left.indices.sumOf { abs(left[it] - right[it]) }
    }

    fun part2(input: List<String>): Int {
        val (left, right) = getLists(input)
        return left.sumOf { l -> l * right.count { it == l } }
    }

    val testInput = readAsLines("Day01_test")
    check(part1(testInput) == 11)
    check(part2(testInput) == 31)

    val input = readAsLines("Day01")
    part1(input).println()
    part2(input).println()
}

