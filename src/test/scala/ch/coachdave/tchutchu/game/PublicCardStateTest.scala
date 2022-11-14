package ch.coachdave.tchutchu.game

import ch.coachdave.tchutchu.UnitSpec

class PublicCardStateTest extends UnitSpec:

  val FACE_UP_CARDS = List(Card.BLUE, Card.BLACK, Card.ORANGE, Card.ORANGE, Card.RED)

  "PublicCardState" should "fail with invalid number of FaceUpCards" in {
    assertThrows[IllegalArgumentException](PublicCardState(Card.RED::FACE_UP_CARDS, 0, 0))
  }

  it should "fail with negative deck or discard size" in {
    assertThrows[IllegalArgumentException](PublicCardState(FACE_UP_CARDS, -1, 0))
    assertThrows[IllegalArgumentException](PublicCardState(FACE_UP_CARDS, 0, -1))
  }
