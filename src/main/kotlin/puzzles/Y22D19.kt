package puzzles

import Puzzle
import kotlin.math.ceil

class Y22D19(name: String, input: String, testInput: String) : Puzzle(name, input, testInput) {

    override fun part1(): String {
        val re = Regex("^Blueprint (\\d+): " +
                "Each ore robot costs (\\d+) ore. " +
                "Each clay robot costs (\\d+) ore. " +
                "Each obsidian robot costs (\\d+) ore and (\\d+) clay. " +
                "Each geode robot costs (\\d+) ore and (\\d+) obsidian.\$")
        val blueprints = input().map {
            val (
                bpId,
                oreRobot,
                clayRobot,
                obsidianRobotOre,
                obsidianRobotClay,
                geodeRobotOre,
                geodeRobotObsidian,
            ) = re.find(it)!!.destructured
            BluePrint(
                bpId.toInt(),
                oreRobot.toInt(),
                clayRobot.toInt(),
                obsidianRobotOre.toInt() to obsidianRobotClay.toInt(),
                geodeRobotOre.toInt() to geodeRobotObsidian.toInt(),
            )
        }

        return blueprints.sumOf { it.id * findMaxGeodes(it, 24) }.toString()
    }

    override fun part2(): String {
        val re = Regex("^Blueprint (\\d+): " +
                "Each ore robot costs (\\d+) ore. " +
                "Each clay robot costs (\\d+) ore. " +
                "Each obsidian robot costs (\\d+) ore and (\\d+) clay. " +
                "Each geode robot costs (\\d+) ore and (\\d+) obsidian.\$")
        val blueprints = input().map {
            val (
                bpId,
                oreRobot,
                clayRobot,
                obsidianRobotOre,
                obsidianRobotClay,
                geodeRobotOre,
                geodeRobotObsidian,
            ) = re.find(it)!!.destructured
            BluePrint(
                bpId.toInt(),
                oreRobot.toInt(),
                clayRobot.toInt(),
                obsidianRobotOre.toInt() to obsidianRobotClay.toInt(),
                geodeRobotOre.toInt() to geodeRobotObsidian.toInt(),
            )
        }.take(3)

        return blueprints.map { findMaxGeodes(it, 32) }.reduce { acc, i -> acc * i }.toString()
    }

    private fun findMaxGeodes(bp: BluePrint, timeLimit: Int): Int {
        val debug = false
        fun dp(t: Int, store: Store, robots: RobotFactory): Int {
            val indent = "\t".repeat(t-1)
            if (debug) println("$indent> dp: $t, $store, $robots")
            if (t >= timeLimit) {
                if (t > timeLimit) {
                    throw IllegalStateException("Time exceeded maximum $t")
                }
                val updatedStore = robots.mine(store)
                if (debug) println("$indent> RESULT $t, $updatedStore, $robots")
                return updatedStore.geodes
            }

            val makeRobotMaxScore = Robot.values().mapNotNull { robotType ->
                robots.timeToAfford(robotType, store, bp)?.let { robotType to it }
            }
                .sortedBy { (robotType, _) -> robotType.ordinal }
                // Skip any robots that would take us past the time limit
                .filter { (_, timeToAfford) -> (t + timeToAfford) < timeLimit }
                // Skip robots that don't make sense to prioritize
                .filter { (robotType, _) ->
                    when (robotType) {
                        Robot.ORE -> {
                            // Don't make new ore robots if we have enough robots to make either a clay, obsidian, or geode robot
                            val maxOreRobots = maxOf(bp.clayRobot, bp.obsidianRobot.first, bp.geodeRobot.first)
                            if (robots.ore >= maxOreRobots) {
                                if (debug) println("$indent> skip making ore robot")
                                false
                            } else {
                                true
                            }
                        }
                        Robot.CLAY -> {
                            // Don't make new clay robots if we have enough robots to make obsidian
                            if (robots.clay >= bp.obsidianRobot.second) {
                                if (debug) println("$indent> skip making clay robot")
                                false
                            } else {
                                true
                            }
                        }
                        Robot.OBSIDIAN -> {
                            // Don't make new obsidian robots if we have enough robots to make geodes
                            if (robots.obsidian >= bp.geodeRobot.second) {
                                if (debug) println("$indent> skip making obsidian robot")
                                false
                            } else {
                                true
                            }
                        }
                        else -> true
                    }
                }
                .also { if (debug) println("$indent> Make robots $it") }
                .maxOfOrNull { (robotType, timeToAffordRobot) ->
                    if (debug) println("$indent> make new robot $robotType, $timeToAffordRobot")
                    val (updatedRobots, updatedStore) = robots.makeRobot(robots.mine(store, timeToAffordRobot), robotType, bp)
                    if (t + timeToAffordRobot >= timeLimit) {
                        return updatedStore.geodes
                    }
                    dp(t + timeToAffordRobot + 1, updatedStore, updatedRobots)
                }

            return if (makeRobotMaxScore == null) {
                // do nothing score
                if (debug) println("$indent> mine without making robot")
                val result = dp(t + 1, robots.mine(store), robots)
                result
            } else {
                makeRobotMaxScore
            }
        }

        val result = dp(1, Store(), RobotFactory())
        println("Max geodes for blueprint $result $bp")

        return result
    }

    data class BluePrint(
        val id: Int,
        // ore cost
        val oreRobot: Int,
        // ore cost
        val clayRobot: Int,
        // ore cost, clay cost
        val obsidianRobot: Pair<Int, Int>,
        // ore cost, obsidian cost
        val geodeRobot: Pair<Int, Int>,
    ) {
        fun costOf(robot: Robot) = when (robot) {
            Robot.ORE -> Store(ore = oreRobot)
            Robot.CLAY -> Store(ore = clayRobot)
            Robot.OBSIDIAN -> Store(ore = obsidianRobot.first, clay = obsidianRobot.second)
            Robot.GEODE -> Store(ore = geodeRobot.first, obsidian = geodeRobot.second)
        }
    }

    data class Store(
        var ore: Int = 0,
        var clay: Int = 0,
        var obsidian: Int = 0,
        var geodes: Int = 0,
    ) {
        operator fun plus(other: Store) = Store(
            ore + other.ore,
            clay + other.clay,
            obsidian + other.obsidian,
            geodes + other.geodes,
        )

        operator fun minus(other: Store) = Store(
            ore - other.ore,
            clay - other.clay,
            obsidian - other.obsidian,
            geodes - other.geodes,
        )

        fun canAfford(cost: Store) =
            ore >= cost.ore && clay >= cost.clay && obsidian >= cost.obsidian && geodes >= cost.geodes
    }

    data class RobotFactory(
        var ore: Int = 1,
        var clay: Int = 0,
        var obsidian: Int = 0,
        var geode: Int = 0,
    ) {
        fun mine(s: Store, t: Int = 1) = Store(ore * t, clay * t, obsidian * t, geode * t) + s

        private fun addRobot(robot: Robot) = when (robot) {
            Robot.ORE -> this.copy(ore = this.ore + 1)
            Robot.CLAY -> this.copy(clay = this.clay + 1)
            Robot.OBSIDIAN -> this.copy(obsidian = this.obsidian + 1)
            Robot.GEODE -> this.copy(geode = this.geode + 1)
        }

        fun makeRobot(s: Store, robot: Robot, bp: BluePrint): Pair<RobotFactory, Store> {
            val robotCost = bp.costOf(robot)
            if (!s.canAfford(robotCost)) {
                throw IllegalArgumentException("Cannot afford robot $robot, cost:$robotCost, store:$s, $bp")
            }

            return this.addRobot(robot) to mine(s - robotCost)
        }

        fun timeToAfford(robot: Robot, store: Store, bp: BluePrint): Int? {
            val cost = bp.costOf(robot)
            if (store.canAfford(cost)) {
                return 0
            }
            return when (robot) {
                Robot.ORE -> {
                    if (ore == 0) null
                    else ceil((cost.ore - store.ore) / ore.toDouble())
                }
                Robot.CLAY -> {
                    if (ore == 0) null
                    else ceil((cost.ore - store.ore) / ore.toDouble())
                }
                Robot.OBSIDIAN -> {
                    if (ore == 0 || clay == 0) null
                    else {
                        maxOf(
                            ceil((cost.ore - store.ore) / ore.toDouble()),
                            ceil((cost.clay - store.clay) / clay.toDouble())
                        )
                    }
                }
                Robot.GEODE -> {
                    if (ore == 0 || obsidian == 0) null
                    else {
                        maxOf(
                            ceil((cost.ore - store.ore) / ore.toDouble()),
                            ceil((cost.obsidian - store.obsidian) / obsidian.toDouble())
                        )
                    }
                }
            }?.toInt()
        }
    }

    enum class Robot {
        ORE,
        CLAY,
        OBSIDIAN,
        GEODE,
    }
}