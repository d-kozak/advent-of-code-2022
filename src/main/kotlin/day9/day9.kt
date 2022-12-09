package day9

import java.io.FileInputStream
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day9/${fileName}"))


data class Coord(var x: Int, var y: Int) {

    operator fun minus(other: Coord): Coord {
        return Coord(this.x - other.x, this.y - other.y)
    }

    fun move(dir: Char) {
        when (dir) {
            'R' -> y++
            'L' -> y--
            'U' -> x++
            'D' -> x--
            else -> error("Unknown move $dir")
        }
    }

    fun shouldMove(other: Coord): Boolean {
        val vec = other - this
        return when {
            vec.x == 0 -> abs(vec.y) > 1
            vec.y == 0 -> abs(vec.x) > 1
            else -> abs(vec.x) != abs(vec.y) || abs(vec.x) > 1
        }
    }

    fun moveCloser(target: Coord) {
        val vec = target - this
        when {
            vec.x == 0 -> this.y += max(min(1, vec.y), -1)
            vec.y == 0 -> this.x += max(min(1, vec.x), -1)
            else -> {
                this.y += max(min(1, vec.y), -1)
                this.x += max(min(1, vec.x), -1)
            }
        }
    }

    override fun toString(): String {
        return "{$x,$y}"
    }
}

fun main() {
    setIn("input.txt")

    val head = Coord(0, 0)
    val tail = Coord(0, 0)

//    dump(head,tail)

    val positions = mutableSetOf(tail.copy())

    var cmd = readlnOrNull()
    while (cmd != null) {
        val (left, right) = cmd.split(' ')
        val dir = left[0]
        val iter = right.toInt()

        println("---$cmd---")

        repeat(iter) {
            head.move(dir)
            if (head.shouldMove(tail)) {
                tail.moveCloser(head)
                positions.add(tail.copy())
            }
//            dump(head, tail)
        }
        cmd = readlnOrNull()
    }

    println(positions)
    println(positions.size)
}

fun dump(head: Coord, tail: Coord) {
    val top = max(max(head.x, tail.x), 4)
    val right = max(max(head.y, tail.y), 5)
    for (line in top downTo 0) {
        for (col in 0..right) {
            when {
                head.x == line && head.y == col -> print('H')
                tail.x == line && tail.y == col -> print('T')
                else -> print('.')
            }
        }
        println()
    }
    println()
}
