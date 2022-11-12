package ch.coachdave.tchutchu.game

import Constants.FACE_UP_CARDS_COUNT
import ch.coachdave.tchutchu.SortedBag

import java.util.Objects

class CardState private (faceUpCards: List[Card], private val deck: Deck[Card], private val discarded: SortedBag[Card]) extends PublicCardState(faceUpCards, deck.size, discarded.size):

  def withDrawnFaceUpCard(slot: Int): CardState =
    new CardState(faceUpCards.updated(Objects.checkIndex(slot, FACE_UP_CARDS_COUNT), deck.topCard), deck.withoutTopCard, discarded)

  def topDeckCard: Card = deck.topCard

  def withoutTopDeckCard: CardState = new CardState(faceUpCards, deck.withoutTopCard, discarded)

  def withDeckRecreatedFromDiscards: CardState =
    require(deck.isEmpty)
    new CardState(faceUpCards, Deck.of(discarded), SortedBag.of())

  def withMoreDiscardedCards(additionalDiscards: SortedBag[Card]): CardState = new CardState(faceUpCards, deck, discarded.union(additionalDiscards))

object CardState:
  def of(deck: Deck[Card]): CardState = new CardState(deck.topCards(FACE_UP_CARDS_COUNT), deck.withoutTopCards(FACE_UP_CARDS_COUNT), SortedBag.of())
