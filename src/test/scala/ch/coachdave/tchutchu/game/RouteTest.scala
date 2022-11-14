package ch.coachdave.tchutchu.game

import Color.*
import ch.coachdave.tchutchu.{UnitSpec, SortedBag}

class RouteTest extends UnitSpec:
  val s1 = Station(0, "Lausanne")
  val s2 = Station(1, "EPFL")

  "Route" should "not be instantiated with two identical stations" in {
    val s: Station = Station(0, "Lausanne")
    assertThrows[IllegalArgumentException](Route("id", s, s, 1, Route.Level.UNDERGROUND, Some(BLACK)))
  }

  it should "not be instantiated with invalid length" in {
    assertThrows[IllegalArgumentException](Route("id", s1, s2, 0, Route.Level.OVERGROUND, Some(BLACK)))
    assertThrows[IllegalArgumentException](Route("id", s1, s2, 7, Route.Level.OVERGROUND, Some(BLACK)))
  }

  it should "return opposite station" in {
    Route("id", s1, s2, 1, Route.Level.OVERGROUND, Some(BLACK)).stationOpposite(s1) shouldEqual Some(s2)
  }

  it should "return None with invalid route" in {
    val s3 = Station(3, "Zurich")
    Route("id", s1, s2, 1, Route.Level.OVERGROUND, Some(BLACK)).stationOpposite(s3) shouldEqual None
  }

  it should "return correct number of possible claimed cards with overground colored route" in {
    Color.ALL.zip(Card.CARS).foreach { (color, card) =>
      (1 to 6) foreach { l =>
        val r = Route("id", s1, s2, l, Route.Level.OVERGROUND, Some(color))
        r.possibleClaimCards shouldEqual List(SortedBag.of(l, card))
      }
    }
  }

  it should "return correct number of possible claimed cards with overground neutral route" in {
    (1 to 6) foreach { l =>
      val r = Route("id", s1, s2, l, Route.Level.OVERGROUND, None)
      r.possibleClaimCards shouldEqual List(SortedBag.of(l, Card.BLACK), SortedBag.of(l, Card.VIOLET),
        SortedBag.of(l, Card.BLUE), SortedBag.of(l, Card.GREEN), SortedBag.of(l, Card.YELLOW), SortedBag.of(l, Card.ORANGE), SortedBag.of(l, Card.RED), SortedBag.of(l, Card.WHITE))
    }
  }

  it should "return correct number of possible claimed cards with underground colored route" in {
    Color.ALL.zip(Card.CARS).foreach { (color, card) =>
      (1 to 6) foreach { l =>
        val r = Route("id", s1, s2, l, Route.Level.UNDERGROUND, Some(color))
        val expected = (0 to l).map(locomotives => SortedBag.of(l - locomotives, card, locomotives, Card.LOCOMOTIVE))
        r.possibleClaimCards shouldEqual expected
      }
    }
  }

  it should "return correct number of possible claimed cards with underground neutral route" in {
    (1 to 6) foreach { l =>
      val r = Route("id", s1, s2, l, Route.Level.UNDERGROUND, None)
      val expected = (0 to l).flatMap { locomotives =>
        if l == locomotives then List(SortedBag.of(locomotives, Card.LOCOMOTIVE)) else
          Card.CARS.map(card => SortedBag.of(l - locomotives, card, locomotives, Card.LOCOMOTIVE))
      }
      r.possibleClaimCards shouldEqual expected
    }
  }

  it should "count correctly additional claim cards with colored cards only" in {
    (1 to 6) foreach { l =>
      Color.ALL map { color =>
        val matchingCard = Card.of(color)
        val nonMatchingCard = if color == BLACK then Card.WHITE else Card.BLACK
        val claimCards = SortedBag.of(l, matchingCard)
        val r = Route("id", s1, s2, l, Route.Level.UNDERGROUND, Some(color))
        (0 to 3) foreach { m =>
          (0 to m) foreach { locomotives =>
            val drawnBuilder = new SortedBag.Builder[Card]
            drawnBuilder.add(locomotives, Card.LOCOMOTIVE)
            drawnBuilder.add(m - locomotives, matchingCard)
            drawnBuilder.add(3 - m, nonMatchingCard)
            val drawn = drawnBuilder.build()
            r.additionalClaimCardsCount(claimCards, drawn) shouldEqual m
          }
        }
      }
    }
  }

  it should "count correctly additional claim cards with locomotives only" in {
    (1 to 6) foreach { l =>
      Color.ALL map { color =>
        val matchingCard = Card.of(color)
        val nonMatchingCard = if color == BLACK then Card.WHITE else Card.BLACK
        val claimCards = SortedBag.of(l, Card.LOCOMOTIVE)
        val r = Route("id", s1, s2, l, Route.Level.UNDERGROUND, Some(color))
        (0 to 3) foreach { m =>
          (0 to m) foreach { locomotives =>
            val drawnBuilder = new SortedBag.Builder[Card]
            drawnBuilder.add(locomotives, Card.LOCOMOTIVE)
            drawnBuilder.add(m - locomotives, matchingCard)
            drawnBuilder.add(3 - m, nonMatchingCard)
            val drawn = drawnBuilder.build()
            r.additionalClaimCardsCount(claimCards, drawn) shouldEqual locomotives
          }
        }
      }
    }
  }

  it should "count correctly additional claim cards with mixed cards" in {
    (2 to 6) foreach { l =>
      Color.ALL map { color =>
        val matchingCard = Card.of(color)
        val nonMatchingCard = if color == BLACK then Card.WHITE else Card.BLACK
        (1 until l) foreach {claimLoc =>
          val claimCards = SortedBag.of(l - claimLoc, matchingCard, claimLoc, Card.LOCOMOTIVE)
          val r = Route("id", s1, s2, l, Route.Level.UNDERGROUND, Some(color))
          (0 to 3) foreach { m =>
            (0 to m) foreach { locomotives =>
              val drawnBuilder = new SortedBag.Builder[Card]
              drawnBuilder.add(locomotives, Card.LOCOMOTIVE)
              drawnBuilder.add(m - locomotives, matchingCard)
              drawnBuilder.add(3 - m, nonMatchingCard)
              val drawn = drawnBuilder.build()
              r.additionalClaimCardsCount(claimCards, drawn) shouldEqual m
            }
          }
        }
      }
    }
  }
