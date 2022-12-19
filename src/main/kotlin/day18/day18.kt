package day18

import java.io.FileInputStream
import kotlin.math.max
import kotlin.math.min

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day18/${fileName}"))

data class Grid(val cubes: MutableSet<GridPos>) {
    var xMin = Int.MAX_VALUE
    var yMin = Int.MAX_VALUE
    var zMin = Int.MAX_VALUE

    var xMax = Int.MIN_VALUE
    var yMax = Int.MIN_VALUE
    var zMax = Int.MIN_VALUE

    init {
        for (cube in cubes) {
            xMin = min(xMin, cube.x)
            yMin = min(yMin, cube.y)
            zMin = min(zMin, cube.z)

            xMax = max(xMax, cube.x)
            yMax = max(yMax, cube.y)
            zMax = max(zMax, cube.z)
        }
    }

    fun isInside(cube: GridPos) = cube.x in xMin..xMax && cube.y in yMin..yMax && cube.z in zMin..zMax


}

data class GridPos(val x: Int, val y: Int, val z: Int) {
    fun nei() = sequence {
        yield(GridPos(x + 1, y, z))
        yield(GridPos(x - 1, y, z))
        yield(GridPos(x, y + 1, z))
        yield(GridPos(x, y - 1, z))
        yield(GridPos(x, y, z + 1))
        yield(GridPos(x, y, z - 1))
    }
}

fun parseGrid(line: String): GridPos {
    val (x, y, z) = line.split(",").map { it.toInt() }
    return GridPos(x, y, z)
}

private fun countSurfaceArea(cubes: MutableSet<GridPos>): Int {
    var cnt = 0
    for (cube in cubes) {
        for (nei in cube.nei()) {
            if (nei !in cubes) {
                cnt++
            }
        }
    }
    return cnt
}

fun fillAirPockets(grid: Grid) {
    val processed = mutableSetOf<GridPos>()

    val filledArea = mutableSetOf<GridPos>()

    for (cube in grid.cubes) {
        for (nei in cube.nei()) {
            if (nei !in grid.cubes && grid.isInside(nei) && processed.add(nei)) {
                val area = expandAirPocket(grid, nei)
                filledArea.addAll(area)
            }
        }
    }

    grid.cubes.addAll(filledArea)
}

fun expandAirPocket(grid: Grid, pos: GridPos): Set<GridPos> {
    val seen = mutableSetOf<GridPos>()

    val queue = ArrayDeque<GridPos>()
    queue.add(pos)

    while (queue.isNotEmpty()) {
        val curr = queue.removeFirst()
        if (!seen.add(curr)) continue

        for (nei in curr.nei()) {
            if (nei in grid.cubes) continue
            if (!grid.isInside(nei)) {
                // escaped the grid
                return emptySet()
            }
            queue.add(nei)
        }

    }

    return seen
}

fun main() {
    setIn("input.txt")

    val cubes = mutableSetOf<GridPos>()

    var line = readlnOrNull()
    while (line != null) {
        val grid = parseGrid(line)
        cubes.add(grid)
        line = readlnOrNull()
    }


    var cnt = countSurfaceArea(cubes)
    println("part1:")
    println(cnt)

    fillAirPockets(Grid(cubes))
    cnt = countSurfaceArea(cubes)
    println("part2:")
    println(cnt)
}
