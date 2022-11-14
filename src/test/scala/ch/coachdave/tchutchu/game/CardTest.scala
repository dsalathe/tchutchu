package ch.coachdave.tchutchu.game

import ch.coachdave.tchutchu.UnitSpec
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CardTest extends UnitSpec:

  "Card Enum" should "contain values in correct order" in {
    val cards: Array[Card] = Array(Card.BLACK, Card.VIOLET, Card.BLUE, Card.GREEN, Card.YELLOW, Card.ORANGE, Card.RED, Card.WHITE, Card.LOCOMOTIVE)
    cards shouldEqual Card.values
  }

  it should "have 9 elements" in {
    Card.COUNT shouldEqual 9
  }

  it should "retrieve every colors correctly" in {
    val colors = Color.ALL;
    Card.CARS foreach (card => colors(card.ordinal) shouldEqual card.color.get)
  }
