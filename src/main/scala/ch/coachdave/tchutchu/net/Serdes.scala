package ch.coachdave.tchutchu.net

import ch.coachdave.tchutchu.SortedBag
import ch.coachdave.tchutchu.game.Player.TurnKind
import ch.coachdave.tchutchu.game.{Card, ChMap, PlayerId, PlayerState, PublicCardState, PublicGameState, PublicPlayerState, Route, Ticket}

import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.regex.Pattern

object Serdes:

  private val encoder = Base64.getEncoder
  private val decoder = Base64.getDecoder

  val integer: Serde[Int] = Serde.of(_.toString, _.toInt)
  val string: Serde[String] = Serde.of(s => encoder.encodeToString(s.getBytes(StandardCharsets.UTF_8)), s => new String(decoder.decode(s), StandardCharsets.UTF_8))

  val playerId: Serde[Option[PlayerId]] = Serde.oneOf(PlayerId.ALL)
  val playerIdNoNull: Serde[PlayerId] = Serde.oneOfNoNull(PlayerId.ALL)
  val turnKind: Serde[TurnKind] = Serde.oneOfNoNull(TurnKind.values.toSeq)
  val card: Serde[Card] = Serde.oneOfNoNull(Card.ALL)
  val route: Serde[Route] = Serde.oneOfNoNull(ChMap.routes)
  val ticket: Serde[Ticket] = Serde.oneOfNoNull(ChMap.tickets)

  val listOfString: Serde[List[String]] = Serde.listOf(string, ",")
  val listOfCard: Serde[List[Card]] = Serde.listOf(card, ",")
  val listOfRoute: Serde[List[Route]] = Serde.listOf(route, ",")
  val bagOfCard: Serde[SortedBag[Card]] = Serde.bagOf(card, ",")
  val bagOfTicket: Serde[SortedBag[Ticket]] = Serde.bagOf(ticket, ",")
  val listOfBagOfCard: Serde[List[SortedBag[Card]]] = Serde.listOf(bagOfCard, ";")

  val publicCardState: Serde[PublicCardState] = Serde.of(pcs => List(listOfCard.serialize(pcs.faceUpCards), integer.serialize(pcs.deckSize), integer.serialize(pcs.discardsSize)).mkString(";"),
    s => {
      val Array(faceUpCards, deckSize, discardsSize) = s.split(Pattern.quote(";"), -1)
      PublicCardState(listOfCard.deserialize(faceUpCards), integer.deserialize(deckSize), integer.deserialize(discardsSize))
    })

  val publicPlayerState: Serde[PublicPlayerState] = Serde.of(pps => List(integer.serialize(pps.ticketCount), integer.serialize(pps.cardCount), listOfRoute.serialize(pps.routes)).mkString(";"),
    s => {
      val Array(ticketCount, cardCount, routes) = s.split(Pattern.quote(";"), -1)
      PublicPlayerState(integer.deserialize(ticketCount), integer.deserialize(cardCount), listOfRoute.deserialize(routes))
    })

  val playerState: Serde[PlayerState] = Serde.of(ps => List(bagOfTicket.serialize(ps.tickets), bagOfCard.serialize(ps.cards), listOfRoute.serialize(ps.routes)).mkString(";"),
    s => {
      val Array(tickets, cards, routes) = s.split(Pattern.quote(";"), -1)
      PlayerState(bagOfTicket.deserialize(tickets), bagOfCard.deserialize(cards), listOfRoute.deserialize(routes))
    })

  val publicGameState: Serde[PublicGameState] = Serde.of(gs => List(integer.serialize(gs.ticketsCount),
    publicCardState.serialize(gs.cardState), playerIdNoNull.serialize(gs.currentPlayerId), publicPlayerState.serialize(gs.playerState(PlayerId.PLAYER_1).get), publicPlayerState.serialize(gs.playerState(PlayerId.PLAYER_2).get),
    playerId.serialize(gs.lastPlayer)).mkString(":"),
    s => {
      val Array(ticketsCount, cardState, currentPlayerId, pps1, pps2, lastPlayer) = s.split(Pattern.quote(":"), -1)
      PublicGameState(integer.deserialize(ticketsCount), publicCardState.deserialize(cardState), playerIdNoNull.deserialize(currentPlayerId),
        Map(PlayerId.PLAYER_1 -> publicPlayerState.deserialize(pps1), PlayerId.PLAYER_2 -> publicPlayerState.deserialize(pps2)), playerId.deserialize(lastPlayer))
    })
