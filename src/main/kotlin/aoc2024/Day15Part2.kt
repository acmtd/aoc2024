package aoc2024

import aoc2024.Day15Part2.*

class Day15Part2 {
    class Warehouse(val input: String) {
        private val items = input.lines().mapIndexed { y, line ->
            line.flatMapIndexed { x, char ->
                when (char) {
                    'O' -> listOf(BigBox(Vec2(x * 2, y)))
                    '#' -> listOf(Wall(Vec2(x * 2, y)), Wall(Vec2(x * 2 + 1, y)))
                    '@' -> listOf(Robot(Vec2(x * 2, y)))
                    else -> emptyList()
                }
            }
        }.flatten()

        private val maxX = items.map { it.pos }.maxOf { it.x }
        private val maxY = items.map { it.pos }.maxOf { it.y }

        private val robot = items.first { it is Robot }

        private fun moveableItems(dir: Vec2, item: Item, maybeMovable: Set<Item>): Set<Item> {
            val newPositions = buildList {
                if (item is BigBox) {
                    addAll(item.positions().map { it + dir })
                } else {
                    add(item.pos + dir)
                }
            }

            if (newPositions.any { it in items.filterIsInstance<Wall>().map { wall -> wall.pos } }) return emptySet()

            val nextBoxes =
                items.filterIsInstance<BigBox>().filter { it != item && it.positions().any { p -> p in newPositions } }

            if (nextBoxes.isEmpty()) return maybeMovable

            return nextBoxes.flatMap { box ->
                val nextForBox = moveableItems(dir, box, maybeMovable + box)
                nextForBox.ifEmpty { return emptySet() }
            }.toSet()
        }

        fun makeMove(dir: Vec2) {
            moveableItems(dir, robot, setOf(robot)).forEach { item -> item.pos += dir }
        }

        fun gps(): Int {
            return items.filterIsInstance<BigBox>().sumOf { it.pos.y * 100 + it.pos.x }
        }

        fun draw() {
            var lastWasBigBox = false

            val map = items.associateBy { it.pos }

            for (y in 0..maxY) {
                for (x in 0..maxX) {
                    val item = map[Vec2(x, y)]
                    when (item) {
                        null -> {
                            if (!lastWasBigBox) {
                                print(".")
                            }
                            lastWasBigBox = false

                        }

                        is Wall -> {
                            print("#")
                        }

                        is BigBox -> {
                            print("[]")
                            lastWasBigBox = true
                        }

                        is Robot -> {
                            print("@")
                        }
                    }
                }
                println("")
            }
        }
    }

    sealed class Item(var pos: Vec2) {
        abstract fun canMove(): Boolean
    }

    class BigBox(pos: Vec2) : Item(pos) {
        override fun canMove() = true
        fun positions() = listOf(pos, Vec2(pos.x + 1, pos.y))
    }

    class Wall(pos: Vec2) : Item(pos) {
        override fun canMove() = false
    }

    class Robot(pos: Vec2) : Item(pos) {
        override fun canMove() = true
    }

    data class Vec2(val x: Int, val y: Int) {
        companion object {
            fun move(char: Char) = when (char) {
                '^' -> Vec2(0, -1)
                'v' -> Vec2(0, 1)
                '<' -> Vec2(-1, 0)
                '>' -> Vec2(1, 0)
                else -> null
            }
        }

        operator fun plus(offset: Vec2) = Vec2(x + offset.x, y + offset.y)
    }
}

fun part2(input: List<String>): Int {
    val warehouse = Warehouse(input.first())
    val moveList = input.last().mapNotNull { Vec2.move(it) }

    moveList.forEach { warehouse.makeMove(it) }

    println("END POSITION")
    warehouse.draw()
    return warehouse.gps()
}

fun main() {
    val testInput = readAsBlocks("Day15_test")
    check(part2(testInput) == 9021)

    val input = readAsBlocks("Day15")
    part2(input).println()
}