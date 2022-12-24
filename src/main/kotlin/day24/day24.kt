package day24

import day23.Direction
import java.io.FileInputStream


fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day24/${fileName}"))
enum class Dir(override val dr: Int, override val dc: Int) : Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    override fun toString(): String = when (this) {
        UP -> "^"
        DOWN -> "v"
        LEFT -> "<"
        RIGHT -> ">"
    }
}

data class Blizzard(var r: Int, var c: Int, var d: Dir)

fun main() {
    setIn("input.txt")

    var r = 0
    var R = 0
    var C = 0

    var startR = -1
    var startC = 1

    val startBlizzards = mutableSetOf<Blizzard>()

    var line = readlnOrNull()
    C = line!!.length - 2
    // skip the header line
    line = readlnOrNull()
    while (line != null) {
        if (line[1] == '#') {
            // skip the last line
            break
        }
        for (c in line.indices) {
            when (line[c]) {
                '>' -> startBlizzards.add(Blizzard(r, c - 1, Dir.RIGHT))

                '<' -> startBlizzards.add(Blizzard(r, c - 1, Dir.LEFT))

                '^' -> startBlizzards.add(Blizzard(r, c - 1, Dir.UP))

                'v' -> startBlizzards.add(Blizzard(r, c - 1, Dir.DOWN))
            }
        }
        r++
        R++
        line = readlnOrNull()
    }

    var endR = R
    var endC = C - 1

    fun dumpField(pr: Int, pc: Int, blizzards: Set<Blizzard>) {
        for (c in 0 until C + 2) {
            print(if (c == 1) '.' else '#')
        }
        println()

        for (r in 0 until R) {
            print('#')
            for (c in 0 until C) {
                var cnt = 0
                var blz = null as Blizzard?
                for (blizzard in blizzards) {
                    if (blizzard.r == r && blizzard.c == c) {
                        cnt++
                        blz = blizzard
                    }
                }
                when {
                    cnt > 1 -> print(cnt)
                    blz != null -> print(blz.d)
                    pr == r && pc == c -> print('p')
                    else -> print('.')
                }
            }
            println('#')
        }

        for (c in 0 until C + 2) {
            print(if (c == C) '.' else '#')
        }
        println()
    }

//    dumpField(-1, -1, startBlizzards)


    fun moveBlizzards(blizzards: Set<Blizzard>): Set<Blizzard> {
        val res = mutableSetOf<Blizzard>()

        for (blz in blizzards) {
            val next = blz.copy()
            next.r = (next.r + next.d.dr) % R
            if (next.r < 0)
                next.r += R
            next.c = (next.c + next.d.dc) % C
            if (next.c < 0)
                next.c += C
            res.add(next)
        }

        return res
    }

    data class State(val pr: Int, val pc: Int, var len: Int, val iter: Int) {
        var prev: State? = null
    }


    val mod = R * C
    val blizzards = mutableListOf<Set<Blizzard>>()
    val occupied = mutableListOf<Set<Pair<Int, Int>>>()

    var curr: Set<Blizzard> = startBlizzards
    for (i in 0 until mod) {
        blizzards.add(curr)
        occupied.add(curr.map { it.r to it.c }.toSet())
        curr = moveBlizzards(curr)
    }

    fun dumpState(endState: State) {
        val allStates = mutableListOf<State>()
        var curr = endState as State?
        while (curr != null) {
            allStates.add(curr!!)
            curr = curr!!.prev
        }

        for (state in allStates.asReversed()) {
            dumpField(state.pr, state.pc, blizzards[state.len % mod])
            println()
        }
    }

    val seen = mutableSetOf<State>()

    val queue = ArrayDeque<State>()
    queue.add(State(startR, startC, 0, 0))

    l@ while (queue.isNotEmpty()) {
        val state = queue.removeFirst()
        val (pr, pc, len, iter) = state
        var nextIter = iter
        if (!seen.add(state)) continue
//        println(queue.size)
//        println(state)
//        dumpField(pr,pc,blizzards)

        if (pr to pc in occupied[len % mod])
            continue@l

        if (pr == endR && pc == endC && iter % 2 == 0) {
            val part = if (iter == 0) 1 else 2
            println("part $part")
//            dumpState(state)
            println("shortest len: $len")
            if (part == 1) {
                nextIter = 1
            } else if (part == 2) {
                return
            }
        }

        if (pr == startR && pc == startC && iter == 1) {
            println("==mid===")
            nextIter = 2
        }

        val moves = listOf(pr + 1 to pc, pr - 1 to pc, pr to pc - 1, pr to pc + 1, pr to pc)
        for ((nr, nc) in moves) {
            if ((nr in 0 until R && nc in 0 until C) || (nr == endR && nc == endC) || (nr == startR && nc == startC)) {
                queue.add(State(nr, nc, len + 1, nextIter).also { it.prev = state })
            }
        }
    }

    println("$endR $endC")
    error("No path found")
}

