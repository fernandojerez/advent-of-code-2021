package fernandojerez.advent_of_code

import kotlin.math.*

data class Point(val x: Int, val y: Int)

fun String.toPoint(): Point? {
    val (x, y) = this.trim().split(",")
    return try {
        Point(x = x.toInt(), y = y.toInt())
    } catch (e: Throwable) {
        null
    }
}

fun Point.has45Angle(end: Point): Boolean {
    val (x1, y1) = this
    val (x2, y2) = end
    return ceil(Math.toDegrees(atan2(x2.toDouble() - x1.toDouble(), y2.toDouble() - y1.toDouble())))
        .toInt() % 45 == 0
}

fun Point.createDiagonal(end: Point): Sequence<Point>? {
    val (x1, y1) = this
    val (x2, y2) = end
    val xDir = 1.takeIf { x1 < x2 } ?: -1
    val yDir = 1.takeIf { y1 < y2 } ?: -1

    return (0..abs(x1 - x2)).zip(0..abs(y1 - y2)) { x, y ->
        Point(x1 + x * xDir, y1 + y * yDir)
    }.asSequence()
}

fun Sequence<Pair<Point, Point>>.buildBoard(alsoDiagonal: Boolean): Map<Point, Int> {
    return this.mapNotNull { (startPoint, endPoint) ->
        val (x1, y1) = startPoint
        val (x2, y2) = endPoint
        when {
            x1 == x2 -> (min(y1, y2)..max(y1, y2)).asSequence().map { Point(x = x1, y = it) }
            y1 == y2 -> (min(x1, x2)..max(x1, x2)).asSequence().map { Point(x = it, y = y1) }
            alsoDiagonal and startPoint.has45Angle(endPoint) -> startPoint.createDiagonal(endPoint)
            else -> null
        }
    }.flatten()
        .fold(mutableMapOf<Point, Int>()) { map, point ->
            map[point]?.let {
                map.put(point, it + 1)
            } ?: map.put(point, 1)
            map
        }
}

fun main() {
    val input = object {}.javaClass.getResource("/fernandojerez/advent_of_code/day05.txt").readText().trim()
    input.splitToSequence("\n")
        .mapNotNull {
            val (start, end) = it.split(" -> ")
            val startPoint = start.toPoint()
            val endPoint = end.toPoint()
            if (startPoint == null || endPoint == null) null
            else startPoint to endPoint
        }.buildBoard(true)
        .entries.asSequence().filter {
            it.value >= 2
        }.count().also {
            println(it)
        }
}
