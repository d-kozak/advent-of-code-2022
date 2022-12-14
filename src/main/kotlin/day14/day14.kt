package day14

import java.io.FileInputStream
import kotlin.math.min
import kotlin.math.max

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day14/${fileName}"))

data class Coord(val col: Int, val row: Int) {
    fun down(): Coord = Coord(col, row + 1)

    fun diagLeft() = Coord(col - 1, row + 1)
    fun diagRight() = Coord(col + 1, row + 1)
    fun steps(): Sequence<Coord> = sequence {
        yield(down())
        yield(diagLeft())
        yield(diagRight())
    }


}

const val delim = " -> "
fun parseStone(line: String): List<Coord> = line.split(delim)
    .map {
        it.split(",")
            .map { it.toInt() }
    }
    .map { Coord(it[0], it[1]) }


enum class Cell(val c: Char) {
    SAND('o'),
    AIR('.'),
    STONE('#');

    override fun toString(): String {
        return c.toString()
    }
}

data class Grid(val items: MutableMap<Coord, Cell> = mutableMapOf()) {

    var maxCol = Int.MIN_VALUE
    var minCol = Int.MAX_VALUE
    var minRow = 0
    var maxRow = Int.MIN_VALUE
    //                                              for part 2
    operator fun get(coord: Coord): Cell = if (coord.row == maxRow + 2) Cell.STONE else items[coord] ?: Cell.AIR

    operator fun set(coord: Coord, cell: Cell) {
        items[coord] = cell

        minCol = min(minCol,coord.col)
        maxCol = max(maxCol,coord.col)
    }

    fun addStone(stone: List<Coord>) {
        val n = stone.size
        for (i in 0 until n - 1) {
            val from = stone[i]
            val to = stone[i + 1]
            if (from.col == to.col) {
                val lo = min(from.row, to.row)
                val hi = max(from.row, to.row)
                for (row in lo..hi)
                    this[Coord(from.col, row)] = Cell.STONE
            } else if (from.row == to.row) {
                val lo = min(from.col, to.col)
                val hi = max(from.col, to.col)

                for (col in lo..hi)
                    this[Coord(col, from.row)] = Cell.STONE
            } else {
                error("Cannot handle $from $to")
            }

        }
    }

    fun draw() {
        for (row in minRow..maxRow+2) {
            for (col in minCol..maxCol) {
                print(this[Coord(col, row)])
            }
            println()
        }
    }

    fun computeBounds() {
        for (coord in items.keys) {
            maxCol = max(maxCol, coord.col)
            minCol = min(minCol, coord.col)

            maxRow = max(maxRow, coord.row)
            minRow = min(minRow, coord.row)
        }
    }

    fun sandDrop(): Boolean {
        var curr = Coord(500, 0)

        l@ while (this[curr] == Cell.AIR && withinBounds(curr)) {
            for (next in curr.steps()) {
                if (this[next] == Cell.AIR) {
                    curr = next
                    continue@l
                }
            }
            this[curr] = Cell.SAND
            return true
        }


        return false
    }

    private fun withinBounds(curr: Coord): Boolean = curr.row <= maxRow + 2 // + 2 for part 2

}

fun main() {
    setIn("input.txt")

    val grid = Grid()

    var line = readlnOrNull()
    while (line != null) {
        val stone = parseStone(line)
        println(stone)
        grid.addStone(stone)
        line = readlnOrNull()
    }

    grid.computeBounds()
    grid.draw()
    println("===")
    var cnt = 0
    while (true) {
        if (!grid.sandDrop())
            break
        cnt++
//        grid.draw()
//        println("===")
    }

    grid.draw()
    println("===")

    println(cnt)
}
