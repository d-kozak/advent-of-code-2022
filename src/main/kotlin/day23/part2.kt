package day23

import day23.Direction.*
import day23.Direction.Left
import day23.Direction.Right

fun verifySquareMoves(squareMoves: Map<Pair<Int, Direction>, Pair<Int, Direction>>) {
    for ((from, to) in squareMoves) {
        // check reverse edges
        check(squareMoves[to] == from) { "$from $to" }
    }
}

fun main() {
    setIn("input.txt")

    val SQUARE_SIZE = 50

    fun squareCoord(nr: Int, nc: Int): Pair<Int, Int> {
        return nr / SQUARE_SIZE to nc / SQUARE_SIZE
    }


    val squares = mapOf(
        1 to (0 to 1),
        2 to (0 to 2),
        3 to (1 to 1),
        4 to (2 to 0),
        5 to (2 to 1),
        6 to (3 to 0)
    )

    // +1 to automatically handle overflow
    val squareAt = Array(4 + 1) { IntArray(3 + 1) { -1 } }
    squareAt[0][1] = 1
    squareAt[0][2] = 2
    squareAt[1][1] = 3
    squareAt[2][0] = 4
    squareAt[2][1] = 5
    squareAt[3][0] = 6

    fun move(from: Int, fromDir: Direction, to: Int, toDir: Direction) = (from to fromDir) to (to to toDir)

    // if we overflow from square X with direction d1
    // then we will end up in square Y on its edge d2
    // note - the actual direction afterwards will be the opposite of d2 (eg from the right edge you start going left)
    val squareMoves = mapOf(
        move(1, Right, 2, Left),
        move(1, Down, 3, Up),
        move(1, Left, 4, Left),
        move(1, Up, 6, Left),

        move(2, Right, 5, Right),
        move(2, Down, 3, Right),
        move(2, Left, 1, Right),
        move(2, Up, 6, Down),

        move(3, Right, 2, Down),
        move(3, Down, 5, Up),
        move(3, Left, 4, Up),
        move(3, Up, 1, Down),

        move(4, Right, 5, Left),
        move(4, Down, 6, Up),
        move(4, Left, 1, Left),
        move(4, Up, 3, Left),

        move(5, Right, 2, Right),
        move(5, Down, 6, Right),
        move(5, Left, 4, Right),
        move(5, Up, 3, Down),

        move(6, Right, 5, Down),
        move(6, Down, 2, Up),
        move(6, Left, 1, Up),
        move(6, Up, 4, Down)
    )

    verifySquareMoves(squareMoves)

    fun transformCoords(r: Int, c: Int, currentSquare: Int, d: Direction): Triple<Int, Int, Direction> {
        val (nextSquare, side) = squareMoves[currentSquare to d]!!
        val newDir = side.flip()

        // local coordinates in the current square
        var lr = r % SQUARE_SIZE
        var lc = c % SQUARE_SIZE

        when {
            d == Right && side == Left -> {
                lr = lr
                lc = 0
            }

            d == Down && side == Up -> {
                lr = 0
                lc = lc
            }

            d == Left && side == Right -> {
                lr = lr
                lc = SQUARE_SIZE - 1
            }

            d == Up && side == Down -> {
                lr = SQUARE_SIZE - 1
                lc = lc
            }

            d == Left && side == Left -> {
                lr = SQUARE_SIZE - lr - 1
                lc = 0
            }

            d == Up && side == Left -> {
                lr = lc
                lc = 0
            }

            d == Left && side == Up -> {
                lc = lr
                lr = 0
            }

            d == Right && side == Right -> {
                lr = SQUARE_SIZE - lr - 1
                lc = SQUARE_SIZE - 1
            }

            d == Down && side == Right -> {
                lr = lc
                lc = SQUARE_SIZE - 1
            }

            d == Right && side == Down -> {
                lc = lr
                lr = SQUARE_SIZE - 1
            }
        }

        val (dr, dc) = squares[nextSquare]!!

        // go back the the global coordinates
        val nr = dr * SQUARE_SIZE + lr
        val nc = dc * SQUARE_SIZE + lc

        return Triple(nr, nc, newDir)
    }

    val computeNextStep: GameState.() -> Triple<Int, Int, Direction> = {
        val coords = squareCoord(r, c)
        val currentSquare = squareAt[coords.first][coords.second]

        var nr = r + d.dRow
        var nc = c + d.dCol

        val nextSquare = if (nr < 0 || nc < 0) -1 else {
            val (sr, sc) = squareCoord(nr, nc)
            squareAt[sr][sc]
        }
        if (currentSquare == nextSquare) {
            check(nr >= 0)
            check(nc >= 0)
            Triple(nr, nc, d)
        } else {
            transformCoords(r, c, currentSquare, d)
        }
    }


    val res = solve(computeNextStep)
    println("Part 2")
    println(res)
}



