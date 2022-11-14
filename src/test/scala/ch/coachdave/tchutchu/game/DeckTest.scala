package ch.coachdave.tchutchu.game

import ch.coachdave.tchutchu.UnitSpec
import ch.coachdave.tchutchu.{SortedBag, TestRandomizer}

import collection.JavaConverters.*

class DeckTest extends UnitSpec:

  def toSortedBag(cards: List[Int]) = SortedBag.of(cards.map(_.asInstanceOf[java.lang.Integer]).asJava)

  "Deck" should "be shuffable" in {
    val cards = (0 until 100).toList
    val cardsBag = toSortedBag(cards)
    (0 until TestRandomizer.RANDOM_ITERATIONS) foreach { i =>
      scala.util.Random.setSeed(i)
      val deck = Deck.of(cardsBag)
      val deckList = deck.topCards(deck.size)
      deckList shouldNot equal (cards)
      deckList.sorted shouldEqual cards
    }
  }

  it should "fail to give a card when deck is empty" in {
    val deck = Deck.of(SortedBag.of())
    deck shouldBe empty
    assertThrows[IllegalArgumentException](deck.topCard)
  }

  it should "return a top card when non-empty" in {
    val card = "card"
    val deck = Deck.of(SortedBag.of(10, card))
    deck.topCard shouldEqual card
  }

  it should "fail to return a deck without top card if already empty" in {
    assertThrows[IllegalArgumentException](Deck.of(SortedBag.of()).withoutTopCard)
  }

  it should "remove top card if deck is non-empty" in {
    val cards = toSortedBag((0 until 50).toList)
    var deck = Deck.of(cards)
    val actualCardsBuilder = new SortedBag.Builder[java.lang.Integer]
    while (!deck.isEmpty)
      actualCardsBuilder.add(deck.topCard)
      deck = deck.withoutTopCard

    actualCardsBuilder.build() shouldEqual cards
  }