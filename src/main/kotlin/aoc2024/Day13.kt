package aoc2024

import aoc2024.Day13.*
import kotlin.time.measureTime

class Day13 {
    data class Button(val mx: Int, val my: Int)
    data class Prize(val px: Int, val py: Int)
    data class Machine(val a: Button, val b: Button, val prize: Prize)
}

private fun String.toButton() =
    this.substringAfter(": ").split(", ").map { it.substringAfter("+").toInt() }.let { Button(it[0], it[1]) }

private fun String.toPrize() =
    this.substringAfter(": ").split(", ").map { it.substringAfter("=").toInt() }.let { Prize(it[0], it[1]) }

fun main() {
    fun parse(blocks: List<String>) =
        blocks.map { it.lines() }.map { l -> Machine(l[0].toButton(), l[1].toButton(), l[2].toPrize()) }

    fun evaluate(m: Machine, part2: Boolean): Long {
        // convert my math scribblings into code...
        val modifier: Long = if (part2) 10000000000000 else 0

        val px = m.prize.px + modifier
        val py = m.prize.py + modifier

        val aDividend = (px * m.b.my - py * m.b.mx)
        val aDivisor = (m.b.my * m.a.mx - m.b.mx * m.a.my)

        // make sure solution is integer
        if (aDividend % aDivisor == 0.toLong()) {
            val a = aDividend.div(aDivisor)

            // don't need the full equation to find b,
            // just plug the calculated a-value into one
            // equation and voilà
            val remaining = px - (a * m.a.mx)

            if (remaining % m.b.mx == 0.toLong()) {
                val b = remaining.div(m.b.mx)

                return 3 * a + b
            }
        }
        return 0
    }

    fun part1(input: List<String>) = parse(input).sumOf { evaluate(it, false) }
    fun part2(input: List<String>) = parse(input).sumOf { evaluate(it, true) }

    val testInput = readAsBlocks("Day13_test")
    check(part1(testInput) == 480.toLong())

    val input = readAsBlocks("Day13")
    measureTime { part1(input).println() }.also { it.println() }
    measureTime { part2(input).println() }.also { it.println() }
}
