package ch.coachdave.tchutchu.game

import ch.coachdave.tchutchu.game.GameState.MAX_PLAYERS
import ch.coachdave.tchutchu.{SortedBag, game}
import ch.coachdave.tchutchu.game.UserAction._

import collection.JavaConverters.*
import scala.util.Random.{nextInt, shuffle}

class GameState private(tickets: Deck[Ticket], cardState: CardState, currentPlayerId: PlayerId, players: Map[PlayerId, Player],
                        playerStates: Map[PlayerId, PlayerState], lastPlayer: Option[PlayerId], nextExpectedAction: UserAction,
                        claimedRoute: Option[Route] = None, initialClaimCards: Option[SortedBag[Card]] = None,
                        suggestedTickets: Option[Map[PlayerId, SortedBag[Ticket]]] = None)
  extends PublicGameState(tickets.size, cardState, currentPlayerId,  playerStates, lastPlayer):

  val totalCurrentSize: Int = cardState.deckSize + playerStates.map(_._2.cardCount).sum + 5 + cardState.discardsSize
  require(totalCurrentSize >= Constants.TOTAL_CARDS_COUNT - Constants.ADDITIONAL_TUNNEL_CARDS,
    s"total cards sum up to $totalCurrentSize, should be at least ${Constants.TOTAL_CARDS_COUNT - Constants.ADDITIONAL_TUNNEL_CARDS}")

  override def playerState(playerId: PlayerId): Option[PlayerState] = playerStates.get(playerId)
  override def currentPlayerState: PlayerState = playerStates(currentPlayerId)

  def currentPlayer: Player = players(currentPlayerId)
  def playerMap: Map[PlayerId, Player] = players
  def allPlayer: Iterable[Player] = players.values
  def getNextExpectedAction: UserAction = nextExpectedAction

  // Group 1: Ticket or Card related

  def topTickets(count: Int): SortedBag[Ticket] = tickets.topCardsSorted(count)
  def withoutTopTickets(count: Int): GameState =
    new GameState(tickets.withoutTopCards(count), cardState, currentPlayerId, players, playerStates, lastPlayer, nextExpectedAction)
  def topCard: Card = cardState.topDeckCard
  def withoutTopCard: GameState =
    new GameState(tickets, cardState.withoutTopDeckCard, currentPlayerId, players, playerStates, lastPlayer, nextExpectedAction)
  def withMoreDiscardedCards(discardCards: SortedBag[Card]): GameState =
    new GameState(tickets, cardState.withMoreDiscardedCards(discardCards), currentPlayerId, players, playerStates, lastPlayer, nextExpectedAction)
  def withCardsDeckRecreatedIfNeeded: GameState =
    if cardState.isDeckEmpty then new GameState(tickets, cardState.withDeckRecreatedFromDiscards, currentPlayerId, players, playerStates, lastPlayer, nextExpectedAction) else this

  def distributeTickets(nHeap: Int, sizeHeap: Int): (List[SortedBag[Ticket]], GameState) =
    require(nextExpectedAction == DISTRIBUTING_INITIAL_TICKETS)
    val (distributed, remaining): (List[SortedBag[Ticket]], Deck[Ticket]) = tickets.distribute(nHeap, sizeHeap)
    val suggestedTickets: Map[PlayerId, SortedBag[Ticket]] = (PlayerId.ALL zip distributed).toMap
    (distributed, new GameState(remaining, cardState, currentPlayerId, players,
      playerStates, lastPlayer, INITIAL_TICKETS_CHOSEN, suggestedTickets = Some(suggestedTickets)))

  // Group 2: re-actions

  private def withAction(f: PlayerState => PlayerState): Map[PlayerId, PlayerState] =
    withAction(f, currentPlayerId)

  private def withAction(f: PlayerState => PlayerState, playerId: PlayerId): Map[PlayerId, PlayerState] =
    playerStates.updated(playerId, playerState(playerId).map(f).get)


  def withInitiallyChosenTickets(playerId: PlayerId, chosenTickets: SortedBag[Ticket]): GameState =
    require(playerStates(playerId).tickets.size == 0, s"size of $playerId is ${playerStates(playerId).tickets.size}")
    require((suggestedTickets.get)(playerId).contains(chosenTickets), "I'm pretty sure I didn't give you those tickets!" )
    require(nextExpectedAction == INITIAL_TICKETS_CHOSEN)
    require(chosenTickets.size() >= 3)
    def isLastToChooseInitialTickets(id: PlayerId) = playerStates forall {case (pId, pState) => pId == id || pState.tickets.size > 0}
    val nextAction = if isLastToChooseInitialTickets(playerId) then PLAY_TURN else INITIAL_TICKETS_CHOSEN
    new GameState(tickets, cardState, currentPlayerId, players, withAction(p => p.withAddedTickets(chosenTickets), playerId), lastPlayer, nextAction, suggestedTickets=suggestedTickets)

  def withChosenAdditionalTickets(chosenTickets: SortedBag[Ticket]): GameState =
    require(nextExpectedAction == ADDITIONAL_TICKETS_CHOSEN)
    val drawnTickets: SortedBag[Ticket] = (suggestedTickets.get)(currentPlayerId)
    require(drawnTickets.contains(chosenTickets), "I'm pretty sure I didn't give you those tickets!")
    new GameState(tickets.withoutTopCards(drawnTickets.size), cardState, 
      currentPlayerId, players, withAction(p => p.withAddedTickets(chosenTickets)), lastPlayer, PLAY_TURN).forNextTurn

  def withDrawnFaceUpCard(slot: Int, nthCard: Integer): GameState =
    require((nthCard == 1 && nextExpectedAction == PLAY_TURN) || (nthCard == 2 && nextExpectedAction == DRAW_SECOND))
    val nextAction = if nthCard == 1 then DRAW_SECOND else PLAY_TURN
    new GameState(tickets, cardState.withDrawnFaceUpCard(slot), currentPlayerId, players, withAction(p => p.withAddedCard(cardState.faceUpCards(slot))), lastPlayer, nextAction).forNextTurn

  def withBlindlyDrawnCard(nthCard: Integer): GameState =
    require((nthCard == 1 && nextExpectedAction == PLAY_TURN) || (nthCard == 2 && nextExpectedAction == DRAW_SECOND))
    val nextAction = if nthCard == 1 then DRAW_SECOND else PLAY_TURN
    new GameState(tickets, cardState.withoutTopDeckCard, currentPlayerId, players, withAction(p => p.withAddedCard(cardState.topDeckCard)), lastPlayer, nextAction).forNextTurn

  def withClaimedRoute(route: Route, cards: SortedBag[Card]): GameState =
    require(playerStates.values.forall(p => !(p.routes contains route)), s"$route is already taken!")
    require(playerStates(currentPlayerId).canClaimRoute(route), s"$currentPlayerId is not authorized to claim route $route")
    require(nextExpectedAction == PLAY_TURN || nextExpectedAction == ADDITIONAL_CARDS_CHOSEN)
    require(route.possibleClaimCards.exists(cards.contains), s"Given cards $cards are not valid to take route $route")
    new GameState(tickets, cardState.withMoreDiscardedCards(cards), currentPlayerId, players,
      withAction(p => p.withClaimedRoute(route, cards)), lastPlayer, PLAY_TURN).forNextTurn
  

  // Group 3: Handling last turn

  def lastTurnBegins: Boolean = lastPlayer.isEmpty && playerStates(currentPlayerId).carCount <= 2

  def isOver: Boolean = lastPlayer.isDefined && lastPlayer.get == currentPlayerId
  
  def waitingForAdditionalCards(claimedRoute: Route, initialClaimCards: SortedBag[Card]): GameState =
    new GameState(tickets, cardState, currentPlayerId, players, playerStates, lastPlayer,
      ADDITIONAL_CARDS_CHOSEN, claimedRoute = Some(claimedRoute), initialClaimCards = Some(initialClaimCards))
    
  def waitingForChosenTickets(drawnTickets: SortedBag[Ticket]): GameState =
    new GameState(tickets, cardState, currentPlayerId, players, playerStates, lastPlayer,
      ADDITIONAL_TICKETS_CHOSEN, suggestedTickets=Some(Map(currentPlayerId->drawnTickets)))
    
    
  def getClaimedRoute: Option[Route] = claimedRoute
  def getInitialClaimCards: Option[SortedBag[Card]] = initialClaimCards

  // Group 4: Handling new players

  def isGameFull: Boolean = players.size >= MAX_PLAYERS

  def isGameFullIfOneAdded: Boolean = players.size + 1 >= MAX_PLAYERS

  def withNewPlayer(playerId: PlayerId, player: Player): GameState =
    require(!isGameFull)
    require(nextExpectedAction == JOINING_GAME)
    val nextAction = if isGameFullIfOneAdded then DISTRIBUTING_INITIAL_TICKETS else JOINING_GAME
    new GameState(tickets, cardState, currentPlayerId, players + (playerId -> player), playerStates, lastPlayer, nextAction)

  def withUpdatedPlayer(playerId: PlayerId, channelId: String): GameState =
    new GameState(tickets, cardState, currentPlayerId, players.updated(playerId, players(playerId).withNewChannel(channelId)),
      playerStates, lastPlayer, nextExpectedAction, claimedRoute, initialClaimCards, suggestedTickets)

  def forNextTurn: GameState =
    if nextExpectedAction == PLAY_TURN || nextExpectedAction == ADDITIONAL_CARDS_CHOSEN then
      new GameState(tickets, cardState, currentPlayerId.next, players, playerStates, if lastTurnBegins then Some(currentPlayerId) else lastPlayer, nextExpectedAction)
    else
      this

  def passTurn: GameState =
    new GameState(tickets, cardState, currentPlayerId.next, players, playerStates, if lastTurnBegins then Some(currentPlayerId) else lastPlayer, PLAY_TURN)


object GameState:

  val MAX_PLAYERS = 2

  def initial(tickets: SortedBag[Ticket], playerId: PlayerId, player: Player): GameState =
    val shuffledCards: Deck[Card] = Deck.of(Constants.ALL_CARDS)
    val (distributed, remaining) = shuffledCards.distribute(PlayerId.COUNT, Constants.INITIAL_CARDS_COUNT)
    val playerState: Map[PlayerId, PlayerState] = PlayerId.ALL.zip(distributed).map { case (pId, cards) => pId -> PlayerState.initial(cards)}.toMap
    new GameState(Deck.of(tickets), CardState.of(remaining), PlayerId.ALL(nextInt(PlayerId.COUNT)), Map(playerId -> player), playerState, None, JOINING_GAME)
