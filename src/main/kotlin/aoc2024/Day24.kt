package aoc2024

class Day24(input: List<String>) {
    private val wireCounts = buildMap {
        input.first().lines().map { wc -> wc.split(": ").let { put(it[0], it[1].toInt()) } }
    }.toMutableMap()

    private val gates = input.last().lines().map { c -> c.split(" ").let { Gate(listOf(it[0], it[2]), it[1], it[4]) } }

    private fun processGate(gate: Gate) {
        wireCounts[gate.output] =
            gate.inputs.map { wireCounts.getValue(it) }.let { doOperation(it[0], it[1], gate.inst) }
    }

    private fun unresolvedGates() =
        gates.filter { it.inputs.all { input -> input in wireCounts.keys } && it.output !in wireCounts.keys }

    private fun resolveGates() {
        do {
            unresolvedGates().forEach { processGate(it) }
        } while (unresolvedGates().isNotEmpty())
    }

    private fun zWireDecimalValue(): Long {
        val zWires = wireCounts.keys.filter { it.startsWith("z") }.sortedBy { it.substring(1).toInt() }
        return zWires.indices.sumOf { idx -> wireCounts.getValue(zWires[idx]).toLong().shl(idx) }
    }

    private fun doOperation(x: Int, y: Int, inst: String) = when (inst) {
        "AND" -> x.and(y)
        "OR" -> x.or(y)
        "XOR" -> x.xor(y)
        else -> throw IllegalArgumentException("Invalid operation: $inst")
    }

    data class Gate(val inputs: List<String>, val inst: String, val output: String) {}

    fun part1(): Long {
        resolveGates()
        return zWireDecimalValue();
    }
}

fun main() {
    val testInput = readAsBlocks("Day24_test")
    check(Day24(testInput).part1() == 4L)

    val testInput2 = readAsBlocks("Day24_test2")
    check(Day24(testInput2).part1() == 2024L)

    val input = readAsBlocks("Day24")
    Day24(input).part1().println()
}