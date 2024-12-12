package aoc2024

import kotlin.time.measureTime

fun main() {
    val blinkMap = hashMapOf<String, List<String>>()

    fun removeLeadingZeros(s: String): String {
        val removed = s.trimStart('0')

        if (removed.isEmpty()) return "0"
        return removed
    }

    fun splitNumber(s: String): List<String> {
        return listOf(
            s.substring(0, s.length / 2),
            removeLeadingZeros(s.substring(s.length / 2, s.length))
        )
    }

    fun multiplyNumber(s: String): String {
        return (s.toLong() * 2024).toString()
    }

    fun blink(stone: String): List<String> {
        return blinkMap.getOrPut(stone) {
            buildList {
                if (stone == "0") {
                    add("1")
                } else if (stone.length % 2 == 0) {
                    addAll(splitNumber(stone))
                } else {
                    add(multiplyNumber(stone))
                }
            }
        }
    }

    fun doBlinks(input: String, blinks: Int): Long {
        val stoneCounts = mutableMapOf<String, Long>().apply {
            input.split(" ").forEach { this[it] = 1 }
        }

        repeat(blinks) {
            stoneCounts.filter { it.value > 0 }
                .forEach { (stone, count) ->
                    stoneCounts[stone] = stoneCounts[stone]!! - count

                    blink(stone).forEach { newStone ->
                        stoneCounts[newStone] = stoneCounts.getOrDefault(newStone, 0) + count
                    }
                }
        }

        return stoneCounts.values.sum()
    }

    val testInput = "125 17"
    check(doBlinks(testInput, 6) == 22.toLong())
    check(doBlinks(testInput, 25) == 55312.toLong())

    val input = readAsString("Day11")
    measureTime { doBlinks(input, 25).println() }.also { it.println() } // 5ms for part 1
    measureTime { doBlinks(input, 75).println() }.also { it.println() } // 28ms for part 2
}

