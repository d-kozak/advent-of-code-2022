package day23

import java.io.FileInputStream
import kotlin.math.max

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day23/${fileName}"))

enum class Direction(val dRow: Int, val dCol: Int) {
    Right(0, +1),
    Down(+1, 0),
    Left(0, -1),
    Up(-1, 0);

    fun left() = when (this) {
        Right -> Up
        Down -> Right
        Left -> Down
        Up -> Left
    }

    fun right() = when (this) {
        Right -> Down
        Down -> Left
        Left -> Up
        Up -> Right
    }


    override fun toString(): String = when (this) {
        Right -> ">"
        Down -> "v"
        Left -> "<"
        Up -> "^"
    }
}

sealed class Instruction
object Left : Instruction()
object Right : Instruction()
data class Move(val x: Int) : Instruction()

fun parseInstructions(line: String): List<Instruction> {
    var i = 0
    val n = line.length
    val res = mutableListOf<Instruction>()
    while (i < n) {
        val parseNum = res.size % 2 == 0
        if (parseNum) {
            var end = i
            while (end < n && line[end].isDigit())
                end++
            val x = line.substring(i, end).toInt()
            res.add(Move(x))
            i = end
        } else {
            when (val change = line[i]) {
                'L' -> {
                    res.add(Left)
                }

                'R' -> {
                    res.add(Right)
                }

                else -> error("Bad dir change $change")
            }
            i++
        }
    }

    val serialized = res.joinToString("") {
        when (it) {
            Left -> "L"
            is Move -> it.x.toString()
            Right -> "R"
        }
    }

    require(line == serialized)

    return res
}

fun main() {
    setIn("test.txt")
//    setIn("input.txt")

    val grid = mutableListOf<CharArray>()

    var cols = 0
    var rows = 0

    var line = readln()
    while (line.isNotEmpty()) {
        grid.add(line.toCharArray())
        line = readln()
        cols = max(cols, line.length)
        rows++
    }


    val instructions = parseInstructions(readln())

    val firstOnTop = IntArray(cols) { 0 }
    val firstOnBottom = IntArray(cols) { rows - 1 }
    val firstOnLeft = IntArray(rows) { 0 }
    val firstOnRight = IntArray(rows) { grid[it].size - 1 }

    for ((row, data) in grid.withIndex()) {
        var leftSet = false
        var rightSet = false
        for (col in data.indices) {
            val left = col
            val right = data.size - 1 - col
            if (!leftSet && data[left] != ' ') {
                firstOnLeft[row] = left
                leftSet = true
            }
            if (!rightSet && data[right] != ' ') {
                firstOnRight[row] = right
                rightSet = true
            }
            if (leftSet && rightSet)
                break
        }
        require(leftSet && rightSet)
        println(data)
    }

    for (col in 0 until cols) {
        var topSet = false
        var bottomSet = false
        for (row in 0 until rows) {
            val top = row
            val bottom = rows - 1 - row
            if (!topSet && col < grid[top].size && grid[top][col] != ' ') {
                firstOnTop[col] = top
                topSet = true
            }
            if (!bottomSet && col < grid[bottom].size && grid[bottom][col] != ' ') {
                firstOnBottom[col] = bottom
                bottomSet = true
            }
            if (topSet && bottomSet)
                break
        }
        require(topSet && bottomSet)
    }

    // check the edges
    for (row in 0 until rows) {
        val left = firstOnLeft[row]
        check(grid[row][left] != ' ' && (left == 0 || grid[row][left - 1] == ' ')) { "$row left" }
        val right = firstOnRight[row]
        check(grid[row][right] != ' ' && (right == grid[row].size - 1 || grid[row][right + 1] == ' ')) { "$row right" }
    }

    for (col in 0 until cols) {
        val top = firstOnTop[col]
        check(grid[top][col] != ' ' && (top == 0 || col >= grid[top - 1].size || grid[top - 1][col] == ' ')) { "$col top" }
        val bottom = firstOnBottom[col]
        check(grid[bottom][col] != ' ' && (bottom == rows - 1 || col >= grid[bottom + 1].size || grid[bottom + 1][col] == ' ')) { "$col bottom" }
    }


    println("===")
    for (instruction in instructions) {
        println(instruction)
    }

    println("part1")


    fun dumpGrid() {
        for (row in grid) {
            println(row.joinToString(""))
        }
        println("")
    }

    var row = 0
    var col = grid[0].indexOfFirst { it == '.' }
    var direction = Direction.Right

    grid[row][col] = direction.toString()[0]

//    dumpGrid()

    fun next(): Pair<Int, Int> {
        val nRow = row + direction.dRow
        val nCol = col + direction.dCol
        val outside = nRow < 0 || nRow == rows || nCol < 0 || nCol >= grid[nRow].size || grid[nRow][nCol] == ' '
        return if (!outside) nRow to nCol
        else when (direction) {
            Direction.Right -> {
                val left = firstOnLeft[nRow]
                check(nRow < grid.size && left < grid[nRow].size)
                nRow to left
            }

            Direction.Down -> {
                val top = firstOnTop[nCol]
                check(top < grid.size && nCol < grid[top].size)
                top to nCol

            }

            Direction.Left -> {
                val right = firstOnRight[nRow]
                check(nRow < grid.size && right < grid[nRow].size)
                nRow to right
            }

            Direction.Up -> {
                val bottom = firstOnBottom[nCol]
                check(bottom < grid.size && nCol < grid[bottom].size)
                bottom to nCol
            }
        }

    }

    for (instruction in instructions) {
        when (instruction) {
            Left -> direction = direction.left()
            Right -> direction = direction.right()
            is Move -> repeat(instruction.x) {
                grid[row][col] = direction.toString()[0]
                val (nRow, nCol) = next()
                check(grid[nRow][nCol] != ' ') { "$nRow $nCol" }
                if (grid[nRow][nCol] != '#') {
                    row = nRow
                    col = nCol
                    grid[row][col] = '*' // direction.toString()[0]
//                    dumpGrid()
//                    Thread.sleep(200)
                }
            }
        }
    }

    println("$row $col $direction")

    val score = 1000 * (row + 1) + 4 * (col + 1) + direction.ordinal
    println(score)
}

