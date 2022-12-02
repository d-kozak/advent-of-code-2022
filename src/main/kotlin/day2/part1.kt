package day2

import java.io.FileInputStream

/*
A rock
B paper
C scissors

X rock
Y paper
Z scissors

rock 1
paper 2
scissors 3

win 6
draw 3
lost 0
 */

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day2/${fileName}"))

enum class Choice(val points: Int) {
    Rock(1), Paper(2), Scissors(3)
}

enum class Result(val points: Int) {
    Win(6), Draw(3), Lost(0)
}

fun main() {
    setIn("test.txt")
    var score = 0
    var game = readlnOrNull()
    while (game != null) {
        val (oponent, me) = processLine(game)
        score += calculateScore(me, oponent)
        game = readlnOrNull()
    }
    println(score)
}

private fun calculateScore(me: Choice, oponent: Choice): Int {
    val result = when (me) {
        Choice.Rock -> when (oponent) {
            Choice.Rock -> Result.Draw
            Choice.Paper -> Result.Lost
            Choice.Scissors -> Result.Win
        }

        Choice.Paper -> when (oponent) {
            Choice.Rock -> Result.Win
            Choice.Paper -> Result.Draw
            Choice.Scissors -> Result.Lost
        }

        Choice.Scissors -> when (oponent) {
            Choice.Rock -> Result.Lost
            Choice.Paper -> Result.Win
            Choice.Scissors -> Result.Draw
        }
    }

    return me.points + result.points
}

private fun processLine(game: String): Pair<Choice, Choice> {
    val x = when (game[0]) {
        'A' -> Choice.Rock
        'B' -> Choice.Paper
        'C' -> Choice.Scissors
        else -> error("Bad first input ${game[0]}")
    }
    val y = when (game[2]) {
        'X' -> Choice.Rock
        'Y' -> Choice.Paper
        'Z' -> Choice.Scissors
        else -> error("Bad first input ${game[2]}")
    }
    return x to y
}


