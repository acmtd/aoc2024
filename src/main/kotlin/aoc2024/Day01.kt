package aoc2024

import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        val cols = input.filter { it.isNotEmpty() }.map { it.split("\\s+".toRegex()) }

        val left = cols.map { it[0].toInt() }.sorted()
        val right = cols.map { it[1].toInt() }.sorted()

        return left.indices.sumOf { abs(left[it] - right[it]) }
    }

    fun part2(input: List<String>): Long {
        val left = mutableListOf<Int>()
        val right = mutableMapOf<Int, Int>()

        input.forEach { line ->
            if (line.isNotEmpty()) {
                val (a, b) = line.split("\\s+".toRegex())
                left.add(a.toInt())

                if (right.containsKey(b.toInt())) {
                    right[b.toInt()] = right[b.toInt()]!!.plus(1)
                } else {
                    right[b.toInt()] = 1
                }
            }
        }

        var result: Long = 0

        left.forEach { it ->
            if (right.containsKey(it)) {
                val rightValue = right[it]!!

//                println("key $it appears $rightValue times on right")
                result += (it.toLong() * rightValue.toLong())
            }
        }

        return result
    }

    // Or read a large test input from the `src/main/resources/Day01_test.txt` file:
    val testInput = readAsLines("Day01_test")
    check(part1(testInput) == 11)

    // Read the input from the `src/main/resources/Day01.txt` file.
    val input = readAsLines("Day01")
    part1(input).println() // 1223326 correct
    part2(input).println() // 21070419 correct
}

