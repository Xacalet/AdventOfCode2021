/**
 * ADVENT OF CODE 2021 (https://adventofcode.com/2021/)
 * 
 * Solution to day 3 (https://adventofcode.com/2021/day/3)
 * 
 * --- Day 3: Binary Diagnostic ---
 * 
 * The submarine has been making some odd creaking noises, so you ask it to produce a diagnostic report just in case.
 * 
 * The diagnostic report (your puzzle input) consists of a list of binary numbers which, when decoded properly, can tell you many useful things about the conditions of the submarine. The first parameter to check is the power consumption.
 * 
 * You need to use the binary numbers in the diagnostic report to generate two new binary numbers (called the gamma rate and the epsilon rate). The power consumption can then be found by multiplying the gamma rate by the epsilon rate.
 * 
 * Each bit in the gamma rate can be determined by finding the most common bit in the corresponding position of all numbers in the diagnostic report. For example, given the following diagnostic report:
 * 
 * 00100
 * 11110
 * 10110
 * 10111
 * 10101
 * 01111
 * 00111
 * 11100
 * 10000
 * 11001
 * 00010
 * 01010
 * 
 * Considering only the first bit of each number, there are five 0 bits and seven 1 bits. Since the most common bit is 1, the first bit of the gamma rate is 1.
 * 
 * The most common second bit of the numbers in the diagnostic report is 0, so the second bit of the gamma rate is 0.
 * 
 * The most common value of the third, fourth, and fifth bits are 1, 1, and 0, respectively, and so the final three bits of the gamma rate are 110.
 * 
 * So, the gamma rate is the binary number 10110, or 22 in decimal.
 * 
 * The epsilon rate is calculated in a similar way; rather than use the most common bit, the least common bit from each position is used. So, the epsilon rate is 01001, or 9 in decimal. Multiplying the gamma rate (22) by the epsilon rate (9) produces the power consumption, 198.
 * 
 * Use the binary numbers in your diagnostic report to calculate the gamma rate and epsilon rate, then multiply them together. What is the power consumption of the submarine? (Be sure to represent your answer in decimal, not binary.)
 * 
 * --- Part Two ---
 * 
 * Next, you should verify the life support rating, which can be determined by multiplying the oxygen generator rating by the CO2 scrubber rating.
 * 
 * Both the oxygen generator rating and the CO2 scrubber rating are values that can be found in your diagnostic report - finding them is the tricky part. Both values are located using a similar process that involves filtering out values until only one remains. Before searching for either rating value, start with the full list of binary numbers from your diagnostic report and consider just the first bit of those numbers. Then:
 * 
 * - Keep only numbers selected by the bit criteria for the type of rating value for which you are searching. Discard numbers which do not match the bit criteria.
 * - If you only have one number left, stop; this is the rating value for which you are searching.
 * - Otherwise, repeat the process, considering the next bit to the right.
 * 
 * The bit criteria depends on which type of rating value you want to find:
 * 
 * - To find oxygen generator rating, determine the most common value (0 or 1) in the current bit position, and keep only numbers with that bit in that position. If 0 and 1 are equally common, keep values with a 1 in the position being considered.
 * - To find CO2 scrubber rating, determine the least common value (0 or 1) in the current bit position, and keep only numbers with that bit in that position. If 0 and 1 are equally common, keep values with a 0 in the position being considered.
 * 
 * For example, to determine the oxygen generator rating value using the same example diagnostic report from above:
 * 
 * - Start with all 12 numbers and consider only the first bit of each number. There are more 1 bits (7) than 0 bits (5), so keep only the 7 numbers with a 1 in the first position: 11110, 10110, 10111, 10101, 11100, 10000, and 11001.
 * - Then, consider the second bit of the 7 remaining numbers: there are more 0 bits (4) than 1 bits (3), so keep only the 4 numbers with a 0 in the second position: 10110, 10111, 10101, and 10000.
 * - In the third position, three of the four numbers have a 1, so keep those three: 10110, 10111, and 10101.
 * - In the fourth position, two of the three numbers have a 1, so keep those two: 10110 and 10111.
 * - In the fifth position, there are an equal number of 0 bits and 1 bits (one each). So, to find the oxygen generator rating, keep the number with a 1 in that position: 10111.
 * - As there is only one number left, stop; the oxygen generator rating is 10111, or 23 in decimal.
 * 
 * Then, to determine the CO2 scrubber rating value from the same example above:
 * 
 * - Start again with all 12 numbers and consider only the first bit of each number. There are fewer 0 bits (5) than 1 bits (7), so keep only the 5 numbers with a 0 in the first position: 00100, 01111, 00111, 00010, and 01010.
 * - Then, consider the second bit of the 5 remaining numbers: there are fewer 1 bits (2) than 0 bits (3), so keep only the 2 numbers with a 1 in the second position: 01111 and 01010.
 * - In the third position, there are an equal number of 0 bits and 1 bits (one each). So, to find the CO2 scrubber rating, keep the number with a 0 in that position: 01010.
 * - As there is only one number left, stop; the CO2 scrubber rating is 01010, or 10 in decimal.
 * 
 * Finally, to find the life support rating, multiply the oxygen generator rating (23) by the CO2 scrubber rating (10) to get 230.
 * 
 * Use the binary numbers in your diagnostic report to calculate the oxygen generator rating and CO2 scrubber rating, then multiply them together. What is the life support rating of the submarine? (Be sure to represent your answer in decimal, not binary.)
 */

import java.io.File
import java.io.InputStream
import kotlin.math.pow

private val DIAGNOSTIC_LINE_LENGTH = 12

private fun calculateResult1(diagnosticLines: List<List<Int>>): Long {
    var gamma: Long = 0
    var epsilon: Long = 0
    (0 until DIAGNOSTIC_LINE_LENGTH).map { index ->
        val oneCount = diagnosticLines.sumOf { if (it[index] == 1) 1L else 0L }
        val zeroCount = diagnosticLines.sumOf { if (it[index] == 0) 1L else 0L }
        if (oneCount > zeroCount) {
            gamma += 2.toDouble().pow(DIAGNOSTIC_LINE_LENGTH - 1 - index).toLong()
        } else {
            epsilon += 2.toDouble().pow(DIAGNOSTIC_LINE_LENGTH - 1 - index).toLong()
        }
    }

    return (gamma * epsilon)
}

private fun calculateResult2(diagnosticLines: List<List<Int>>): Long {
    var oxygenGeneratorRatingArray = diagnosticLines.toList()
    var co2ScrubberRatingArray = diagnosticLines.toList()
    var index = 0
    while (!(oxygenGeneratorRatingArray.size == 1 && co2ScrubberRatingArray.size == 1) && index < DIAGNOSTIC_LINE_LENGTH) {
        if (oxygenGeneratorRatingArray.size > 1) {
            val mostCommonBit = getMostCommonBit(oxygenGeneratorRatingArray.map { it[index] })
            oxygenGeneratorRatingArray = oxygenGeneratorRatingArray.filter {
                it[index] == mostCommonBit
            }
        }
        if (co2ScrubberRatingArray.size > 1) {
            val leastCommonBit = getLeastCommonBit(co2ScrubberRatingArray.map { it[index] })
            co2ScrubberRatingArray = co2ScrubberRatingArray.filter {
                it[index] == leastCommonBit
            }
        }
        ++index
    }
    val oxygenGeneratorRating = binaryArrayToLong(oxygenGeneratorRatingArray.first())
    val co2ScrubberRating = binaryArrayToLong(co2ScrubberRatingArray.first())

    return (oxygenGeneratorRating * co2ScrubberRating)
}

private fun binaryArrayToLong(input: List<Int>): Long {
    return input.foldIndexed(0) { i, sum, element ->
        sum + element * 2.0.pow(input.size - 1 - i).toInt()
    }
}

private fun getMostCommonBit(input: List<Int>): Int =
    input.groupingBy { it }.eachCount().let { map -> map.toSortedMap(compareByDescending<Int> { map[it] }.thenByDescending { it })}.firstKey()

private fun getLeastCommonBit(input: List<Int>): Int =
    input.groupingBy { it }.eachCount().let { map -> map.toSortedMap(compareBy<Int> { map[it] }.thenBy { it })}.firstKey()


fun main() {
    val inputStream: InputStream = File("./day03_dataset.txt").inputStream()

    val diagnosticLines = inputStream.bufferedReader().useLines { lines ->
        lines.toList().map { line ->
            line.toCharArray().map { char -> char.toString().toInt() }
        }
    }

    println("Result #1 = ${calculateResult1(diagnosticLines)}")
    println("Result #2 = ${calculateResult2(diagnosticLines)}")
}
