package aoc2024

fun main() {
    fun horizontals(input: List<String>): List<String> {
        return mutableListOf<String>().apply {
            addAll(input)
            addAll(input.map { it.reversed() })
        }
    }

    fun verticals(input: List<String>): List<String> {
        return mutableListOf<String>().apply {
            val verticals = input.indices.map { col -> input.map { it[col] }.joinToString("") }

            addAll(verticals)
            addAll(verticals.map { it.reversed() })
        }
    }

    fun diagonals(input: List<String>): List<String> {
        return mutableListOf<String>().apply {
            val coordinates = mutableSetOf<Pair<Int, Int>>()

            val maxVal = input.size - 1

            input.indices.forEach { row ->
                coordinates += Pair(row, 0)
                coordinates += Pair(0, row)
                coordinates += Pair(maxVal, row)
                coordinates += Pair(row, maxVal)
            }

            coordinates.forEach { p ->
                if (p.second < maxVal && p.first < maxVal) {
                    var x = p.first
                    var y = p.second

                    var s = ""

                    while (x <= maxVal && y <= maxVal) {
                        s += input[x][y]
                        x++
                        y++
                    }

                    add(s)
                }

                if (p.second < maxVal && p.first > 0) {
                    var x = p.first
                    var y = p.second

                    var s = ""

                    while (x >= 0 && y <= maxVal) {
                        s += input[x][y]
                        x--
                        y++
                    }

                    add(s)
                }

                if (p.second > 0 && p.first < maxVal) {
                    var x = p.first
                    var y = p.second

                    var s = ""

                    while (x <= maxVal && y >= 0) {
                        s += input[x][y]
                        x++
                        y--
                    }

                    add(s)
                }

                if (p.second > 0 && p.first > 0) {
                    var x = p.first
                    var y = p.second

                    var s = ""

                    while (x >= 0 && y >= 0) {
                        s += input[x][y]
                        x--
                        y--
                    }

                    add(s)
                }
            }
        }
    }

    fun diagonalSams(input: List<String>): List<Pair<Int, Int>> {
        return mutableListOf<Pair<Int, Int>>().apply {
            val coordinates = mutableSetOf<Pair<Int, Int>>()

            val maxVal = input.size - 1

            input.indices.forEach { row ->
                coordinates += Pair(row, 0)
                coordinates += Pair(0, row)
                coordinates += Pair(maxVal, row)
                coordinates += Pair(row, maxVal)
            }

            coordinates.forEach { p ->
                if (p.second < maxVal && p.first < maxVal) {
                    var x = p.first
                    var y = p.second

                    var s = ""

                    while (x <= maxVal && y <= maxVal) {
                        s += input[x][y]
                        x++
                        y++
                    }

                    var index = 0

                    while (index > -1) {
                        index = s.indexOf("SAM", index)
                        if (index > -1) {
                            add(Pair(p.first + index + 1, p.second + index + 1))
                            index++
                        }
                    }
                }

                if (p.second < maxVal && p.first > 0) {
                    var x = p.first
                    var y = p.second

                    var s = ""

                    while (x >= 0 && y <= maxVal) {
                        s += input[x][y]
                        x--
                        y++
                    }

                    var index = 0

                    while (index > -1) {
                        index = s.indexOf("SAM", index)
                        if (index > -1) {
                            add(Pair(p.first - index - 1, p.second + index + 1))
                            index++
                        }
                    }
                }

                if (p.second > 0 && p.first < maxVal) {
                    var x = p.first
                    var y = p.second

                    var s = ""

                    while (x <= maxVal && y >= 0) {
                        s += input[x][y]
                        x++
                        y--
                    }

                    var index = 0

                    while (index > -1) {
                        index = s.indexOf("SAM", index)
                        if (index > -1) {
                            add(Pair(p.first + index + 1, p.second - index - 1))
                            index++
                        }
                    }
                }

                if (p.second > 0 && p.first > 0) {
                    var x = p.first
                    var y = p.second

                    var s = ""

                    while (x >= 0 && y >= 0) {
                        s += input[x][y]
                        x--
                        y--
                    }

                    var index = 0

                    while (index > -1) {
                        index = s.indexOf("SAM", index)
                        if (index > -1) {
                            add(Pair(p.first - index - 1, p.second - index - 1))
                            index++
                        }
                    }
                }
            }
        }
    }

    fun lines(input: List<String>): List<String> {
        return mutableListOf<String>().apply {
            addAll(horizontals(input))
            addAll(verticals(input))
            addAll(diagonals(input))
        }
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

