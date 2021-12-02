package fernandojerez.advent_of_code

data class Position(
    val depth: Int = 0,
    val horizontal: Int = 0,
    val aim: Int = 0
)

enum class Direction {
    FORWARD, UP, DOWN
}

fun moveHorizontal(position: Position, value: Int): Position {
    return position.copy(horizontal = position.horizontal + value)
}

fun moveDown(position: Position, value :Int): Position {
    return position.copy(depth = position.depth + value)
}

fun moveUp(position: Position, value :Int): Position {
    return position.copy(depth = position.depth - value)
}

fun moveAimHorizontal(position: Position, value: Int): Position {
    return position.copy(horizontal = position.horizontal + value,
        depth = position.depth + position.aim * value)
}

fun moveAimDown(position: Position, value :Int): Position {
    return position.copy(aim = position.aim + value)
}

fun moveAimUp(position: Position, value :Int): Position {
    return position.copy(aim = position.aim - value)
}

fun main() {
    val input = object {}.javaClass.getResource("/fernandojerez/advent_of_code/day02.txt").readText().trim()
    val movements = mapOf(
        Direction.FORWARD to ::moveAimHorizontal,
        Direction.UP to ::moveAimUp,
        Direction.DOWN to ::moveAimDown
    )
    input.splitToSequence("\n")
        .fold(Position()){ position, instruction ->
            val (movement, value) = instruction.split(" ")
            try {
                val direction = Direction.valueOf(movement.uppercase())
                value.toIntOrNull()?.let { movements[direction]!!(position, it) }
                    ?: position
            } catch(e: Throwable){
                position
            }
        }.also {
            println(it.depth * it.horizontal)
        }
}
