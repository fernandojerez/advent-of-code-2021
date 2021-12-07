package fernandojerez.advent_of_code

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.abs
import kotlin.math.min

suspend fun Sequence<Long>.sumValues(align: Long, versionOne: Boolean): Long {
    val result = AtomicLong(0)
    this.asFlow()
        .buffer()
        .map { n ->
            val value = abs(n - align)
            value.takeIf { versionOne } ?: ((value * (value + 1)) / 2)
        }
        .collect {
            result.addAndGet(it)
        }
    return result.get()
}

fun main() {
    val input = object {}.javaClass.getResource("/fernandojerez/advent_of_code/day07.txt").readText().trim()
    val numbers = input.splitToSequence(",")
        .mapNotNull { it.toLongOrNull() }
    val max = numbers.maxOf { it }

    runBlocking {
        val result = AtomicLong(-1)
        (1 until max)
            .asFlow()
            .buffer()
            .map {
                numbers.sumValues(it, false)
            }.collect {
                if (result.get() == -1L) result.set(it)
                else result.set(min(it, result.get()))
            }
        println(result)
    }
}
