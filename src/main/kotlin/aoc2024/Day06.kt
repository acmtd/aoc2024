package aoc2024

import kotlin.time.measureTime

class D6Direction(val deltaRow: Int, val deltaCol: Int) {
    companion object {
        val UP = D6Direction(-1, 0)
        val DOWN = D6Direction(1, 0)
        val LEFT = D6Direction(0, -1)
        val RIGHT = D6Direction(0, 1)
    }

    fun nextDirection(): D6Direction {
        when (this) {
            UP -> return RIGHT
            DOWN -> return LEFT
            RIGHT -> return DOWN
        }

        return UP
    }

    override fun toString(): String {
        return "Direction(deltaRow=$deltaRow, deltaCol=$deltaCol)"
    }
}

data class D6Pos(val row: Int, val col: Int) {
    fun transform(direction: D6Direction): D6Pos {
        return D6Pos(row + direction.deltaRow, col + direction.deltaCol)
    }

    fun outOfBounds(grid: Array<CharArray>) =
        (col < 0 || row < 0 || col >= grid.first().size || row >= grid.size)
}

data class D6State(val pos: D6Pos, val dir: D6Direction, val done: Boolean = false) {
    fun move(grid: Array<CharArray>): D6State {
        val nextPosition = pos.transform(dir)

        if (nextPosition.outOfBounds(grid)) {
            return D6State(pos, dir, true)
        }

        val content = grid[nextPosition.row][nextPosition.col]

        if (content == '#') {
            return D6State(pos, dir.nextDirection())
        } else {
            return D6State(nextPosition, dir)
        }
    }
}

fun main() {
    fun makeGrid(input: List<String>): Pair<Array<CharArray>, D6State> {
        val rows = input.size
        val cols = input.first().length

        val grid = Array(rows) { CharArray(cols) }

        var startPos = D6Pos(0, 0)

        for ((row, line) in input.withIndex()) {
            for ((col, character) in line.withIndex()) {
                grid[row][col] = character

                if (character == '^') {
                    startPos = D6Pos(row, col)
                }
            }
        }
        // we start by going up
        return Pair(grid, D6State(startPos, D6Direction.UP))
    }


    fun getVisited(input: List<String>): Set<D6State> {
        val (grid, initialState) = makeGrid(input)

        var state = initialState

        val visited = mutableSetOf<D6State>().apply { add(initialState) }

        do {
            state = state.move(grid)
            visited.add(state)
        } while (!state.done)

        return visited
    }

    fun part1(input: List<String>): Int {
        return getVisited(input).map { it.pos }.distinct().count()
    }

    fun part2(input: List<String>): Int {
        var loopCount = 0

        getVisited(input).map { s -> s.pos }.distinct().forEach { pos ->
            var (grid, state) = makeGrid(input)

            if (grid[pos.row][pos.col] == '.') {
                grid[pos.row][pos.col] = '#'

                val visited = mutableSetOf<D6State>()

                while (!state.done) {
                    visited.add(state)
                    state = state.move(grid)
                    if (state in visited) break
                }

                if (!state.done) loopCount++
            }
        }

        return loopCount
    }

    val testInput = readAsLines("Day06_test")
    check(part1(testInput) == 41)
    check(part2(testInput) == 6)

    val input = readAsLines("Day06")
    part1(input).println()
    measureTime {
        part2(input).println()
    }.also { it.println() }
}

