package day17

import java.io.FileInputStream
import kotlin.system.measureTimeMillis

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day17/${fileName}"))

val CAVE_WIDTH = 7
val AIR = '.'

data class Coord(var x: Int, var y: Int) {
    fun down() {
        y--
    }

    fun up() {
        y++
    }

    fun left() {
        x--
    }

    fun right() {
        x++
    }
}

data class Rock(val parts: List<Coord>) {
    fun down() {
        for (part in parts) {
            part.down()
        }
    }

    fun up() {
        for (part in parts) {
            part.up()
        }
    }


    fun left() {
        for (part in parts) {
            part.left()
        }
    }

    fun right() {
        for (part in parts) {
            part.right()
        }
    }

    fun canMoveRight(cave: MutableList<CharArray>): Boolean {
        if (parts.any { it.x + 1 == CAVE_WIDTH })
            return false
        for (part in parts) {
            if (part.y < cave.size && cave[part.y][part.x + 1] != '.')
                return false
        }
        return true
    }

    fun canMoveLeft(cave: MutableList<CharArray>): Boolean {
        if (parts.any { it.x == 0 })
            return false
        for (part in parts) {
            if (part.y < cave.size && cave[part.y][part.x - 1] != '.')
                return false
        }
        return true
    }

}


enum class RockShape {
    HorizontalLine,
    Cross,
    L,
    VerticalLine,
    Square;

    fun rockAt(startX: Int, startY: Int): Rock {
        val stones = mutableListOf<Coord>()

        when (this) {
            HorizontalLine -> {
                for (x in startX..startX + 3)
                    stones.add(Coord(x, startY))
            }

            VerticalLine -> {
                for (y in startY..startY + 3)
                    stones.add(Coord(startX, y))
            }

            Cross -> {
                stones.add(Coord(startX, startY))
                stones.add(Coord(startX, startY + 1))
                stones.add(Coord(startX - 1, startY + 1))
                stones.add(Coord(startX + 1, startY + 1))
                stones.add(Coord(startX, startY + 2))
            }

            L -> {
                stones.add(Coord(startX, startY))
                stones.add(Coord(startX + 1, startY))
                stones.add(Coord(startX + 2, startY))
                stones.add(Coord(startX + 2, startY + 1))
                stones.add(Coord(startX + 2, startY + 2))
            }

            Square -> {
                stones.add(Coord(startX, startY))
                stones.add(Coord(startX + 1, startY))
                stones.add(Coord(startX, startY + 1))
                stones.add(Coord(startX + 1, startY + 1))

            }
        }


        return Rock(stones)
    }
}


fun collision(rock: Rock, cave: MutableList<CharArray>): Boolean =
    rock.parts.any { it.y < 0 || (it.y < cave.size && cave[it.y][it.x] != AIR) }


fun appendRock(rock: Rock, cave: MutableList<CharArray>) {
    val high = rock.parts.maxOf { it.y }
    while (cave.size <= high)
        cave.add(CharArray(CAVE_WIDTH) { AIR })

    for (coord in rock.parts)
        cave[coord.y][coord.x] = '#'
}

fun dumpCave(cave: MutableList<CharArray>) {
    println()
    for (line in cave.asReversed()) {
        println(line.contentToString())
    }
}


class Game(val shifts: String) {
    var opIndex = 0

    val cave = mutableListOf<CharArray>()

    var nextShapeIndex = 0
    val shapes = RockShape.values()

    var landedRocks = 0L

    fun nextOp(): Char {
        val op = shifts[opIndex]
        opIndex = (opIndex + 1) % shifts.length
        return op
    }

    fun insertRock(y: Int): Rock {
        val shape = shapes[nextShapeIndex]
        nextShapeIndex = (nextShapeIndex + 1) % shapes.size
        val x = if (shape == RockShape.Cross) 3 else 2

        return shape.rockAt(x, y)
    }


    inline fun runUntil(condition: Game.() -> Boolean) {
        while (!condition()) {
            val highestOccupied = cave.size
            val rock = insertRock(highestOccupied + 3)
            do {
                when (val op = nextOp()) {
                    '>' -> {
                        if (rock.canMoveRight(cave))
                            rock.right()
                    }

                    '<' -> {
                        if (rock.canMoveLeft(cave))
                            rock.left()
                    }

                    else -> error("Bad op $op")
                }

                rock.down()
            } while (!collision(rock, cave))

            rock.up()
            landedRocks++
            afterRockHasFallen?.invoke(this)
            appendRock(rock, cave)
        }
    }

    var afterRockHasFallen: (Game.() -> Unit)? = null
}

fun main() {
    setIn("input.txt")

    val shifts = readln()
    var game = Game(shifts)

    println("Part 1")
    game.runUntil { landedRocks == 2022L }
    println(game.cave.size)

    // reset the state for part 2
    println("Part 2")
    game = Game(shifts)

    var shouldFinish = false
//    var pattern = null as String?
//    game.afterRockHasFallen = {
//        val str = dumpCaveAsLetters(game.cave)
//        pattern = findRepetition(str)
//        if (pattern != null)
//            shouldFinish = true
//    }

    game.runUntil { landedRocks == 10_000L }

    println("after 10_000 iters")
    var str = dumpCaveAsLetters(game.cave)
    var (startIndex, pattern) = findRepetition(str)!!

    println("Pattern")
    println(pattern)

    game = Game(shifts)
    game.runUntil { cave.size == startIndex + pattern.length && dumpCaveAsLetters(game.cave).endsWith(pattern) }

    str = dumpCaveAsLetters(game.cave)
    require(str.endsWith(pattern))

    println("fixed")
    val stones = game.landedRocks
    val double = pattern + pattern
    game.runUntil { cave.size == startIndex + double.length && dumpCaveAsLetters(game.cave).endsWith(double) }

    str = dumpCaveAsLetters(game.cave)
    require(str.endsWith(double))

    println("fixed double")
    val stonesNext = game.landedRocks
    val rocksPerRep = stonesNext - stones
    println(rocksPerRep)

    // part 2
    val limit = 1_000_000_000_000L
    // expected = 1_514_285_714_288


//    val str = dumpCaveAsLetters(game.cave)
//    findRepetition(str)
//
//    println(game.cave.size)
}

fun findRepetition(str: String): Pair<Int, String>? {
    val n = str.length
    var bestLen = -1
    var startIndex = -1
    val t = measureTimeMillis {
        len@ for (len in 10 until n) {
//            println("len=$len")
            start@ for (start in 0 until n - len) {
                for (rep in 0..2) {
                    val nextStart = start + rep * len
                    for (i in 0 until len) {
                        if (nextStart + i >= n) continue@start
                        if (str[start + i] != str[nextStart + i])
                            continue@start
                    }
                }
                bestLen = len
                startIndex = start
            }
        }
    }

//    println("Took ${t / 1000.0} seconds.")
    if (bestLen != -1) {
        val rep = str.substring(startIndex, startIndex + bestLen)
//        println("pattern:")
//        println(rep)
        return startIndex to rep
    } else {
//        println("Not found")
        return null
    }
}

fun dumpCaveAsLetters(cave: MutableList<CharArray>): String {
    val letters = mutableMapOf<String, Char>()
    val builder = StringBuilder()
    for (line in cave) {
        require(letters.size < 255)
        val c = letters.computeIfAbsent(line.contentToString()) { letters.size.toChar() }
        builder.append(c)
    }

    val str = builder.toString()
//    println(str)
    return str
}


