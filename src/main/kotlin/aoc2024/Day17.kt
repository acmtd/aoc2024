package aoc2024

import aoc2024.Day17.*
import java.math.BigInteger
import kotlin.time.measureTime

class Day17 {
    data class Operation(val opcode: Int, val operand: Int) {
        companion object {
            fun fromValues(l: List<String>): Operation {
                val opcode = l.first().toInt()
                val operand = l.last().toInt()

                return Operation(opcode, operand)
            }
        }

        fun execute(c: Computer): Int? {
            var output: Int? = null

            when (opcode) {
                0 -> { // ADV
                    c.a = dv(c, operand)
                }

                1 -> { // BXL
                    c.b = c.b.xor(BigInteger.valueOf(operand.toLong()))
                }

                2 -> { // BST
                    c.b = combo(operand, c).mod(BigInteger.valueOf(8))
                }

                3 -> { // JNZ: in all tests this just goes back to the start so no special coding
                }

                4 -> { // BXC (ignores operand)
                    c.b = c.b.xor(c.c)
                }

                5 -> { // OUT
                    output = (combo(operand, c).mod(BigInteger.valueOf(8))).toInt()
                }

                6 -> { // BDV
                    c.b = dv(c, operand)
                }

                7 -> { // CDV
                    c.c = dv(c, operand)
                }
            }

            return output
        }

        private fun dv(c: Computer, o: Int): BigInteger {
            if (combo(o, c) == BigInteger.ZERO) return c.a

            val denominator = BigInteger.TWO.pow(combo(o, c).toInt())
            return c.a.divide(denominator)
        }

        private fun combo(operand: Int, c: Computer): BigInteger {
            return when (operand) {
                in (0..3) -> BigInteger.valueOf(operand.toLong())
                4 -> c.a
                5 -> c.b
                6 -> c.c
                else -> throw IllegalArgumentException("Invalid opcode")
            }
        }
    }

    data class Computer(var a: BigInteger, var b: BigInteger, var c: BigInteger, val program: List<Operation>) {
        private val cache = mutableMapOf<Pair<BigInteger, BigInteger>, Int>()

        companion object {
            fun fromInput(input: List<String>): Computer {
                val registers = input.first().split("\n").map { it.substringAfter(": ") }.map { it.toBigInteger() }
                val program = input.last().substringAfter(": ").split(",").chunked(2).map { Operation.fromValues(it) }

                return Computer(registers[0], registers[1], registers[2], program)
            }
        }

        private fun program() = program.flatMap { listOf(it.opcode, it.operand) }

        private fun doCycle(): Int {
            // take advantage of the fact that all the full programs
            // have an output then a back-to-start as the last two instructions,
            // for shorter programs this doesn't work
            return cache.getOrPut(Pair(a, b)) {
                program.firstNotNullOf { op -> op.execute(this) }
            }
        }

        fun run() = buildList { while (a > BigInteger.ZERO) add(doCycle()) }.joinToString(",")

        fun runPart2(): BigInteger {
            var possibleA = listOf(0).map { it.toBigInteger() }

            for (round in 1..this.program().size) {
                val target = this.program().takeLast(round)

                val aValues =
                    possibleA.flatMap { baseA -> (0..7).map { baseA.plus(BigInteger.valueOf(it.toLong())) } }.distinct()
                        .filter { it > BigInteger.ZERO }

                val bValues = (0..7).map { it.toBigInteger() }

                val matches = aValues.flatMap { pa ->
                    bValues.map { pb -> Pair(pa, pb) }
                        .filter {
                            val computer = Computer(
                                it.first,
                                it.second,
                                BigInteger.ZERO,
                                program
                            )

                            val result = buildList {
                                repeat(round) {
                                    add(computer.doCycle())
                                }
                            }

                            result == target
                        }
                }

                if (round < this.program().size) {
                    possibleA = matches.map { it.first }.distinct().map { it.multiply(BigInteger.valueOf(8)) }
                } else {
                    return matches.filter { it.second == BigInteger.ZERO }.minOfOrNull { it.first } ?: BigInteger.ZERO
                }
            }

            return BigInteger.ZERO
        }
    }
}

fun main() {
    // part 1
    Computer.fromInput(readAsBlocks("Day17_test")).run().println()
    measureTime { Computer.fromInput(readAsBlocks("Day17")).run().println() }.also { it.println() }

    // part 2
    Computer.fromInput(readAsBlocks("Day17_test2")).runPart2().println()
    measureTime { Computer.fromInput(readAsBlocks("Day17")).runPart2().println() }.also { it.println() }
}
