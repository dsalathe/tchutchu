package ch.coachdave.tchutchu.game

import ch.coachdave.tchutchu.TestRandomizer.RANDOM_ITERATIONS
import ch.coachdave.tchutchu.UnitSpec

import scala.util.Random.nextInt

class TripTest extends UnitSpec:

  "Trip" should "work on known examples" in {
    val from: List[Station] = List(Station(0, "Lausanne"), Station(1, "Neuchâtel"))
    val to: List[Station] = List(Station(2, "Berne"), Station(3, "Zürich"), Station(4, "Coire"))
    val points = 17
    val expectedIds: Set[(Int, Int)] = Set((0,2), (0,3), (0,4), (1,2), (1,3), (1,4))
    val all = Trip.all(from, to, points)
    all should have size (from.size * to.size)
    val actualIds: Set[(Int, Int)] = all.map{case Trip(Station(from, _), Station(to, _), points) => (from, to)}.toSet
    actualIds shouldEqual expectedIds
  }

  it should "fail with invalid points" in {
    val s1: Station = Station(0, "Lausanne")
    val s2: Station = Station(1, "EPFL")
    the [IllegalArgumentException] thrownBy Trip(s1, s2, 0) should have message "requirement failed: 0 is not strictly positive"
    the [IllegalArgumentException] thrownBy Trip(s1, s2, -1) should have message "requirement failed: -1 is not strictly positive"
  }

  it should "be correctly instantiated" in {
    (0 until RANDOM_ITERATIONS) foreach { i =>
      val fromId = nextInt(100)
      val from = Station(fromId, "Lausanne")
      val to = Station(fromId+1, "Neuchâtel")
      val points = 1 + nextInt(10)
      val trip = Trip(from, to, points)
      trip.from shouldEqual from
      trip.to shouldEqual to
      trip.points shouldEqual points
    }
  }

  it should "returns correct sign of points with their respective connections" in {
    val connected = FullConnectivity
    val disconnected = NoConnectivity
    (0 to RANDOM_ITERATIONS) foreach {i =>
      val fromId = nextInt(100)
      val from  = Station(fromId, "Lugano")
      val to = Station(fromId+1, "Wassen")
      val points = 1  + nextInt(10)
      val trip = Trip(from, to, points)
      trip.points(connected) shouldEqual points
      trip.points(disconnected) shouldEqual -points
    }
  }

object FullConnectivity extends StationConnectivity {override def connected(s1: Station, s2: Station) = true}
object NoConnectivity extends StationConnectivity {override def connected(s1: Station, s2: Station) = false}
