package aoc2024

fun main() {
    fun horizontalsAndVerticals(input: List<String>): List<String> {
        return mutableListOf<String>().apply {
            addAll(input)
            addAll(input.map { it.reversed() })

            val verticals = input.indices.map { col -> input.map { it[col] }.joinToString("") }

            addAll(verticals)
            addAll(verticals.map { it.reversed() })
        }
    }

    fun perimeter(input: List<String>): MutableSet<Pair<Int, Int>> {
        val coordinates = mutableSetOf<Pair<Int, Int>>()

        input.indices.forEach { row ->
            coordinates += Pair(row, 0)
            coordinates += Pair(0, row)
            coordinates += Pair(input.size - 1, row)
            coordinates += Pair(row, input.size - 1)
        }

        return coordinates
    }

    data class Transform(val deltaX: Int, val deltaY: Int) {
        fun isPossible(pos: Pair<Int, Int>, maxVal: Int): Boolean {
            if (pos.first + deltaX < 0 || pos.first + deltaX > maxVal) return false
            if (pos.second + deltaY < 0 || pos.second + deltaY > maxVal) return false
            return true
        }

        fun apply(p: Pair<Int, Int>) = Pair(p.first + deltaX, p.second + deltaY)

        fun apply(p: Pair<Int, Int>, iterations: Int): Pair<Int, Int> {
            var p2 = p

            repeat(iterations) {
                p2 = apply(p2)
            }

            return p2
        }
    }

    val transforms = listOf(
        Transform(1, 1),
        Transform(1, -1),
        Transform(-1, -1),
        Transform(-1, 1)
    )

    fun diagonalValue(p: Pair<Int, Int>, t: Transform, s: String, input: List<String>): String {
        return if (t.isPossible(p, input.size - 1)) {
            diagonalValue(t.apply(p), t, s + input[p.first][p.second], input)
        } else {
            s + input[p.first][p.second]
        }
    }

    fun diagonals(input: List<String>) =
        perimeter(input).flatMap { p ->
            transforms.filter { t -> t.isPossible(p, input.size - 1) }
                .map { t -> diagonalValue(p, t, "", input) }
        }

    fun samFinder(
        pos: Pair<Int, Int>, t: Transform, s: String, startIdx: Int, found: MutableList<Pair<Int, Int>>
    ): List<Pair<Int, Int>> {
        val index = s.indexOf("SAM", startIdx)
        if (index == -1) return found

        found.add(t.apply(pos, index + 1))
        return samFinder(pos, t, s, index + 1, found)
    }

    fun diagonalSams(input: List<String>): List<Pair<Int, Int>> {
        return mutableListOf<Pair<Int, Int>>().apply {
            perimeter(input).forEach { p ->
                transforms.filter { t -> t.isPossible(p, input.size - 1) }.forEach { t ->
                    samFinder(p, t, diagonalValue(p, t, "", input), 0, this)
                }
            }
        }
    }

    fun lines(input: List<String>): List<String> {
        return horizontalsAndVerticals(input) + diagonals(input)
    }

    fun part1(input: List<String>): Int {
        return lines(input).sumOf { it.split("XMAS").size - 1 }
    }

    fun part2(input: List<String>): Int {
        return diagonalSams(input).groupingBy { it }.eachCount().filter { it.value == 2 }.size
    }

    val testInput = readAsLines("Day04_test")
    check(part1(testInput) == 18)

    val testInput2 = readAsLines("Day04_test2")
    check(part2(testInput2) == 9)

    val input = readAsLines("Day04")
    part1(input).println() // 2567
    part2(input).println() // 2029
}

