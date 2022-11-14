package ch.coachdave.tchutchu.game

import ch.coachdave.tchutchu.UnitSpec

class TicketTest extends UnitSpec:

  val s1: Station = Station(0, "From")
  val s2: Station = Station(1, "To")

  val BER: Station = Station(0, "Berne")
  val LAU: Station = Station(1, "Lausanne")
  val STG: Station = Station(2, "Saint-Gall")

  // Stations - countries
  val DE1: Station = Station(3, "Allemagne")
  val DE2: Station = Station(4, "Allemagne")
  val DE3: Station = Station(5, "Allemagne")
  val AT1: Station = Station(6, "Autriche")
  val AT2: Station = Station(7, "Autriche")
  val IT1: Station = Station(8, "Italie")
  val IT2: Station = Station(9, "Italie")
  val IT3: Station = Station(10, "Italie")
  val FR1: Station = Station(11, "France")
  val FR2: Station = Station(12, "France")

  // Countries
  val DE: List[Station] = List(DE1, DE2, DE3)
  val AT: List[Station] = List(AT1, AT2)
  val IT: List[Station] = List(IT1, IT2, IT3)
  val FR: List[Station] = List(FR1, FR2)

  val LAU_STG: Ticket = Ticket(LAU, STG, 13)
  val LAU_BER: Ticket = Ticket(LAU, BER, 2)
  val BER_NEIGHBORS: Ticket = ticketToNeighbors(List(BER), 6, 11, 8, 5)
  val FR_NEIGHBORS: Ticket = ticketToNeighbors(FR, 5, 14, 11, 0)

  class TestConnectivity(stations1: List[Station], stations2: List[Station]) extends StationConnectivity:
    require(stations1.size == stations2.size, s"size mismatch between stations: ${stations1.size} vs ${stations2.size}")

    override def connected(s1: Station, s2: Station): Boolean =
      stations1.zip(stations2).exists{case (t1, t2) => s1 == t1 && s2 == t2 || s1 == t2 && s2 == t1}

  end TestConnectivity

  object FullConnectivity extends StationConnectivity:
    override def connected(s1: Station, s2: Station): Boolean = true


  private def ticketToNeighbors(from: List[Station], de: Int, at: Int, it: Int, fr: Int) =
    def allOrNothing(from: List[Station], country: List[Station], points: Int) =
      if points == 0 then Nil else Trip.all(from, country, points)
    val trips: List[Trip] = allOrNothing(from, DE, de) ++ allOrNothing(from, AT, at) ++ allOrNothing(from, IT, it) ++ allOrNothing(from, FR, fr)
    Ticket(trips)

  "Ticket" should "not be instantiated with no trip" in {
    the [IllegalArgumentException] thrownBy Ticket(Nil) should have message "requirement failed: No trips given"
  }

  it should "output a correct text for simple ticket" in {
    Ticket(s1, s2, 1).text shouldEqual "From - To (1)"
  }

  it should "output a correct text for single trip ticket" in {
    Ticket(List(Trip(s1, s2, 15))).text shouldEqual "From - To (15)"
  }

  it should "output a correct text for multiple trip tickets" in {
    BER_NEIGHBORS.text shouldEqual "Berne - {Allemagne (6), Autriche (11), France (5), Italie (8)}"
    FR_NEIGHBORS.text shouldEqual "France - {Allemagne (5), Autriche (14), Italie (11)}"
  }

  it should "compute points correctly when no connectivity" in {
    val connectivity: StationConnectivity = new TestConnectivity(Nil, Nil)
    LAU_STG.points(connectivity) shouldEqual -13
    BER_NEIGHBORS.points(connectivity) shouldEqual -5
    FR_NEIGHBORS.points(connectivity) shouldEqual -5
  }

  it should "compute points correctly when full connectivity" in {
    val connectivity: StationConnectivity = FullConnectivity
    LAU_STG.points(connectivity) shouldEqual 13
    BER_NEIGHBORS.points(connectivity) shouldEqual 11
    FR_NEIGHBORS.points(connectivity) shouldEqual 14
  }

  it should "compute points correctly when partial connectivity" in {
    val connectivity: StationConnectivity = new TestConnectivity(
      List(LAU, BER, BER, FR1, FR2, FR2),
      List(BER, FR2, DE3, IT1, IT2, DE1))

    LAU_STG.points(connectivity) shouldEqual -13
    BER_NEIGHBORS.points(connectivity) shouldEqual 6
    FR_NEIGHBORS.points(connectivity) shouldEqual 11
  }

  it should "compare correctly on know tickets" in {
    LAU_BER.compare(LAU_STG) should be < 0
    LAU_BER.compare(FR_NEIGHBORS) should be > 0
    LAU_BER.compare(LAU_BER) shouldEqual 0
  }
