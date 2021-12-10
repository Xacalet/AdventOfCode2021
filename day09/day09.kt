/**
 * ADVENT OF CODE 2021 (https://adventofcode.com/2021/)
 * 
 * Solution to day 9 (https://adventofcode.com/2021/day/9)
 * 
 * --- Day 9: Smoke Basin ---
 * 
 * These caves seem to be lava tubes. Parts are even still volcanically active; small hydrothermal vents release smoke into the caves that slowly settles like rain.
 * 
 * If you can model how the smoke flows through the caves, you might be able to avoid it and be that much safer. The submarine generates a heightmap of the floor of the nearby caves for you (your puzzle input).
 * 
 * Smoke flows to the lowest point of the area it's in. For example, consider the following heightmap:
 * 
 * 2199943210
 * 3987894921
 * 9856789892
 * 8767896789
 * 9899965678
 * 
 * Each number corresponds to the height of a particular location, where 9 is the highest and 0 is the lowest a location can be.
 * 
 * Your first goal is to find the low points - the locations that are lower than any of its adjacent locations. Most locations have four adjacent locations (up, down, left, and right); locations on the edge or corner of the map have three or two adjacent locations, respectively. (Diagonal locations do not count as adjacent.)
 * 
 * In the above example, there are four low points, all highlighted: two are in the first row (a 1 and a 0), one is in the third row (a 5), and one is in the bottom row (also a 5). All other locations on the heightmap have some lower adjacent location, and so are not low points.
 * 
 * The risk level of a low point is 1 plus its height. In the above example, the risk levels of the low points are 2, 1, 6, and 6. The sum of the risk levels of all low points in the heightmap is therefore 15.
 * 
 * Find all of the low points on your heightmap. What is the sum of the risk levels of all low points on your heightmap?
 * 
 * --- Part Two ---
 * 
 * Next, you need to find the largest basins so you know what areas are most important to avoid.
 * 
 * A basin is all locations that eventually flow downward to a single low point. Therefore, every low point has a basin, although some basins are very small. Locations of height 9 do not count as being in any basin, and all other locations will always be part of exactly one basin.
 * 
 * The size of a basin is the number of locations within the basin, including the low point. The example above has four basins.
 * 
 * The top-left basin, size 3:
 * 
 * 2199943210
 * 3987894921
 * 9856789892
 * 8767896789
 * 9899965678
 * 
 * The top-right basin, size 9:
 * 
 * 2199943210
 * 3987894921
 * 9856789892
 * 8767896789
 * 9899965678
 * 
 * The middle basin, size 14:
 * 
 * 2199943210
 * 3987894921
 * 9856789892
 * 8767896789
 * 9899965678
 * 
 * The bottom-right basin, size 9:
 * 
 * 2199943210
 * 3987894921
 * 9856789892
 * 8767896789
 * 9899965678
 * 
 * Find the three largest basins and multiply their sizes together. In the above example, this is 9 * 14 * 9 = 1134.
 * 
 * What do you get if you multiply together the sizes of the three largest basins?
 */

import java.io.File

private data class Location(
    val x: Int,
    val y: Int,
    val height: Int
)

private fun calculateResult1(locations: List<List<Location>>): Int {
    return getLowestPoints(locations).sumOf { it.height + 1 }
}

@OptIn(ExperimentalStdlibApi::class)
private fun calculateResult2(locations: List<List<Location>>): Int {
    return buildList<List<Location>> {
        getLowestPoints(locations).forEach { location ->
            add(exploreLocation(locations, location, mutableListOf()))
        }
    }.sortedByDescending { basin -> basin.size }.take(3)
    .map { basin -> basin.count() }
    .reduce { acc, i -> acc * i }
}

@OptIn(ExperimentalStdlibApi::class)
private fun getLowestPoints(locations: List<List<Location>>): List<Location> {
    return buildList<Location> {
        locations.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, location ->
                if ((columnIndex == 0 || row[columnIndex-1].height > location.height) &&
                (columnIndex == row.size.dec() || row[columnIndex+1].height > location.height) &&
                (rowIndex == 0 || locations[rowIndex-1][columnIndex].height > location.height) &&
                (rowIndex == locations.size.dec() || locations[rowIndex+1][columnIndex].height > location.height)) {
                    add(location)
                }
            }
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
private fun exploreLocation(locations: List<List<Location>>, location: Location, visitedLocations: MutableList<Location>): List<Location> {
    return if (!visitedLocations.contains(location) && location.height < 9) {
        visitedLocations.add(location)
        buildList<Location> {
            add(location)
            if(location.x > 0 && locations[location.x - 1][location.y].let { !visitedLocations.contains(it) && it.height > location.height} ) {
                addAll(exploreLocation(locations, locations[location.x - 1][location.y], visitedLocations))
            }
            if(location.x < locations.size.dec() && locations[location.x + 1][location.y].let { !visitedLocations.contains(it) && it.height > location.height} ) {
                addAll(exploreLocation(locations, locations[location.x + 1][location.y], visitedLocations))
            }
            if(location.y > 0 && locations[location.x][location.y - 1].let { !visitedLocations.contains(it) && it.height > location.height } ) {
                addAll(exploreLocation(locations, locations[location.x][location.y - 1], visitedLocations))
            }
            if(location.y < locations[0].size.dec() && locations[location.x][location.y + 1].let { !visitedLocations.contains(it) && it.height > location.height} ) {
                addAll(exploreLocation(locations, locations[location.x][location.y + 1], visitedLocations))
            } 
        }
    } else {
        emptyList()
    }
}

fun main() {
    val locations = File("./day09_dataset.txt").readLines().mapIndexed { rowIndex, row -> 
        row.mapIndexed { columnIndex, location -> 
            Location(rowIndex, columnIndex, location.toString().toInt())
        } 
    }
    
    println("Result #1 = ${calculateResult1(locations)}")
    println("Result #2 = ${calculateResult2(locations)}")
}
