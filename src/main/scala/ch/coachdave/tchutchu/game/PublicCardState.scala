package ch.coachdave.tchutchu.game

import java.util.Objects
import scala.util.{Success, Try, Failure}

val FACE_UP_CARDS_COUNT = 5 // hardcoded because Constants.FACE_UP_CARDS_COUNT does not initialize correctly

case class PublicCardState(faceUpCards: List[Card], deckSize: Int, discardsSize: Int):
  require(faceUpCards.size == FACE_UP_CARDS_COUNT)
  require(deckSize >= 0)
  require(discardsSize >= 0)

  def faceUpCard(slot: Int): Option[Card] = Try(faceUpCards(Objects.checkIndex(slot, FACE_UP_CARDS_COUNT))) match
    case Success(value) => Some(value)
    case Failure(_) => None

  def isDeckEmpty: Boolean = deckSize == 0
