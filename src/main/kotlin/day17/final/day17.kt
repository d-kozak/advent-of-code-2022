package day17.final

import java.io.FileInputStream
import kotlin.math.max

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day17/${fileName}"))

data class Coord(val x: Long, val y: Long) {
    fun up() = Coord(x, y + 1)
    fun down() = Coord(x, y - 1)

    fun left() = Coord(x - 1, y)
    fun right() = Coord(x + 1, y)
}

enum class RockShape {
    HorizontalLine,
    Cross,
    L,
    VerticalLine,
    Square
}

fun nextStone(rockShape: RockShape, y: Long): Set<Coord> = when (rockShape) {
    RockShape.HorizontalLine -> setOf(Coord(2, y), Coord(3, y), Coord(4, y), Coord(5, y))
    RockShape.Cross -> setOf(Coord(3, y), Coord(2, y + 1), Coord(3, y + 1), Coord(4, y + 1), Coord(3, y + 2))
    RockShape.L -> setOf(Coord(2, y), Coord(3, y), Coord(4, y), Coord(4, y + 1), Coord(4, y + 2))
    RockShape.VerticalLine -> setOf(Coord(2, y), Coord(2, y + 1), Coord(2, y + 2), Coord(2, y + 3))
    RockShape.Square -> setOf(Coord(2, y), Coord(3, y), Coord(2, y + 1), Coord(3, y + 1))
}


data class State(val shiftIndex: Int, val stoneIndex: Int, val topStones: Set<Coord>)

fun main() {
    setIn("input.txt")
    val shifts = readln()
    var shiftIndex = 0

    val PART_1_LIMIT = 2022L
    val PART_2_LIMIT = 1_000_000_000_000
    val limit = PART_2_LIMIT

    var stones = 0L
    var top = 0L
    val allStones = (0L..6).map { Coord(it, 0) }.toMutableSet()

    var added = 0L

    val seen = mutableMapOf<State, Pair<Long, Long>>()

    while (stones < limit) {
        var currStone = nextStone(RockShape.values()[(stones % RockShape.values().size).toInt()], top + 4)


        var settled = false
        while (!settled) {
            when (val op = shifts[shiftIndex]) {
                '>' -> {
                    currStone = right(currStone)
                    if (currStone.any { allStones.contains(it) }) {
                        currStone = left(currStone)
                    }
                }

                '<' -> {
                    currStone = left(currStone)
                    if (currStone.any { allStones.contains(it) }) {
                        currStone = right(currStone)
                    }
                }

                else -> error("Unknown op $op")
            }
            shiftIndex = (shiftIndex + 1) % shifts.length

            currStone = down(currStone)
            if (currStone.any { allStones.contains(it) })
                settled = true
        }

        currStone = up(currStone)

        allStones.addAll(currStone)

        top = max(top, currStone.maxOf { it.y })
        stones++

        if (stones == PART_1_LIMIT) {
            dump(allStones)
            println(top)
        }

        val state = State(shiftIndex, (stones % RockShape.values().size).toInt(), getTopN(allStones))
        val prev = seen[state]
        if (prev != null && stones > PART_1_LIMIT) {
            println("cycle")
            val (prevStones, prevTop) = prev
            val dStones = stones - prevStones
            val dTop = top - prevTop
            val x = (limit - stones) / dStones
            stones += x * dStones
            added += x * dTop
//            top += x * dTop
        } else {
            seen[state] = stones to top
        }
    }


    println(top + added)
}

fun getTopN(allStones: MutableSet<Coord>): Set<Coord> {
    val top = allStones.maxOf { it.y }
    return allStones.filter { top - it.y <= 30 }
        .map { Coord(it.x, top - it.y) }
        .toSet()
}

fun dump(allStones: MutableSet<Coord>) {

}

fun left(currStone: Set<Coord>): Set<Coord> =
    if (currStone.any { it.x == 0L }) currStone else currStone.map { it.left() }.toSet()

fun right(currStone: Set<Coord>): Set<Coord> =
    if (currStone.any { it.x == 6L }) currStone else currStone.map { it.right() }.toSet()

fun up(currStone: Set<Coord>): Set<Coord> = currStone.map { it.up() }.toSet()
fun down(currStone: Set<Coord>): Set<Coord> = currStone.map { it.down() }.toSet()


