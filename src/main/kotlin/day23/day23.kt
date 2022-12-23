package day23

import java.io.FileInputStream
import kotlin.math.max
import kotlin.math.min

data class Coord(val r: Int, val c: Int) {
    operator fun plus(dir: Direction) = Coord(r + dir.dr, c + dir.dc)

    fun adj() = sequence {
        for (dr in listOf(-1, 0, 1)) {
            for (dc in listOf(-1, 0, 1)) {
                if (dr == 0 && dc == 0) continue
                yield(Coord(r + dr, c + dc))
            }
        }
    }
}

interface Direction {
    val dr: Int
    val dc: Int
}

enum class BaseDirection(override val dr: Int, override val dc: Int) : Direction {
    N(-1, 0),
    S(1, 0),
    W(0, -1),
    E(0, 1);

    fun nei() = sequence<Direction> {
        when (this@BaseDirection) {
            N -> {
                yield(OtherDirection.NW)
                yield(N)
                yield(OtherDirection.NE)
            }

            S -> {
                yield(OtherDirection.SW)
                yield(S)
                yield(OtherDirection.SE)
            }

            W -> {
                yield(OtherDirection.NW)
                yield(W)
                yield(OtherDirection.SW)
            }

            E -> {
                yield(OtherDirection.NE)
                yield(E)
                yield(OtherDirection.SE)
            }
        }
    }
}

enum class OtherDirection(override val dr: Int, override val dc: Int) : Direction {
    NW(-1, -1),
    NE(-1, 1),
    SW(1, -1),
    SE(1, 1)
}


fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day23/${fileName}"))

fun main() {
    setIn("input.txt")

    var r = 0
    var elves = mutableSetOf<Coord>()

    var TOP_R = 0
    var BOTTOM_R = r - 1
    var LEFT_C = 0
    var RIGHT_C = 0


    var line = readlnOrNull()
    while (line != null) {
        for (c in line.indices) {
            if (line[c] == '#') {
                elves.add(Coord(r, c))
                RIGHT_C = max(RIGHT_C, c)
            }
        }
        BOTTOM_R = r
        r++
        line = readlnOrNull()
    }


    fun dump() {
        for (r in TOP_R..BOTTOM_R) {
            for (c in LEFT_C..RIGHT_C) {
                print(if (Coord(r, c) in elves) '#' else '.')
            }
            println()
        }
        println()
    }

//    println("Before")
//    dump()

    val dirs = BaseDirection.values()
    val D = dirs.size
    var currentDir = 0

    var move = 1

    while (true) {
        val collisions = mutableMapOf<Coord, Int>()
        val wantedDirections = mutableMapOf<Coord, Coord>()

        for (elf in elves) {

            if (elf.adj().none { it in elves })
                continue

            l@ for (i in 0 until D) {
                val j = (currentDir + i) % D
                for (n in dirs[j].nei()) {
                    if ((elf + n) in elves)
                        continue@l
                }
                val nextCoord = elf + dirs[j]
                collisions[nextCoord] = (collisions[nextCoord] ?: 0) + 1
                wantedDirections[elf] = nextCoord
                break
            }
        }

        val nextElves = mutableSetOf<Coord>()

        var anyMovement = false

        for (elf in elves) {
            val nextCoord = wantedDirections[elf]
            if (nextCoord == null || collisions[nextCoord]!! > 1)
                nextElves.add(elf)
            else {
                nextElves.add(nextCoord)
                anyMovement = true
                TOP_R = min(TOP_R, nextCoord.r)
                BOTTOM_R = max(BOTTOM_R, nextCoord.r)
                LEFT_C = min(LEFT_C, nextCoord.c)
                RIGHT_C = max(RIGHT_C, nextCoord.c)
            }
        }

        if (!anyMovement) break

        currentDir = (currentDir + 1) % D
        elves = nextElves

//        println("After move $move")
//        dump()

        if (move == 10) {
            var empty = 0

            for (r in TOP_R..BOTTOM_R) {
                for (c in LEFT_C..RIGHT_C) {
                    if (Coord(r, c) !in elves)
                        empty++
                }
            }

            println("Part 1")
            println(empty)
        }

        move++
    }

    println("Part 2")
    dump()
    println(move)
}











