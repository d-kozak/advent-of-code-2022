package day12

import java.io.FileInputStream

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day12/${fileName}"))

data class Coord(val x: Int, val y: Int) {

    fun nei() = sequence {
        yield(Coord(x + 1, y))
        yield(Coord(x - 1, y))
        yield(Coord(x, y + 1))
        yield(Coord(x, y - 1))
    }
}

fun readMap(): Triple<Array<CharArray>, Coord, Coord> {
    var line = readlnOrNull()
    val res = mutableListOf<CharArray>()
    var i = 0
    var start = null as Coord?
    var end = null as Coord?
    while (line != null) {
        val arr = line.toCharArray()
        res.add(arr)
        for (j in arr.indices) {
            when {
                arr[j] == 'S' -> {
                    start = Coord(i, j)
                    arr[j] = 'a'
                }

                arr[j] == 'E' -> {
                    end = Coord(i, j)
                    arr[j] = 'z'
                }
            }
        }
        i++
        line = readlnOrNull()
    }

    return Triple(res.toTypedArray(), start!!, end!!)
}


operator fun Array<BooleanArray>.get(coord: Coord) = this[coord.x][coord.y]
operator fun Array<BooleanArray>.set(coord: Coord, value: Boolean) {
    this[coord.x][coord.y] = value
}

operator fun Array<CharArray>.get(coord: Coord) = this[coord.x][coord.y]

fun main() {
    setIn("input.txt")

    val (map, start, end) = readMap()
    val n = map.size
    val m = map[0].size

    val seen = Array(n) { BooleanArray(m) }
    val queue = ArrayDeque<Pair<Coord, Int>>()

    queue.add(start to 0)
    seen[start] = true

    // part 2 - multiple starting points
    for (i in 0 until n) {
        for (j in 0 until m) {
            val coord = Coord(i, j)
            if (coord != start && map[coord] == 'a') {
                seen[coord] = true
                queue.add(coord to 0)
            }
        }
    }


    while (queue.isNotEmpty()) {
        val (curr, dist) = queue.removeFirst()
        if (curr == end) {
            println(dist)
            return
        }
        for (next in curr.nei()) {
            if (next.x in 0 until n && next.y in 0 until m && !seen[next]) {
                if (map[next] - map[curr] <= 1) {
                    seen[next] = true
                    queue.add(next to dist + 1)
                }
            }
        }
    }
    error("Could not find a path to E")
}