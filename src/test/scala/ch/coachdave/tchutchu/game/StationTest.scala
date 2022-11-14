package ch.coachdave.tchutchu.game

import ch.coachdave.tchutchu.TestRandomizer._
import ch.coachdave.tchutchu.UnitSpec


class StationTest extends UnitSpec:

  "Station" should "not be instantiated with negative ID" in {
    assertThrows[IllegalArgumentException](Station(-1, "Lausanne"))
  }

  it should "have ID correctly retrieved" in {
    (0 to RANDOM_ITERATIONS) foreach { _ =>
      val id: Int = scala.util.Random.nextInt(Int.MaxValue)
      val station: Station = Station(id, "Lausanne")
      id shouldEqual station.id
    }
  }
