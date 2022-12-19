package day19.v2

import java.io.FileInputStream
import kotlin.math.max
import kotlin.system.measureTimeMillis

fun setIn(fileName: String) = System.setIn(FileInputStream("src/main/kotlin/day19/${fileName}"))

data class OreRobotPrice(val ore: Int)
data class ClayRobotPrice(val ore: Int)
data class ObsidianRobotPrice(val ore: Int, val clay: Int)
data class GeodeRobotPrice(val ore: Int, val obsidian: Int)
data class Config(
    val oreRobot: OreRobotPrice,
    val clayRobot: ClayRobotPrice,
    val obsidianRobot: ObsidianRobotPrice,
    val geodeRobot: GeodeRobotPrice
)


fun parseConfig(line: String): Config {
    val split = line.split(' ')
    val ore = OreRobotPrice(split[6].toInt())
    val clay = ClayRobotPrice(split[12].toInt())
    val obsidian = ObsidianRobotPrice(split[18].toInt(), split[21].toInt())
    val geode = GeodeRobotPrice(split[27].toInt(), split[30].toInt())
    return Config(ore, clay, obsidian, geode)
}


data class StoneState(
    val ore: Int,
    val clay: Int,
    val obsidian: Int,
    val geode: Int
)

data class RobotState(
    val ore: Int,
    val clay: Int,
    val obsidian: Int,
    val geode: Int
)

var TIME_LIMIT = 24

data class State(val stones: StoneState, val robots: RobotState, val time: Int)

fun solve(config: Config): Int {
    val stack = ArrayDeque<State>()
    stack.add(State(StoneState(0, 0, 0, 0), RobotState(1, 0, 0, 0), 1))

    val maxOreConsumption =
        listOf(config.oreRobot.ore, config.clayRobot.ore, config.obsidianRobot.ore, config.geodeRobot.ore).max()
    val maxClayConsumption = config.obsidianRobot.clay

    var best = 0

    var i = 0
    var skip = 0
    val seen = mutableSetOf<State>()

    while (stack.isNotEmpty()) {
        val state = stack.removeLast()
        if (!seen.add(state)) {
            skip++
            continue
        }
//        println("$state,${seen.size}")
        if (i++ % 1_000_000 == 0) {
//            println(state)
            println("$state,${seen.size},$skip")
        }

        val (stones, robots, time) = state
        val (ore, clay, obsidian, geode) = stones
        val (oreR, clayR, obsidianR, geodeR) = robots
        if (time > TIME_LIMIT) {
            best = max(best, geode)
            continue
        }

        // do nothing
        stack.add(State(StoneState(ore + oreR, clay + clayR, obsidian + obsidianR, geode + geodeR), robots, time + 1))

        if (ore >= config.oreRobot.ore && oreR < maxOreConsumption) {
            stack.add(
                State(
                    StoneState(ore + oreR - config.oreRobot.ore, clay + clayR, obsidian + obsidianR, geode + geodeR),
                    RobotState(oreR + 1, clayR, obsidianR, geodeR),
                    time + 1
                )
            )
        }

        if (ore >= config.clayRobot.ore && clayR < maxClayConsumption) {
            stack.add(
                State(
                    StoneState(ore + oreR - config.clayRobot.ore, clay + clayR, obsidian + obsidianR, geode + geodeR),
                    RobotState(oreR, clayR + 1, obsidianR, geodeR),
                    time + 1
                )
            )
        }

        if (ore >= config.obsidianRobot.ore && clay >= config.obsidianRobot.clay) {
            stack.add(
                State(
                    StoneState(
                        ore + oreR - config.obsidianRobot.ore,
                        clay + clayR - config.obsidianRobot.clay,
                        obsidian + obsidianR,
                        geode + geodeR
                    ),
                    RobotState(oreR, clayR, obsidianR + 1, geodeR),
                    time + 1
                )
            )
        }

        if (ore >= config.geodeRobot.ore && obsidian >= config.geodeRobot.obsidian) {
            stack.add(
                State(
                    StoneState(
                        ore + oreR - config.geodeRobot.ore,
                        clay + clayR,
                        obsidian + obsidianR - config.geodeRobot.obsidian,
                        geode + geodeR
                    ),
                    RobotState(oreR, clayR, obsidianR, geodeR + 1),
                    time + 1
                )
            )
        }
    }

    return best
}

fun main() {
    setIn("input.txt")
    var line = readlnOrNull()
    var id = 1
    var res = 0
    while (line != null) {
        val config = parseConfig(line)
        println(config)
        val t = measureTimeMillis {
            val x = solve(config)
            res += id++ * x
            println("$config -> $x")
        }
        println("Took ${t / 1000} seconds.")
        line = readlnOrNull()
    }

    println(res)
}
