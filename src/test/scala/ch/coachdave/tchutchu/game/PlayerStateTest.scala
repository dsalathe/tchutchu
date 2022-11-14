package ch.coachdave.tchutchu.game

import ch.coachdave.tchutchu.{SortedBag, UnitSpec}

class PlayerStateTest extends UnitSpec:

  val INITIAL_CARD_COUNT = 4
  val TOTAL_CAR_COUNT = 40

  val COLORS = Color.ALL
  val CAR_CARDS = Card.CARS

  "Player State" should "correctly return initial state" in {
    val initialCards = SortedBag.of(INITIAL_CARD_COUNT, Card.BLUE)
    val s = PlayerState.initial(initialCards)
    s.tickets shouldEqual SortedBag.of()
    s.cards shouldEqual initialCards
    s.routes shouldEqual Nil
    s.carCount shouldEqual TOTAL_CAR_COUNT
    s.claimPoints shouldEqual 0
    s.ticketPoints shouldEqual 0
    s.finalPoints shouldEqual 0
  }
