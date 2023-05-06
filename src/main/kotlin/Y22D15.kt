import Point.Companion.maxX
import Point.Companion.minX

class Y22D15(name: String, input: String, testInput: String) : Puzzle(name, input, testInput) {
    override fun part1(): String {
        val points = input().map { line ->
            val (sensorRaw, beaconRaw) = line.split(": ")
            val sensorPoint = sensorRaw
                .removePrefix("Sensor at x=")
                .split(", y=")
                .let {
                    Point(it[0].toInt(), it[1].toInt())
                }

            val beaconPoint = beaconRaw
                .removePrefix("closest beacon is at x=")
                .split(", y=")
                .let {
                    Point(it[0].toInt(), it[1].toInt())
                }
            sensorPoint to beaconPoint
        }

        val sensors = points.map { it.first }.toSet()
        val beacons = points.map { it.second }.toSet()
        val sensorDistances = points.map { (sensor, beacon) ->
            sensor to sensor.absDistanceFrom(beacon)
        }.associate { it }

        val maxDistance = sensorDistances.values.max()

        val minX = sensors.minX().x - maxDistance
        val maxX = sensors.maxX().x + maxDistance


        fun isEmptySpot(point: Point): Boolean {
            return sensors.any { it.absDistanceFrom(point) <= sensorDistances[it]!! }
                    && !sensors.contains(point) && !beacons.contains(point)
        }

        val result = (minX..maxX).mapNotNull { x ->
            val c = Point(x, 2000000)
            if (isEmptySpot(c)) {
                c
            } else {
                null
            }
        }.count()

        return result.toString()
    }

    override fun part2(): String {
        val points = input().map { line ->
            val (sensorRaw, beaconRaw) = line.split(": ")
            val sensorPoint = sensorRaw
                .removePrefix("Sensor at x=")
                .split(", y=")
                .let {
                    Point(it[0].toInt(), it[1].toInt())
                }

            val beaconPoint = beaconRaw
                .removePrefix("closest beacon is at x=")
                .split(", y=")
                .let {
                    Point(it[0].toInt(), it[1].toInt())
                }
            sensorPoint to beaconPoint
        }

        val sensors = points.map { it.first }.toSet()
        val beacons = points.map { it.second }.toSet()
        val sensorDistances = points.map { (sensor, beacon) ->
            sensor to sensor.absDistanceFrom(beacon)
        }.associate { it }

        fun isHiddenBeacon(c: Point): Boolean {
            return sensors.all { it.absDistanceFrom(c) > sensorDistances[it]!! }
                    && !sensors.contains(c) && !beacons.contains(c)
        }

        val (aCoeff, bCoeff) = sensors.map {
            val r = sensorDistances[it]!!
            listOf(
                it.y - it.x + r + 1,
                it.y - it.x - r - 1,
            ) to listOf(
                it.y + it.x + r + 1,
                it.y + it.x - r - 1,
            )
        }.let { coeffs ->
            coeffs.map { it.first }.flatten() to coeffs.map { it.second }.flatten()
        }

        val candidates = aCoeff.map { a ->
            bCoeff.map { b ->
                Point((b-a)/2, (b+a)/2)
            }
        }.flatten().toSet()

        val bounds = (0..4_000_000)

        val result = candidates
            .filter { bounds.contains(it.x) && bounds.contains(it.y) }
            .filter {
                isHiddenBeacon(it)
            }
            .also { println(it) }
            .map {
                it.x.toBigInteger() * 4_000_000.toBigInteger() + it.y.toBigInteger()
            }

        return result.first().toString()
    }
}


/*
From reddit: https://www.reddit.com/r/adventofcode/comments/zmcn64/comment/j0b90nr/?utm_source=share&utm_medium=web2x&context=3

Python

Part 2 in python in 0.01 seconds. Unprocessed input data in input_data.

import re
def all_numbers(s): return [int(d) for d in re.findall("(-?\d+)", s)]
def md(p, q): return abs(p[0]-q[0])+abs(p[1]-q[1])

data = [all_numbers(line) for line in input_data.split("\n")]
radius = {(a,b):md((a,b),(c,d)) for (a,b,c,d) in data}
scanners = radius.keys()

acoeffs, bcoeffs = set(), set()
for ((x,y), r) in radius.items():
    acoeffs.add(y-x+r+1)
    acoeffs.add(y-x-r-1)
    bcoeffs.add(x+y+r+1)
    bcoeffs.add(x+y-r-1)

bound = 4_000_000
for a in acoeffs:
    for b in bcoeffs:
        p = ((b-a)//2, (a+b)//2)
        if all(0<c<bound for c in p):
            if all(md(p,t)>radius[t] for t in scanners):
                print(4_000_000*p[0]+p[1])
Here's the idea:

As there is only one missing value, it's going to be just outside the boundaries of at least two scanners (unless we're incredibly unlucky and it's right on the bounds of the 0-4_000_000 square, but it isn't!).

The boundary of a scanner is four line segments. If a scanner is in position (sx,sy) and has 'radius' r, then we want the line segments just outside, i.e. of radius r+1. There will be two line segments of gradient 1:

y = x + sy-sx+r+1
y = x + sy-sx-r-1
and two line segments of gradient -1:

y = -x + sx+sy+r+1
y = -x + sx+sy-r-1
Determining where a line y=x+a and a line y=-x+b intersect is very easy - they intersect at the point ( (b-a)/2 , (a+b)/2 ).

One of these intersection points will be the missing scanner location. So, we assemble a set of all the 'a' coefficients (lines of gradient 1) and all the 'b' coefficients (lines of gradient -1), then look at their intersections to see if they are the point we need. Given the number of scanners we only need to check a couple of thousand points at most.
 */