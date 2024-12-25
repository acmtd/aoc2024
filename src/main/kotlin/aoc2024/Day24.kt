package aoc2024

import guru.nidi.graphviz.attribute.Color
import guru.nidi.graphviz.attribute.Font
import guru.nidi.graphviz.graph
import guru.nidi.graphviz.model.Factory.mutNode
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.engine.Format
import java.io.File

private fun Long.asBinaryString(): String {
    return java.lang.Long.toBinaryString(this).reversed().padEnd(46, '0')
}

class Day24(input: List<String>) {
    private val configuration = Configuration.fromInput(input)

    data class Configuration(val wireCounts: MutableMap<String, Int>, val gates: List<Gate>) {
        companion object {
            fun fromInput(input: List<String>): Configuration {
                val wireCounts = buildMap {
                    input.first().lines().map { wc -> wc.split(": ").let { put(it[0], it[1].toInt()) } }
                }.toMutableMap()

                val gates =
                    input.last().lines().map { c -> c.split(" ").let { Gate(listOf(it[0], it[2]), it[1], it[4]) } }

                return Configuration(wireCounts, gates)
            }
        }

        private fun processGate(gate: Gate) {
            wireCounts[gate.output] =
                gate.inputs.map { wireCounts.getValue(it) }.let { doOperation(it[0], it[1], gate.inst) }
        }

        private fun unresolvedGates() =
            gates.filter { it.inputs.all { input -> input in wireCounts.keys } && it.output !in wireCounts.keys }

        fun resolveGates() {
            wireCounts.keys.filter { !it.startsWith("x") && !it.startsWith("y") }.forEach(wireCounts::remove)

            do {
                unresolvedGates().forEach { processGate(it) }
            } while (unresolvedGates().isNotEmpty())
        }

        fun decimal(prefix: String): Long {
            val wires = wireCounts.keys.filter { it.startsWith(prefix) }.sortedBy { it.substring(1).toInt() }
            return wires.indices.sumOf { idx -> wireCounts.getValue(wires[idx]).toLong().shl(idx) }
        }

        private fun doOperation(x: Int, y: Int, inst: String) = when (inst) {
            "AND" -> x.and(y)
            "OR" -> x.or(y)
            "XOR" -> x.xor(y)
            else -> throw IllegalArgumentException("Invalid operation: $inst")
        }
    }

    data class Gate(val inputs: List<String>, val inst: String, val output: String) {}

    fun part1(): Long {
        configuration.resolveGates()
        return configuration.decimal("z");
    }

    private fun gatesAfterFlips(flips: Set<Set<Gate>>, initialConfiguration: Configuration): List<Gate> {
        val gatesToAdd = flips.flatMap { gates ->
            val first = gates.first()
            val second = gates.last()

            listOf(
                Gate(first.inputs, first.inst, second.output),
                Gate(second.inputs, second.inst, first.output)
            )
        }

        val gatesToReplace = flips.flatten()
        return initialConfiguration.gates + gatesToAdd - gatesToReplace.toSet()
    }

    fun drawGraph() {
        configuration.resolveGates()

        val gates = configuration.gates

        // these were figured out completely by visual anomaly inspection
        val flips = setOf(
            setOf(gates.first { it.output == "vss" }, gates.first { it.output == "z14" }),
            setOf(gates.first { it.output == "kdh" }, gates.first { it.output == "hjf" }),
            setOf(gates.first { it.output == "kpp" }, gates.first { it.output == "z31" }),
            setOf(gates.first { it.output == "sgj" }, gates.first { it.output == "z35" }),
        )

        val newGates = gatesAfterFlips(flips, configuration)

        val nc = configuration.copy(gates = newGates)
        nc.resolveGates()

        val desired = nc.decimal("x") + nc.decimal("y")
        val current = nc.decimal("z")

        val xor = current.xor(desired)

        val nodes = buildMap {
            nc.gates.forEach { g ->
                put(g.output, mutNode(g.output))
            }

            nc.wireCounts.keys.forEach { w ->
                put(w, mutNode(w))
            }
        }.toMutableMap()

        nodes.filter { it.key.startsWith("z") }.forEach { node ->
            val num = node.key.substring(1).toInt()
            val bit = current.asBinaryString().substring(num, num + 1)
            node.value.add(Font.size(30))
            if (xor and (1L shl num) == 0L) {
                node.value.add(Color.BLUE)
            } else {
                if (bit == "0") {
                    node.value.add(Color.RED)
                } else {
                    node.value.add(Color.SALMON)
                }
            }
        }

        nc.gates.forEach { g ->
            val key = g.inputs.first() + "_" + g.inst + "_" + g.inputs.last()
            val linkNode = mutNode(key)
            linkNode.add(Color.TAN1)

            g.inputs.forEach { g2 ->
                nodes.getValue(g2).addLink(linkNode)
            }

            linkNode.addLink(nodes.getValue(g.output))
            nodes[key] = linkNode
        }

        val graph = graph("Day24").setDirected(true)
        nodes.forEach { node -> graph.add(node.value) }
        Graphviz.fromGraph(graph).render(Format.PNG).toFile(File("src/main/resources/day24_graph.png"))
        println(flips.flatten().map { it.output }.sorted().joinToString(","))
    }
}

fun main() {
    val testInput = readAsBlocks("Day24_test")
    check(Day24(testInput).part1() == 4L)

    val testInput2 = readAsBlocks("Day24_test2")
    check(Day24(testInput2).part1() == 2024L)

    val input = readAsBlocks("Day24")
    Day24(input).part1().println()
    Day24(input).drawGraph()
}