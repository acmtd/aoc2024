package aoc2024

import aoc2024.Day13.*

class Day13 {
    data class Button(val mx: Int, val my: Int)
    data class Prize(val px: Int, val py: Int)
    data class Machine(val a: Button, val b: Button, val prize: Prize)
}

private fun String.toButton(): Button {
    val values = this.substringAfter(": ").split(", ").map { it.substringAfter("+").toInt() }

    return Button(values[0], values[1])
}

private fun String.toPrize(): Prize {
    val values = this.substringAfter(": ").split(", ").map { it.substringAfter("=").toInt() }

    return Prize(values[0], values[1])
}

fun main() {
    fun parse(blocks: List<String>): List<Machine> {
        return blocks.map { it.lines() }.map { l -> Machine(l[0].toButton(), l[1].toButton(), l[2].toPrize()) }
    }

    fun evaluate(m: Machine, part2: Boolean): Long {
        // convert my math scribblings into code...
        val modifier: Long = if (part2) 10000000000000 else 0

        val px = m.prize.px + modifier
        val py = m.prize.py + modifier

        val max = m.a.mx
        val mbx = m.b.mx
        val may = m.a.my
        val mby = m.b.my

        val a = (px * mby - py * mbx).div(mby * max - mbx * may)
        val remainder = (px * mby - py * mbx) % (mby * max - mbx * may)

        // make sure solution is integer
        if (remainder == 0.toLong()) {
            val b = (px * may - py * max).div(may * mbx - max * mby)

            val cost = 3 * a + b
            return cost
        }
        return 0
    }

    fun part1(input: List<String>): Long {
        return parse(input).sumOf { evaluate(it, false) }
    }

    fun part2(input: List<String>): Long {
        return parse(input).sumOf { evaluate(it, true) }
    }

    val testInput = readAsBlocks("Day13_test")
    check(part1(testInput) == 480.toLong())
    part2(testInput)

    val input = readAsBlocks("Day13")
    part1(input).println()
    part2(input).println() // 96019708038790 is too high
}
