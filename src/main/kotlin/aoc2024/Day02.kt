package aoc2024

import kotlin.math.abs

fun main() {
    fun evaluateReport(r: List<Int>): Boolean {
        val pairs = r.zipWithNext()
        val allIncreasing = pairs.all { it.second > it.first }
        val allDecreasing = pairs.all { it.second < it.first }
        val differenceOk = pairs.all { abs(it.second - it.first) in (1..3) }

        return (allIncreasing || allDecreasing) && differenceOk
    }

    fun part1(input: List<String>): Int {
        return input.map { it.split(" ").map { l -> l.toInt() } }
            .count { evaluateReport(it) }
    }

    fun part2(input: List<String>): Int {
        val reports = input.map { it.split(" ").map { l -> l.toInt() } }

        val safeWithNoRemovals = reports.count { evaluateReport(it) }
        val reportsToCheck = reports.filterNot { evaluateReport(it) }

        val safeAfterRemoval = reportsToCheck.count {
            it.indices.any { i ->
                evaluateReport(it.subList(0, i) + it.subList(i + 1, it.size))
            }
        }

        return safeWithNoRemovals + safeAfterRemoval
    }


    val testInput = readAsLines("Day02_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readAsLines("Day02")
    part1(input).println()
    part2(input).println()
}

