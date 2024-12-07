package aoc2024

fun main() {
    fun calibrates(target: Long, values: List<Long>, part2: Boolean): Boolean {
        val queue = ArrayDeque<List<Long>>()
        queue.add(values)

        var calculated = false

        while (queue.isNotEmpty() && !calculated) {
            val digits = queue.removeFirst()

            val operands = digits.subList(0, 2)
            val results = buildList {
                add(operands[0] * operands[1])
                add(operands[0] + operands[1])
                if (part2) add("${operands[0]}${operands[1]}".toLong())
            }

            if (digits.size > 2) {
                val remainder = digits.subList(2, digits.size)

                results.filter { it <= target }.map {
                    buildList {
                        add(it)
                        addAll(remainder)
                    }
                }.forEach { queue.add(it) }
            } else {
                // no more operands so see if we have the result
                if (results.any { it == target }) {
                    calculated = true
                }
            }
        }

        return calculated
    }

    fun getCalibrationResult(input: List<String>, part2: Boolean): Long {
        val equations =
            input.map {
                it.substringBefore(": ").toLong() to it.substringAfter(": ").split(" ").map { d -> d.toLong() }
            }

        return equations.filter { calibrates(it.first, it.second, part2) }.sumOf { it.first }
    }

    fun part1(input: List<String>): Long {
        return getCalibrationResult(input, false)
    }

    fun part2(input: List<String>): Long {
        return getCalibrationResult(input, true)
    }

    val testInput = readAsLines("Day07_test")
    check(part1(testInput) == 3749.toLong())
    check(part2(testInput) == 11387.toLong())

    val input = readAsLines("Day07")
    part1(input).println()
    part2(input).println()
}

