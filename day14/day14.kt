/**
 * ADVENT OF CODE 2021 (https://adventofcode.com/2021/)
 * 
 * Solution to day 14 (https://adventofcode.com/2021/day/14)
 * 
 * --- Day 14: Extended Polymerization ---
 * 
 * The incredible pressures at this depth are starting to put a strain on your submarine. The submarine has polymerization equipment that would produce suitable materials to reinforce the submarine, and the nearby volcanically-active caves should even have the necessary input elements in sufficient quantities.
 * 
 * The submarine manual contains instructions for finding the optimal polymer formula; specifically, it offers a polymer template and a list of pair insertion rules (your puzzle input). You just need to work out what polymer would result after repeating the pair insertion process a few times.
 * 
 * For example:
 * 
 *     NNCB
 * 
 *     CH -> B
 *     HH -> N
 *     CB -> H
 *     NH -> C
 *     HB -> C
 *     HC -> B
 *     HN -> C
 *     NN -> C
 *     BH -> H
 *     NC -> B
 *     NB -> B
 *     BN -> B
 *     BB -> N
 *     BC -> B
 *     CC -> N
 *     CN -> C
 * 
 * The first line is the polymer template - this is the starting point of the process.
 * 
 * The following section defines the pair insertion rules. A rule like AB -> C means that when elements A and B are immediately adjacent, element C should be inserted between them. These insertions all happen simultaneously.
 * 
 * So, starting with the polymer template NNCB, the first step simultaneously considers all three pairs:
 * 
 *  - The first pair (NN) matches the rule NN -> C, so element C is inserted between the first N and the second N.
 *  - The second pair (NC) matches the rule NC -> B, so element B is inserted between the N and the C.
 *  - The third pair (CB) matches the rule CB -> H, so element H is inserted between the C and the B.
 * 
 * Note that these pairs overlap: the second element of one pair is the first element of the next pair. Also, because all pairs are considered simultaneously, inserted elements are not considered to be part of a pair until the next step.
 * 
 * After the first step of this process, the polymer becomes NCNBCHB.
 * 
 * Here are the results of a few steps using the above rules:
 * 
 *     Template:     NNCB
 *     After step 1: NCNBCHB
 *     After step 2: NBCCNBBBCBHCB
 *     After step 3: NBBBCNCCNBBNBNBBCHBHHBCHB
 *     After step 4: NBBNBNBBCCNBCNCCNBBNBBNBBBNBBNBBCBHCBHHNHCBBCBHCB
 * 
 * This polymer grows quickly. After step 5, it has length 97; After step 10, it has length 3073. After step 10, B occurs 1749 times, C occurs 298 times, H occurs 161 times, and N occurs 865 times; taking the quantity of the most common element (B, 1749) and subtracting the quantity of the least common element (H, 161) produces 1749 - 161 = 1588.
 * 
 * Apply 10 steps of pair insertion to the polymer template and find the most and least common elements in the result. What do you get if you take the quantity of the most common element and subtract the quantity of the least common element?
 * 
 * --- Part Two ---
 * 
 * The resulting polymer isn't nearly strong enough to reinforce the submarine. You'll need to run more steps of the pair insertion process; a total of 40 steps should do it.
 * 
 * In the above example, the most common element is B (occurring 2192039569602 times) and the least common element is H (occurring 3849876073 times); subtracting these produces 2188189693529.
 * 
 * Apply 40 steps of pair insertion to the polymer template and find the most and least common elements in the result. What do you get if you take the quantity of the most common element and subtract the quantity of the least common element?
 * 
*/

import java.io.File

object Day14 {

    private data class StepParam (
        val step: Int,
        val pair: String
    )

    fun calculateResult1(initialTemplate: String, insertionRules: Map<String, String>): Long {
        var currentTemplate = initialTemplate
        for(i in 1..10) {
            currentTemplate = buildString {
                append(currentTemplate[0])
                currentTemplate.windowed(step = 1, size = 2).forEach { section ->
                    append(insertionRules[section])
                    append(section[1])
                }
            }
        }
        return currentTemplate.groupingBy { it }.eachCount().values.let { it.maxOrNull()!! - it.minOrNull()!! }.toLong()
    }

    fun calculateResult2(initialTemplate: String, insertionRules: Map<String, String>): Long {
        val initialCounter = HashMap(initialTemplate.groupingBy { it.toString() }.eachCount().mapValues{ it.value.toLong() })
        val finalCounter = initialTemplate.windowed(size = 2, step = 1).map { pair ->
            nextStep(1, pair, insertionRules, hashMapOf())
        }.reduce { accum, counter ->
            accum.mergeCounter(counter)
        }.apply { 
            mergeCounter(initialCounter)
        }
        return finalCounter.values.let { counts -> counts.maxOf { it } - counts.minOf { it } }
    }

    private fun HashMap<String, Long>.mergeCounter(counter: HashMap<String, Long>): HashMap<String, Long> {
        counter.keys.forEach { key -> merge(key, counter[key] ?: 0L, Long::plus) }
        return this
    }

    private fun nextStep(
        step: Int,
        pair: String,
        insertionRules: Map<String, String>,
        cachedIncStepResults: HashMap<StepParam, HashMap<String, Long>>
    ): HashMap<String, Long> {
        return cachedIncStepResults[StepParam(step, pair)] ?: insertionRules[pair]?.let { newLetter ->
            if (step < 40) {
                (pair[0] + newLetter + pair[1]).windowed(size = 2, step = 1).map { newPair ->
                    nextStep(step + 1, newPair, insertionRules, cachedIncStepResults).also { stepResult ->
                        cachedIncStepResults.put(StepParam(step + 1, newPair), HashMap(stepResult))
                    }
                }.reduce { accum, counter ->
                    accum.mergeCounter(counter)
                }.apply { 
                    merge(newLetter, 1, Long::plus) 
                }
            } else {
                hashMapOf(newLetter to 1)
            }
        } ?: hashMapOf()
    }

}

fun main() {
    val initialTemplate: String
    val insertionRules: Map<String, String>
    File("./day14_dataset.txt").readLines().let { lines ->
        initialTemplate = lines.first()
        insertionRules = lines.drop(2).map { rule -> 
            """(\w+) -> (\w+)""".toRegex().find(rule)!!.destructured.let { (sequence, newElement) ->
                sequence to newElement
            }
        }.toMap()
    }

    println("Result #1 = ${Day14.calculateResult1(initialTemplate, insertionRules)}")
    println("Result #2 = ${Day14.calculateResult2(initialTemplate, insertionRules)}")
}
