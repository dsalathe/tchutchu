package ch.coachdave.tchutchu.game

import collection.JavaConverters.*
import ch.coachdave.tchutchu.{UnitSpec, TestRandomizer}

class PublicPlayerStateTest extends UnitSpec:

  val TOTAL_CAR_COUNT = 40
  scala.util.Random.setSeed(2021)

  def randomRoutes(): List[Route] =
    val shuffledRoutes = scala.util.Random.shuffle(ChMap.routes)
    val maxRouteCount = scala.util.Random.nextInt(TOTAL_CAR_COUNT)
    shuffledRoutes.scanLeft(0)((length, route) => length + route.length).tail.zip(shuffledRoutes).takeWhile((totalLength, _) => totalLength <= maxRouteCount).map(_._2)

  "Public Player State" should "not be instantiated with negative size" in {
    assertThrows[IllegalArgumentException](PublicPlayerState(-1, 1, Nil))
    assertThrows[IllegalArgumentException](PublicPlayerState(1, -1, Nil))
  }

  it should "count car correctly" in {
    (0 until TestRandomizer.RANDOM_ITERATIONS) foreach { _ =>
      val routes = randomRoutes()
      val routesLength = routes.map(_.length).sum
      val state = PublicPlayerState(0, 0, routes)
      state.carCount shouldEqual TOTAL_CAR_COUNT - routesLength
    }
  }

  it should "claim points correctly" in {
    import Constants.ROUTE_CLAIM_POINTS
    val claimPoints = ROUTE_CLAIM_POINTS.toVector.map(_ + 0)
    (0 until TestRandomizer.RANDOM_ITERATIONS) foreach { _ =>
      val routes = randomRoutes()
      val points = routes.map(r => claimPoints(r.length)).sum
      val state = PublicPlayerState(0, 0, routes)
      state.claimPoints shouldEqual points
    }
  }
