package day9

import kotlin.math.max
import kotlin.math.min

fun main() {
    setIn("input.txt")

    val head = Coord(0, 0)
    val tail = List(9) { Coord(0, 0) }

    dump(head, tail)

    val positions = mutableSetOf(tail.last().copy())

    var cmd = readlnOrNull()
    while (cmd != null) {
        val (left, right) = cmd.split(' ')
        val dir = left[0]
        val iter = right.toInt()

        println("---$cmd---")

        repeat(iter) {

            head.move(dir)
            var prev = head
            for (elem in tail) {
                if (elem.shouldMove(prev)) {
                    elem.moveCloser(prev)
                    prev = elem
                } else break
            }
            positions.add(tail.last().copy())
            dump(head, tail)
        }

        cmd = readlnOrNull()
    }

    println(positions)
    println(positions.size)
}


fun dump(head: Coord, tail: List<Coord>) {
    val bottom = min(min(head.x, tail.minOf { it.x }), 0)
    val top = max(max(head.x, tail.maxOf { it.x }), 4)
    val left = min(min(head.y, tail.minOf { it.y }), 0)
    val right = max(max(head.y, tail.maxOf { it.y }), 5)

    val taken = mutableSetOf<Coord>()

    for (row in top downTo bottom) {
        l@ for (col in left..right) {
            var printed = false
            if (head.x == row && head.y == col) {
                print('H')
                taken.add(Coord(row, col))
                printed = true
            }
            for ((i, elem) in tail.withIndex()) {
                if (elem.x == row && elem.y == col) {
                    if (taken.add(Coord(row, col))) {
                        print(i + 1)
                        printed = true
                    }

                }
            }
            if (!printed)
                print('.')
        }
        println()
    }
    println()
}