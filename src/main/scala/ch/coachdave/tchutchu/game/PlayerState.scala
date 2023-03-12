package ch.coachdave.tchutchu.game

import ch.coachdave.tchutchu.SortedBag
import scala.jdk.CollectionConverters.*
import java.util.stream.Collectors

class PlayerState(val tickets: SortedBag[Ticket], val cards: SortedBag[Card], routes: List[Route]) extends PublicPlayerState(tickets.size(), cards.size(), routes):

  def withAddedTickets(newTickets: SortedBag[Ticket]): PlayerState = new PlayerState(tickets.union(newTickets), cards, routes)
  def withAddedCard(card: Card): PlayerState = withAddedCards(SortedBag.of(card))
  def withAddedCards(additionalCards: SortedBag[Card]) = new PlayerState(tickets, cards.union(additionalCards), routes)
  def canClaimRoute(route: Route): Boolean = carCount >= route.length && route.possibleClaimCards.exists(cards.contains)
  def possibleClaimCards(route: Route): List[SortedBag[Card]] =
    require(carCount >= route.length)
    route.possibleClaimCards.filter(cards.contains)

  def possibleAdditionalCards(additionalCardsCount: Int, initialCards: SortedBag[Card], drawnCard: SortedBag[Card]): List[SortedBag[Card]] =
    require(additionalCardsCount >= 1 && additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS)
    require(!initialCards.isEmpty && initialCards.toSet.size() <= 2)
    require(drawnCard.size() == Constants.ADDITIONAL_TUNNEL_CARDS)
    val remainingCards: SortedBag[Card] = cards.difference(initialCards)
    val possibleCards = SortedBag.of(remainingCards.stream().filter(card => initialCards.contains(card) || card == Card.LOCOMOTIVE).collect(Collectors.toList))
    if possibleCards.size() < additionalCardsCount then Nil else
      val options: List[SortedBag[Card]] = possibleCards.subsetsOfSize(additionalCardsCount).asScala.toList
      options.sortBy(_.countOf(Card.LOCOMOTIVE))

  def withClaimedRoute(route: Route, claimCards: SortedBag[Card]): PlayerState = new PlayerState(tickets, cards.difference(claimCards), route::routes)
  def ticketPoints: Int =
    val highestId = routes.flatMap{case Route(_, station1, station2, _, _, _) => List(station1.id, station2.id)}.maxOption.getOrElse(0)
    val stationPartitionBuilder = new StationPartition.Builder(highestId+1)
    routes.foreach{case Route(_, station1, station2, _, _, _) => stationPartitionBuilder.connect(station1, station2)}
    val stationPartition = stationPartitionBuilder.build
    tickets.toList.asScala.map(_.points(stationPartition)).sum

  def finalPoints: Int = ticketPoints + claimPoints


object PlayerState:
  def initial(initialCards: SortedBag[Card]): PlayerState =
    require(initialCards.size() == Constants.INITIAL_CARDS_COUNT)
    new PlayerState(SortedBag.of(), initialCards, Nil)
