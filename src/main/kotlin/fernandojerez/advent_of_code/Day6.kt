package fernandojerez.advent_of_code

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicLong

data class Group(val daysLeft: Int, val quantity: Long = 1)
data class Shoal(val fishes: List<Group>)

fun main() {
    val input = object {}.javaClass.getResource("/fernandojerez/advent_of_code/day06.txt").readText().trim()
    val shoal = input.splitToSequence(",")
        .mapNotNull { it.toIntOrNull() }
        .groupBy {
            it
        }.asSequence()
        .map {
            Group(daysLeft = it.key, quantity = it.value.size.toLong())
        }.toList().let { Shoal(fishes = it) }

    val simulationPasses = 256
    (1..simulationPasses).fold(shoal) { currentShoal, _ ->
        runBlocking {
            val fishes = mutableListOf<Group>()
            val newFishes = AtomicLong(0)
            currentShoal.fishes.asFlow()
                .buffer()
                .map {
                    val group = it.copy(daysLeft = (it.daysLeft - 1).takeIf { d -> d >= 0 } ?: 6)
                    if (it.daysLeft == 0) group.quantity to group
                    else 0L to group
                }.collect {
                    newFishes.addAndGet(it.first)
                    fishes.add(it.second)
                }
            newFishes.get().takeIf { it > 0 }?.also { fishes.add(Group(daysLeft = 8, quantity = it)) }
            Shoal(fishes = fishes)
        }
    }.fishes.asSequence()
        .map { it.quantity }
        .sum().also {
            print(it)
        }
}
