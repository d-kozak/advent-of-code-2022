package day15

import java.io.FileInputStream
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day15/${fileName}"))

data class Coord(val x: Long, val y: Long)

private fun manhattan(left: Coord, right: Coord): Long {
    return abs(left.x - right.x) + abs(left.y - right.y)
}

data class Sensor(val loc: Coord, val nearestBeacon: Coord) {

    private val radius = manhattan(loc, nearestBeacon)

    fun covers(x: Long, y: Long): Boolean {
        val c = Coord(x, y)
        val dist = manhattan(loc, c)
        return dist <= radius
    }
}

fun parseSensor(line: String): Sensor {
    var cnt = 0
    var fst = 0L

    var loc = null as Coord?

    var i = 0
    val n = line.length
    while (i < n) {
        if (line[i] != '=') {
            i++
            continue
        }
        i++ // skip =
        var end = i
        while (end < n && (line[end].isDigit() || line[end] == '-'))
            end++
        val x = line.substring(i, end).toLong()
        i = end
        when (cnt) {
            0, 2 -> {
                fst = x
                cnt++
            }

            1 -> {
                loc = Coord(fst, x)
                cnt++
            }

            3 -> {
                return Sensor(loc!!, Coord(fst, x))
            }
        }
    }
    error("should never get here")
}

fun main() {
//    setIn("test.txt")
    setIn("input.txt")

    val sensors = mutableListOf<Sensor>()

    var lo = Int.MAX_VALUE.toLong()
    var hi = Int.MIN_VALUE.toLong()

    var line = readlnOrNull()
    while (line != null) {
        val sensor = parseSensor(line)
        println(sensor)
        sensors.add(sensor)
        lo = min(min(lo, sensor.loc.x), sensor.nearestBeacon.x)
        hi = max(max(hi, sensor.loc.x), sensor.nearestBeacon.x)
        line = readlnOrNull()
    }

//    part1(lo, sensors, hi)
    part2(sensors)
}


fun locateSpot(sensors: List<Sensor>): Coord {
    val MAX = 4000000L

    val OPS = MAX * MAX
    var next = 0

    for (y in 0L..MAX) {
        var x = 0L
        while (x < MAX) {
            var hasBeacon = false
            var hasSensor = false
            var isCovered = false
            var coveringSensor = null as Sensor?
            for (sensor in sensors) {
                when {
                    sensor.nearestBeacon.x == x && sensor.nearestBeacon.y == y -> hasBeacon = true
                    sensor.loc.x == x && sensor.loc.y == y -> hasSensor = true
                    sensor.covers(x, y) -> {
                        isCovered = true
                        coveringSensor = sensor
                    }
                }
            }

            if (!hasBeacon and !hasSensor and !isCovered) {
                return Coord(x, y)
            }
            val curr = x * y + x
            val ratio = ((curr.toDouble() / OPS) * 100).toInt()
            if (ratio >= next) {
                println("$x $y: $ratio%")
                next++
            }

            if (coveringSensor != null) {
                var left = x
                var right = MAX

                while (left <= right) {
                    val mid = left + (right - left) / 2
                    if (coveringSensor.covers(mid, y))
                        left = mid + 1
                    else right = mid - 1
                }

                x = left

            } else {
                x++
            }
        }
    }
    error("Spot for beacon not found")
}

fun part2(sensors: List<Sensor>) {
    val pos = locateSpot(sensors)
    println(pos.x * 4000000 + pos.y)
}

private fun part1(lo: Long, sensors: List<Sensor>, hi: Long) {
    val analyzedLine = 2000000L

    var cnt = 0
    var i = lo - 1
    while (sensors.any { it.covers(i, analyzedLine) }) {
        cnt++
        i--
        print('.')
    }
    for (x in lo..hi) {
        var hasBeacon = false
        var hasSensor = false
        var isCovered = false
        for (sensor in sensors) {
            when {
                sensor.nearestBeacon.x == x && sensor.nearestBeacon.y == analyzedLine -> hasBeacon = true
                sensor.loc.x == x && sensor.loc.y == analyzedLine -> hasSensor = true
                sensor.covers(x, analyzedLine) -> isCovered = true
            }
        }
        when {
            hasBeacon -> print('B')
            hasSensor -> print('S')
            isCovered -> {
                print('#')
                cnt++
            }

            else -> print('.')
        }
    }
    i = hi + 1
    while (sensors.any { it.covers(i, analyzedLine) }) {
        cnt++
        i++
        print('.')
    }
    println()
    println(cnt)
}
