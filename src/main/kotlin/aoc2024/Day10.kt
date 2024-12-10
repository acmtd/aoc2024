package aoc2024

enum class Direction(val dx: Int, val dy: Int) {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0),
}

data class Position(val x: Int, val y: Int) {
    fun isOnGrid(gridSize: Int) =
        (x >= 0 && y >= 0 && x < gridSize && y < gridSize)

    fun translate(d: Direction) = Position(x + d.dx, y + d.dy)
}

data class Point(val pos: Position, val height: Int) {
    fun isTrailhead() = (height == 0)
    fun isPeak() = (height == 9)

    fun moves(points: List<Point>, gridSize: Int): List<Point> {
        return buildList {
            add(pos.translate(Direction.UP))
            add(pos.translate(Direction.DOWN))
            add(pos.translate(Direction.LEFT))
            add(pos.translate(Direction.RIGHT))
        }.filter { pos -> pos.isOnGrid(gridSize) }
            .map { pos -> Point(pos, height + 1) }
            .filter { it in points }
    }
}

fun main() {
    fun parse(input: List<String>): List<Point> {
        return input.indices.flatMap { row ->
            input[row].indices.map { col ->
                Point(Position(col, row), input[row][col].digitToInt())
            }
        }
    }

    fun getScore(input: List<String>, part2: Boolean): Int {
        val points = parse(input)

        var score = 0

        points.filter { it.isTrailhead() }.forEach { th ->
            val queue = ArrayDeque<Point>()
            queue.add(th)

            val visited = mutableSetOf<Point>()

            while (queue.isNotEmpty()) {
                val point = queue.removeFirst()

                if (point !in visited || part2) {
                    visited.add(point)

                    if (point.isPeak()) {
                        score++
                    } else {
                        val newPositions = point.moves(points, input.size)

                        queue.addAll(newPositions)
                    }
                }
            }
        }

        return score
    }

    fun part1(input: List<String>): Int {
        return getScore(input, false)
    }

    fun part2(input: List<String>): Int {
        return getScore(input, true)
    }

    val testInput = readAsLines("Day10_test")
    check(part1(testInput) == 36)
    check(part2(testInput) == 81)

    val input = readAsLines("Day10")
    part1(input).println()
    part2(input).println()
}

