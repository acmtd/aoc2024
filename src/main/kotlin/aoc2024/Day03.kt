package aoc2024

fun main() {
    fun getScore(input: String): Int {
        return Regex("""mul\((\d+),(\d+)\)""").findAll(input).sumOf { mul ->
            mul.groupValues.drop(1).map { it.toInt() }.reduce(Int::times)
        }
    }

    fun part1(input: String): Int {
        return getScore(input)
    }

    fun part2(input: String): Int {
        return input.split(Regex("do\\(\\)")).sumOf {
            getScore(it.split(Regex("don't\\(\\)")).first())
        }
    }

    check(part1("xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))") == 161)
    check(part2("xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))") == 48)

    val input = readAsString("Day03")
    part1(input).println()
    part2(input).println()
}

