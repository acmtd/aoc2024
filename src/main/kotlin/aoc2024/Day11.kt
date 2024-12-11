package aoc2024

fun main() {
    val splitMap = hashMapOf<String, List<String>>()
    val multiplyMap = hashMapOf<String, String>()

    fun removeLeadingZeros(s: String): String {
        val removed = s.trimStart('0')

        if (removed.isEmpty()) return "0"
        return removed
    }

    fun splitNumber(s: String): List<String> {
        return splitMap.getOrPut(s) {
            listOf(
                s.substring(0, s.length / 2),
                removeLeadingZeros(s.substring(s.length / 2, s.length))
            )
        }
    }

    fun multiplyNumber(s: String): String {
        return multiplyMap.getOrPut(s) { (s.toLong() * 2024).toString() }
    }

    fun blink(stones: List<String>): List<String> {
        return buildList {
            stones.map {
                if (it == "0") {
                    add("1")
                } else if (it.length % 2 == 0) {
                    addAll(splitNumber(it))
                } else {
                    add(multiplyNumber(it))
                }
            }
        }
    }

    fun parse(input: String): List<String> {
        return input.split(" ")
    }

    fun blinksForSingleStone(stone: String, blinks: Int): Int {
        var arrangement = listOf(stone)

        repeat(blinks) {
            arrangement = blink(arrangement)
        }

        return arrangement.size
    }

    fun doBlinks(stones: List<String>, blinks: Int): Int {
        return stones.sumOf { blinksForSingleStone(it, blinks) }
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = parse("125 17")
    check(doBlinks(testInput, 6) == 22)
    check(doBlinks(testInput, 25) == 55312)

    val input = parse(readAsString("Day11"))
    doBlinks(input, 25).println() // 209412
}

