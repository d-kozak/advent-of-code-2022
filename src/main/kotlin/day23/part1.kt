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

data class Info(val grid: MutableList<MutableList<Char>>, val C: Int, val instructions: List<Instruction>)

fun parseGrid(): Info {
    val grid = mutableListOf<MutableList<Char>>()

    var C = 0

    var line = readln()
    while (line.isNotEmpty()) {
        C = max(C, line.length)
        grid.add(line.toMutableList())
        line = readln()
    }

    for (row in grid) {
        while (row.size < C)
            row.add(' ')
    }

    return Info(grid, C, parseInstructions(readln()))
}

data class GameState(
    var r: Int = 0,
    var c: Int = 0,
    var d: Direction,
    val ROWS: Int,
    val COLS: Int,
    val grid: MutableList<MutableList<Char>>,
    val instructions: List<Instruction>,
    val next: GameState.() -> Pair<Int, Int>
) {
    fun doSolve(): Int {
        while (grid[r][c] != '.')
            c++

        for (inst in instructions) {
            when (inst) {
                Left -> d = d.left()
                Right -> d = d.right()
                is Move -> {
                    for (iter in 0 until inst.x) {
                        val (nr, nc) = next()
                        if (grid[nr][nc] == '#') break
                        r = nr
                        c = nc
                    }
                }
            }
        }

        return 1000 * (r + 1) + 4 * (c + 1) + d.ordinal
    }
}

fun solve(next: GameState.() -> Pair<Int, Int>): Int {
    val (grid, C, instructions) = parseGrid()
    val R = grid.size
    val state = GameState(0, 0, Direction.Right, R, C, grid, instructions, next)

    return state.doSolve()
}


fun main() {
    setIn("input.txt")

    val score = solve {
        var nr = r
        var nc = c
        do {
            nr = (nr + d.dRow) % ROWS
            if (nr < 0) nr += ROWS
            nc = (nc + d.dCol) % COLS
            if (nc < 0) nc += COLS
        } while (grid[nr][nc] == ' ')
        nr to nc
    }

    println("part 1")
    println(score)
}
