import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt


data class Point(var latitude: Float, val longitude: Float)
data class Participants(val passengers: Collection<Person>, val drivers: Collection<Person>)
data class Person(val id: UUID, val finishPoint: Point)

val jetBrainsLocation = Point(59.981525F, 30.214502F)

fun main() {
    val (passengers, drivers) = readPoints()
    for (passenger in passengers) {
        val suggestedDrivers = suggestDrivers(passenger, drivers)
        println("Passenger point: ${passenger.finishPoint.latitude}, ${passenger.finishPoint.longitude}")
        for (driver in suggestedDrivers) {
            println("  ${driver.finishPoint.latitude}, ${driver.finishPoint.longitude}")
        }
    }
}

fun distanceBetweenTwoPoint(first: Point, second: Point): Float {

    return sqrt((first.latitude - second.latitude).pow(2) + (first.longitude - second.longitude).pow(2))

}

fun comfort(driver: Person, passenger: Person): Float {
    return ((distanceBetweenTwoPoint(jetBrainsLocation, passenger.finishPoint)
            + distanceBetweenTwoPoint(passenger.finishPoint, driver.finishPoint))
            - distanceBetweenTwoPoint(jetBrainsLocation, driver.finishPoint))

}

fun compareTwoResult(first: Float, second: Float): Int {
    return when {
        first > second -> {
            1
        }
        first < second -> {
            -1
        }
        else -> {
            0
        }
    }
}


fun suggestDrivers(passenger: Person, drivers: Collection<Person>): Collection<Person> {
    val comparator = Comparator<Person> { driver1: Person, driver2: Person -> (compareTwoResult(comfort(driver1, passenger), comfort(driver2, passenger))) }
    return drivers.sortedWith(comparator)
}

private fun readPoints(): Participants {
    val pathToResource = Paths.get(Point::class.java.getResource("latlons").toURI())
    val allPoints = Files.readAllLines(pathToResource).map { asPoint(it) }.shuffled()
    val passengers = allPoints.slice(0..9).map { Person(UUID.randomUUID(), it) }
    val drivers = allPoints.slice(10..19).map { Person(UUID.randomUUID(), it) }
    return Participants(passengers, drivers)
}

private fun asPoint(it: String): Point {
    val (lat, lon) = it.split(", ")
    return Point(lat.toFloat(), lon.toFloat())
}
