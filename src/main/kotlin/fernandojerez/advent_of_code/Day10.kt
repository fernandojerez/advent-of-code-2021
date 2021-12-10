package fernandojerez.advent_of_code

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicLong

val closeSigns = setOf(')', '}', ']', '>')
val openSigns = mapOf(
    ')' to '(',
    '}' to '{',
    ']' to '[',
    '>' to '<'
)
val signValue = mapOf(
    ')' to 3,
    ']' to 57,
    '}' to 1197,
    '>' to 25137
)
val signScore = mapOf(
    '(' to 1,
    '[' to 2,
    '{' to 3,
    '<' to 4
)

fun String.checkLine(): Pair<Char?, ArrayDeque<Char>> {
    val stack = ArrayDeque<Char>()
    return Pair(this.asSequence().mapNotNull { c ->
        when {
            closeSigns.contains(c) -> c.takeIf { openSigns[c] != stack.removeLastOrNull() }
            else -> {
                stack.addLast(c)
                null
            }
        }
    }.firstOrNull(), stack)
}

fun main() {
    val input = object {}.javaClass.getResource("/fernandojerez/advent_of_code/day10.txt").readText().trim()
    val lines = input.splitToSequence("\n")

    //part 1
    runBlocking {
        val collector = AtomicLong(0)
        lines
            .asFlow()
            .buffer()
            .mapNotNull {
                it.checkLine().first
            }
            .map { signValue[it]!! }
            .collect {
                collector.addAndGet(it.toLong())
            }
        println(collector)
    }

    //part 2
    runBlocking {
        val result = mutableListOf<Long>()
        lines
            .asFlow()
            .buffer()
            .mapNotNull {
                val process = it.checkLine()
                process.second.takeIf { process.first == null }
            }.map {
                it.asReversed()
                    .fold(0L) { acc, v ->
                        acc * 5L + signScore[v]!!.toLong()
                    }
            }.collect {
                result.add(it)
            }
        result.sort()
        println(result[result.size / 2])
    }
}
