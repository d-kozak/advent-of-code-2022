package day1

import java.io.FileInputStream

fun main() {
    System.setIn(FileInputStream("src/main/kotlin/day1/input.txt"))
    var fst = 0L
    var snd = 0L
    var thr = 0L
    var curr = 0L
    var line = readlnOrNull()
    while (line != null) {
        if (line.isEmpty()) {
            if (curr >= fst) {
                thr = snd
                snd = fst
                fst = curr
            } else if (curr >= snd) {
                thr = snd
                snd = curr
            } else if (curr >= thr) {
                thr = curr
            }
            curr = 0
        } else {
            curr += line.toInt()
        }
        line = readlnOrNull()
    }
    if (curr >= fst) {
        thr = snd
        snd = fst
        fst = curr
    } else if (curr >= snd) {
        thr = snd
        snd = curr
    } else if (curr >= thr) {
        thr = curr
    }
    println(fst)
    println(fst + snd + thr)
}