package ch.coachdave.tchutchu.game

import ChMap.*
import ch.coachdave.tchutchu.{UnitSpec, TestRandomizer}

class StationPartitionTest extends UnitSpec:

  val reducedChStations: Vector[Station] = Vector(Station(0, "Berne"), Station(1, "Delémont"), Station(2, "Fribourg"),
    Station(3, "Interlaken"), Station(4, "La Chaux-de-Fonds"), Station(5, "Lausanne"), Station(6, "Lucerne"), Station(7, "Neuchâtel"),
    Station(8, "Olten"), Station(9, "Schwyz"), Station(10, "Soleure"), Station(11, "Wassen"), Station(12, "Yverdon"), Station(13, "Zoug"), Station(14, "Zürich"))

  "StationPartition" should "connect only with their own station initially" in {
    val stations = ChMap.stations
    val partition = new StationPartition.Builder(stations.length).build
    stations foreach { s1 =>
      stations foreach { s2 =>
        partition.connected(s1, s2) shouldBe (s1==s2)
      }
    }
  }

  it should "be idempotent" in {
    val stations = ChMap.stations
    val s0 = stations(0)
    val s1 = stations(1)
    val partition = new StationPartition.Builder(stations.length).connect(s0, s0).connect(s1, s1).connect(s0, s1).connect(s1, s0).build

    assert(partition.connected(s0, s0))
    assert(partition.connected(s1, s1))
    assert(partition.connected(s0, s1))
    assert(partition.connected(s1, s0))

  }

  it should "partition correctly on given example" in {
    val s = reducedChStations
    val partition = new StationPartition.Builder(s.size)
      .connect(s(5), s(2))  // Lausanne - Fribourg
      .connect(s(0), s(3))  // Berne - Interlaken
      .connect(s(0), s(2))  // Berne - Fribourg
      .connect(s(7), s(10)) // Neuchâtel - Soleure
      .connect(s(10), s(8)) // Soleure - Olten
      .connect(s(6), s(13)) // Lucerne - Zoug
      .connect(s(13), s(9)) // Zoug - Schwyz
      .connect(s(9), s(6))  // Schwyz - Lucerne
      .connect(s(9), s(11)) // Schwyz - Wassen
      .build

    assert(partition.connected(s(5), s(3))) // Lausanne - Interlaken
    assert(partition.connected(s(6), s(11))) // Lucerne - Wassen
    assert(partition.connected(s(13), s(11))) // Zoug - Wassen
    assert(partition.connected(s(9), s(11))) // Schwyz - Wassen
    partition.connected(s(0), s(6)) shouldBe false // Berne - Lucerne
  }

  it should "work on known example" in {
    val routes = List(BRI_LOC_1, BRI_SIO_1, MAR_SIO_1, LAU_MAR_1, GEN_LAU_1, GEN_YVE_1,
      LCF_YVE_1, DEL_LCF_1, DEL_SOL_1, OLT_SOL_1, BAL_OLT_1, BER_LUC_1, SCE_WIN_1)
    val maxId = routes.flatMap {case Route(_, s1, s2, _, _, _) => List(s1.id, s2.id)}.max
    (0 until TestRandomizer.RANDOM_ITERATIONS) foreach { _ =>
      val shuffledRoute = scala.util.Random.shuffle(routes)
      val partitionBuilder = new StationPartition.Builder(maxId + 1)
      shuffledRoute foreach {case Route(_, s1, s2, _, _, _) => partitionBuilder.connect(s1, s2)}
      val partition = partitionBuilder.build

      assert(partition.connected(LOC, BAL))
      assert(partition.connected(BER, LUC))
      assert(partition.connected(ZUR, ZUR))
      partition.connected(BER, SOL) shouldBe false
      partition.connected(LAU, LUC) shouldBe false
      partition.connected(ZUR, KRE) shouldBe false
    }


  }


