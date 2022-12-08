package day8

import java.io.FileInputStream
import kotlin.math.max

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day8/${fileName}"))

private fun parseGrid(): MutableList<List<Int>> {
    val grid = mutableListOf<List<Int>>()
    var line = readlnOrNull()
    while (line != null) {
        grid.add(line.map { it - '0' })
        line = readlnOrNull()
    }
    return grid
}

fun main() {
    setIn("input.txt")
    val grid = parseGrid()
    val n = grid.size

    part1(n, grid)
    println("====")
    part2(n, grid)
}

fun part2(n: Int, grid: MutableList<List<Int>>) {
    var best = 0

    val scores = Array(n) { IntArray(n) }

    for (row in 1..n - 2) {
        for (col in 1..n - 2) {
            val x = grid[row][col]
            var leftCnt = 1
            while (col - leftCnt > 0 && grid[row][col - leftCnt] < x)
                leftCnt++
            var rightCnt = 1
            while (col + rightCnt < n - 1 && grid[row][col + rightCnt] < x)
                rightCnt++
            var topCnt = 1
            while (row - topCnt > 0 && grid[row - topCnt][col] < x)
                topCnt++
            var bottomCnt = 1
            while (row + bottomCnt < n - 1 && grid[row + bottomCnt][col] < x)
                bottomCnt++
            scores[row][col] = leftCnt * rightCnt * topCnt * bottomCnt
            best = max(best, scores[row][col])
        }
    }

    scores.forEach { println(it.contentToString()) }
    println(best)
}

private fun part1(n: Int, grid: MutableList<List<Int>>) {
    val isVisible = Array(n) { row -> BooleanArray(n) { col -> row == 0 || row == n - 1 || col == 0 || col == n - 1 } }

    for (row in 1..n - 2) {
        var highestFromLeft = grid[row][0]
        for (col in 1..n - 2) {
            if (!isVisible[row][col]) {
                isVisible[row][col] = grid[row][col] > highestFromLeft
            }
            highestFromLeft = max(highestFromLeft, grid[row][col])
        }
    }

    for (row in 1..n - 2) {
        var highestFromRight = grid[row][n - 1]
        for (col in n - 2 downTo 1) {
            if (!isVisible[row][col]) {
                isVisible[row][col] = grid[row][col] > highestFromRight
            }
            highestFromRight = max(highestFromRight, grid[row][col])
        }
    }

    for (col in 1..n - 2) {
        var highestFromTop = grid[0][col]
        for (row in 1..n - 2) {
            if (!isVisible[row][col]) {
                isVisible[row][col] = grid[row][col] > highestFromTop
            }
            highestFromTop = max(highestFromTop, grid[row][col])
        }
    }

    for (col in 1..n - 2) {
        var highestFromBottom = grid[n - 1][col]
        for (row in n - 2 downTo 1) {
            if (!isVisible[row][col]) {
                isVisible[row][col] = grid[row][col] > highestFromBottom
            }
            highestFromBottom = max(highestFromBottom, grid[row][col])
        }
    }

    grid.forEach { println(it) }

    isVisible.forEach { println(it.contentToString()) }

    println(isVisible.sumOf { it.count { it } })
}
