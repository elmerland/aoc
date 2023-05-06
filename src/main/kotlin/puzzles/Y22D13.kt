package puzzles

import Puzzle
import puzzles.Y22D13.PacketItem.Companion.areInOrder
import puzzles.Y22D13.PacketItem.PacketList
import puzzles.Y22D13.PacketItem.PacketNum

class Y22D13(name: String, input: String, testInput: String) : Puzzle(name, input, testInput) {
    override fun part1(): String {
        val packets = input().chunked(3).map { it[0].toPacket() to it[1].toPacket() }

        val result = packets
            .map { (left, right) -> areInOrder(left, right) }
            .mapIndexed { index, b -> if (b == true) index + 1 else 0 }
            .sum()

        return result.toString()
    }

    override fun part2(): String {
        val dividerPackets = listOf("[[2]]".toPacket(), "[[6]]".toPacket())
        val packets =
            input().filter { it.isNotBlank() }.map { it.toPacket() } +
                    dividerPackets

        val sortedPackets = packets.sortedWith { left, right ->
            when (areInOrder(left, right)) {
                true -> -1
                false -> 1
                else -> 0
            }
        }

        val d1 = sortedPackets.indexOf(dividerPackets[0]) + 1
        val d2 = sortedPackets.indexOf(dividerPackets[1]) + 1

        return (d1 * d2).toString()
    }

    private fun String.toPacket() = this.toPacket(0, this.length)

    private fun String.toPacket(startIndex: Int, endIndex: Int): PacketItem {
        val result = mutableListOf<PacketItem>()

        var currIdx = startIndex + 1
        while (currIdx < endIndex - 1) {
            val c = this[currIdx]
            when (c) {
                '[' -> {
                    val end = this.findListEnd(currIdx)
                    result.add(this.toPacket(currIdx, end + 1))
                    currIdx = end
                }

                ']' -> throw IllegalStateException("Should not happen")
                ',' -> Unit
                else -> {
                    val next = this[currIdx + 1]
                    if (next.isDigit()) {
                        currIdx++
                        result.add(PacketNum(("" + c + next).toInt()))
                    } else {
                        result.add(PacketNum(c.digitToInt()))
                    }
                }
            }
            currIdx++
        }
        return PacketList(result)
    }

    private fun String.findListEnd(startIndex: Int): Int {
        var depth = 0
        (startIndex until length)
            .forEach { index ->
                when (this[index]) {
                    '[' -> depth++
                    ']' -> depth--
                }
                if (depth == 0) {
                    return index
                }
            }
        throw IllegalArgumentException("Unbalanced parens")
    }

    sealed class PacketItem {
        data class PacketNum(val num: Int) : PacketItem() {
            override fun toString(): String = num.toString()
        }

        data class PacketList(val list: List<PacketItem>) : PacketItem() {
            override fun toString(): String = list.toString()
        }

        companion object {
            fun areInOrder(left: PacketItem, right: PacketItem): Boolean? {
                return if (left is PacketNum && right is PacketNum) {
                    if (left.num < right.num) {
                        true
                    } else if (left.num > right.num) {
                        false
                    } else {
                        null
                    }
                } else if (left is PacketList && right is PacketList) {
                    val size = minOf(left.list.size, right.list.size)
                    (0 until size)
                        .map { areInOrder(left.list[it], right.list[it]) }.firstOrNull { it != null }
                        ?: if (left.list.size < right.list.size) {
                            true
                        } else if (left.list.size > right.list.size) {
                            false
                        } else {
                            null
                        }
                } else {
                    if (left is PacketNum) {
                        areInOrder(PacketList(listOf(left)), right)
                    } else {
                        areInOrder(left, PacketList(listOf(right)))
                    }
                }
            }
        }
    }
}