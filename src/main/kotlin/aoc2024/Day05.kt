package aoc2024

fun main() {
    fun <T> List<T>.swap(idx1: Int, idx2: Int): List<T> {
        val newList = mutableListOf<T>()
        newList.addAll(this)

        val t = this[idx1]
        newList[idx1] = this[idx2]
        newList[idx2] = t

        return newList
    }

    data class Page(val pages: List<Int>) {
        fun middlePage() = pages[-1 + (pages.size + 1) / 2]
        fun swapItems(i1: Int, i2: Int): Page {
            return Page(pages.swap(pages.indexOf(i1), pages.indexOf(i2)))
        }
    }

    data class Rule(val before: Int, val after: Int) {
        fun appliesToPage(page: Page) = page.pages.contains(before) && page.pages.contains(after)
        fun testPage(page: Page) = page.pages.indexOf(before) < page.pages.indexOf(after)
    }

    fun getRulesAndPages(input: List<String>): Pair<List<Rule>, List<Page>> {
        val (rulesInput, pagesInput) = input

        val rules = rulesInput.split("\n").map { line -> line.split("|").map { it.toInt() } }
            .map { Rule(it[0], it[1]) }

        val pages = pagesInput.split("\n").map { line -> line.split(",").map { it.toInt() } }
            .map { Page(it) }
        return Pair(rules, pages)
    }

    fun part1(input: List<String>): Int {
        val (rules, pages) = getRulesAndPages(input)

        return pages.filter { page ->
            rules.filter { it.appliesToPage(page) }.all { it.testPage(page) }
        }.sumOf { it.middlePage() }
    }

    fun part2(input: List<String>): Int {
        val (rules, pages) = getRulesAndPages(input)

        return pages.filterNot { page ->
            rules.filter { it.appliesToPage(page) }.all { it.testPage(page) }
        }.sumOf { page ->
            val rulesToApply = rules.filter { it.appliesToPage(page) }

            var perm = page

            while (rulesToApply.any { !it.testPage(perm) }) {
                rulesToApply.first { !it.testPage(perm) }.apply { perm = perm.swapItems(this.before, this.after) }
            }

            perm.middlePage()
        }
    }

    val testInput = readAsBlocks("Day05_test")
    check(part1(testInput) == 143)
    check(part2(testInput) == 123)

    val input = readAsBlocks("Day05")
    part1(input).println()
    part2(input).println()
}

