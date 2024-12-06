package aoc2024

class Direction(val deltaRow: Int, val deltaCol: Int) {
    companion object {
        val UP = Direction(-1, 0)
        val DOWN = Direction(1, 0)
        val LEFT = Direction(0, -1)
        val RIGHT = Direction(0, 1)
    }

    fun nextDirection(): Direction {
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

data class Position(val row: Int, val col: Int) {
    fun transform(direction: Direction): Position {
        return Position(row + direction.deltaRow, col + direction.deltaCol)
    }

    fun outOfBounds(grid: Array<CharArray>) =
        (col < 0 || row < 0 || col >= grid.first().size || row >= grid.size)
}

data class State(val pos: Position, val dir: Direction, val done: Boolean = false) {
    fun move(grid: Array<CharArray>): State {
        val nextPosition = pos.transform(dir)

        if (nextPosition.outOfBounds(grid)) {
            return State(pos, dir, true)
        }

        val content = grid[nextPosition.row][nextPosition.col]

        if (content == '#') {
            return State(pos, dir.nextDirection())
        } else {
            return State(nextPosition, dir)
        }
    }
}

fun main() {
    fun makeGrid(input: List<String>): Pair<Array<CharArray>, State> {
        val rows = input.size
        val cols = input.first().length

        val grid = Array(rows) { CharArray(cols) }

        var startPos = Position(0, 0)

        for ((row, line) in input.withIndex()) {
            for ((col, character) in line.withIndex()) {
                grid[row][col] = character

                if (character == '^') {
                    startPos = Position(row, col)
                }
            }
        }
        // we start by going up
        return Pair(grid, State(startPos, Direction.UP))
    }


    fun part1(input: List<String>): Int {
        val (grid, initialState) = makeGrid(input)

        var state = initialState

        val visited = mutableSetOf<State>().apply { add(initialState) }

        do {
            state = state.move(grid)
            visited.add(state)
        } while (!state.done)

        return visited.map { it.pos }.distinct().count()
    }

    val testInput = readAsLines("Day06_test")
    check(part1(testInput) == 41)

    val input = readAsLines("Day06")
    part1(input).println() // 4515
}

