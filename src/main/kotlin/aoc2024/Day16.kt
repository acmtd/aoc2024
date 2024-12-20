package aoc2024

import aoc2024.Day16.*
import aoc2024.Day16.Vec2.Companion.SOUTH
import aoc2024.Day16.Vec2.Companion.WEST
import aoc2024.Day16.Vec2.Companion.EAST
import aoc2024.Day16.Vec2.Companion.NORTH
import kotlin.time.measureTime

class Day16 {
    data class State(val pos: Vec2, val facing: Vec2, val score: Long, val visited: List<Vec2>)

    data class Grid(val walls: List<Vec2>, val start: Vec2, val end: Vec2) {
        fun walk(): Pair<Long, Int> {
            val queue = ArrayDeque<State>()

            val initialState = State(start, EAST, 0, emptyList())
            queue.add(initialState)

            val minScoreMap = mutableMapOf<Pair<Vec2, Vec2>, Long>()

            var bestScore = Long.MAX_VALUE
            val bestSittingPositions = mutableSetOf<Vec2>()

            while (queue.isNotEmpty()) {
                val state = queue.removeFirst()

                if (!state.visited.contains(state.pos)) {
                    if (state.pos == end) {
                        if (state.score <= bestScore) {
                            if (state.score < bestScore) {
                                bestSittingPositions.clear()
                                bestScore = state.score
                            }

                            bestSittingPositions += state.visited
                        }
                    } else {
                        val lowScoreForPosDir = minScoreMap.getOrDefault(Pair(state.pos, state.facing), Long.MAX_VALUE)

                        if (state.score <= lowScoreForPosDir) {
                            minScoreMap[Pair(state.pos, state.facing)] = state.score

                            queue.addAll(possibleDirections(state.pos).map { dir ->
                                val newScore = if (dir == state.facing) 1 else 1001

                                State(state.pos + dir, dir, state.score + newScore, state.visited + state.pos)
                            })
                        }
                    }
                }
            }

            return Pair(bestScore, bestSittingPositions.size + 1)
        }

        private fun possibleDirections(pos: Vec2) = listOf(NORTH, SOUTH, WEST, EAST).filterNot { pos + it in walls }
    }

    data class Vec2(val x: Int, val y: Int) {
        operator fun plus(offset: Vec2) = Vec2(x + offset.x, y + offset.y)

        companion object {
            val NORTH = Vec2(0, -1)
            val SOUTH = Vec2(0, 1)
            val WEST = Vec2(-1, 0)
            val EAST = Vec2(1, 0)
        }
    }
}

fun parse(input: List<String>): Grid {
    var start: Vec2? = null
    var end: Vec2? = null

    val walls = buildList {
        input.forEachIndexed { rowNum, row ->
            row.forEachIndexed { colNum, value ->
                if (value == '#') {
                    add(Vec2(colNum, rowNum))
                }

                if (value == 'S') {
                    start = Vec2(colNum, rowNum)
                }

                if (value == 'E') {
                    end = Vec2(colNum, rowNum)
                }
            }
        }
    }

    return Grid(walls, start!!, end!!)
}

fun main() {
    val testInput = readAsLines("Day16_test")
    calculate(testInput)

    val testInput2 = readAsLines("Day16_test2")
    calculate(testInput2)

    val input = readAsLines("Day16")
    measureTime { calculate(input) }.also { it.println() } // 7 seconds
}

private fun calculate(input: List<String>) {
    parse(input).walk().println()
}