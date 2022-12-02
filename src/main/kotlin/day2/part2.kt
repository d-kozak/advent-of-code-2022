package day2

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

fun main() {
    setIn("input.txt")
    var score = 0
    var game = readlnOrNull()
    while (game != null) {
        val (oponent, result) = processLine2(game)
        score += calculateScore2(oponent, result)
        game = readlnOrNull()
    }
    println(score)
}

fun calculateScore2(oponent: Choice, result: Result): Int {
    val me = when (result) {
        Result.Win -> when (oponent) {
            Choice.Rock -> Choice.Paper
            Choice.Paper -> Choice.Scissors
            Choice.Scissors -> Choice.Rock
        }

        Result.Draw -> when (oponent) {
            Choice.Rock -> Choice.Rock
            Choice.Paper -> Choice.Paper
            Choice.Scissors -> Choice.Scissors
        }

        Result.Lost -> when (oponent) {
            Choice.Rock -> Choice.Scissors
            Choice.Paper -> Choice.Rock
            Choice.Scissors -> Choice.Paper
        }
    }

    return me.points + result.points
}

fun processLine2(game: String): Pair<Choice, Result> {
    val oppponent = when (game[0]) {
        'A' -> Choice.Rock
        'B' -> Choice.Paper
        'C' -> Choice.Scissors
        else -> error("Bad first input ${game[0]}")
    }
    val result = when (game[2]) {
        'X' -> Result.Lost
        'Y' -> Result.Draw
        'Z' -> Result.Win
        else -> error("Bad first input ${game[2]}")
    }
    return oppponent to result
}


