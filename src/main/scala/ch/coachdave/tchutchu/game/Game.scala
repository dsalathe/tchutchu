package ch.coachdave.tchutchu.game

import ch.coachdave.tchutchu.SortedBag
import ch.coachdave.tchutchu.gui.Info
import Player.TurnKind.*
import Route.Level.*
import ch.coachdave.tchutchu.net.Serdes._

import collection.JavaConverters.*
import scala.annotation.tailrec

object Game:
  
  def initGame(player: Player, playerId: PlayerId, tickets : SortedBag[Ticket]): GameState = GameState.initial(tickets, playerId, player)

  def joiningGame(gs: GameState, player: Player, playerId: PlayerId): GameState =
    val updatedGs : GameState = gs.withNewPlayer(playerId, player)
    if updatedGs.isGameFull then startGame(updatedGs) else updatedGs

  private def startGame(gs: GameState): GameState =
    val players = gs.playerMap
    players.foreach{ case (pId, p) => p.initPlayers(pId, players map{ case (pId, p) => pId -> p.getInfo.getPlayerName}) }
    broadcastInfo(players, players(gs.currentPlayerId).getInfo.willPlayFirst())
    val (distributed, initialTicketsDistributedState) = gs.distributeTickets(players.size, Constants.INITIAL_TICKETS_COUNT)
    players.values.zip(distributed).foreach{ case (p, t) => p.setInitialTicketChoice(t)}
    players.values.foreach(p => {p.chooseInitialTickets})
    broadcastNewStates(players, initialTicketsDistributedState)
    initialTicketsDistributedState
  
  def updateGame(gs : GameState, currentPlayerId: PlayerId, action: String, data: String): GameState =
    require(action == gs.getNextExpectedAction, s"received action was $action but ${gs.getNextExpectedAction} was expected")
    require(currentPlayerId == gs.currentPlayerId || action == "INITIAL_TICKETS_CHOSEN", s"with $action, received action from $currentPlayerId but expected ${gs.currentPlayerId}")
    require(!gs.isOver)
    if gs.lastTurnBegins then endPhase(gs) else playingPhase(gs, currentPlayerId, action, data)

  private def endPhase(gs: GameState): GameState =
    val players: Map[PlayerId, Player] = gs.playerMap
    val trails: Map[PlayerId, Trail] = PlayerId.ALL.map(pId => pId -> Trail.longest(gs.playerState(pId).get.routes)).toMap
    val maximum: Int = trails.values.maxBy(_.length).length
    val playersWithBonus : Map[PlayerId, Trail] = trails.filter { case (_, trail) => trail.length == maximum}
    playersWithBonus.foreach { case (pId, trail) => players(pId).getInfo.getsLongestTrailBonus(trail)}
    val points: List[Int] = PlayerId.ALL.map(pId => gs.playerState(pId).get.finalPoints + (if playersWithBonus.contains(pId) then
    {broadcastInfo(players, players(pId).getInfo.getsLongestTrailBonus(trails(pId)));Constants.LONGEST_TRAIL_BONUS_POINTS} else 0)).toList
    broadcastNewStates(players, gs)
    //2 players only. Need to readjust infos for more players
    if points.head > points.tail.head then
      broadcastInfo(players, players(PlayerId.PLAYER_1).getInfo.won(points.head, points.tail.head))

    else if points(1) > points.head then
      broadcastInfo(players, players(PlayerId.PLAYER_2).getInfo.won(points(1), points.head))

    else
      val playerNames: List[String] = players.values.toList.map(_.getInfo.getPlayerName)
      broadcastInfo(players, Info.draw(playerNames.asJava, points.head))

    gs



  private def playingPhase(gs: GameState, currentPlayerId: PlayerId, action: String, data: String): GameState =
    val players = gs.playerMap
    val updatedGameState = action match //TODO currentPlayerId needed?
      case "INITIAL_TICKETS_CHOSEN" => //TODO currently no check on chosen tickets! might fix it in same time as in ADDITIONAL_TICKETS_CHOSEN
        val updatedGs = gs.withInitiallyChosenTickets(currentPlayerId, bagOfTicket.deserialize(data))
        broadcastInfo(players, players(currentPlayerId).getInfo.keptTickets(updatedGs.playerState(currentPlayerId).get.ticketCount))
        if updatedGs.getNextExpectedAction == "PLAY_TURN" then broadcastInfo(players, players(currentPlayerId).getInfo.canPlay)
        updatedGs

      case "PLAY_TURN" =>
        val turnkind::remaining = data.split(" ").toList
        turnkind match
          case "FIRST_CARD" =>
            handleDrawCardAction(gs, nthCard = 1, integer.deserialize(remaining.head))

          case "CLAIM_ROUTE" => //TODO No check on data integrity!
            val claimedRoute::initialClaimCards::_: List[String] = remaining
            handleClaimRouteAction(gs, route.deserialize(claimedRoute), bagOfCard.deserialize(initialClaimCards))

          case "ASK_MORE_TICKETS" =>
            handleDrawTicketsAction(gs)

      case "DRAW_SECOND" =>
        handleDrawCardAction(gs, 2, integer.deserialize(data))

      case "ADDITIONAL_CARDS_CHOSEN" =>
        val claimedRoute = gs.getClaimedRoute.get
        val initialClaimCards = gs.getInitialClaimCards.get
        val chosenAdditionalCards: SortedBag[Card] = bagOfCard.deserialize(data)
        if chosenAdditionalCards.isEmpty then
          broadcastInfo(players, gs.currentPlayer.getInfo.didNotClaimRoute(claimedRoute))
          gs.forNextTurn

        else
          broadcastInfo(gs.playerMap, gs.currentPlayer.getInfo.claimedRoute(claimedRoute, initialClaimCards))
          gs.withClaimedRoute(claimedRoute, initialClaimCards.union(chosenAdditionalCards))

      case "ADDITIONAL_TICKETS_CHOSEN" =>
        val chosenTickets: SortedBag[Ticket] = bagOfTicket.deserialize(data)
        val drawnTickets = chosenTickets// gs.getDrawnTickets.get
        broadcastInfo(players, gs.currentPlayer.getInfo.keptTickets(chosenTickets.size))
        gs.withChosenAdditionalTickets(drawnTickets, chosenTickets)

      case _ =>
        println("Not implemented")
        gs

    broadcastNewStates(players, updatedGameState)
    updatedGameState

  private def handleDrawCardAction(gs: GameState, nthCard: Integer, slot: Integer): GameState =
    val players = gs.playerMap
    require(slot == Constants.DECK_SLOT || Constants.FACE_UP_CARD_SLOTS.contains(slot))
    val recreatedState: GameState = gs.withCardsDeckRecreatedIfNeeded
    if slot == Constants.DECK_SLOT then
      val withDrawnState: GameState = recreatedState.withBlindlyDrawnCard(nthCard)
      broadcastInfo(players, gs.currentPlayer.getInfo.drewBlindCard)
      withDrawnState
    else
      val drawnFaceUp: Card = recreatedState.cardState.faceUpCards(slot)
      val withDrawnState: GameState = recreatedState.withDrawnFaceUpCard(slot, nthCard)
      broadcastInfo(players, gs.currentPlayer.getInfo.drewVisibleCard(drawnFaceUp))
      withDrawnState

  private def handleClaimRouteAction(gs: GameState, claimedRoute: Route, initialClaimCards: SortedBag[Card]): GameState =
    val players = gs.playerMap
    val player = gs.currentPlayer

    def claimRoute(state: GameState): GameState =
      val updatedGs: GameState = state.withClaimedRoute(claimedRoute, initialClaimCards)
      broadcastInfo(players, player.getInfo.claimedRoute(claimedRoute, initialClaimCards))
      updatedGs

    def handleTunnel(state: GameState, claimedRoute: Route): GameState =
      @tailrec
      def drawNCards(state: GameState, drawn: List[Card], remaining: Int): (SortedBag[Card], GameState) =
        if remaining == 0 then
          (SortedBag.of(drawn.asJava), state)
        else
          val recreatedState = state.withCardsDeckRecreatedIfNeeded
          drawNCards(recreatedState.withoutTopCard, recreatedState.topCard::drawn, remaining-1)

      broadcastInfo(players, gs.currentPlayer.getInfo.attemptsTunnelClaim(claimedRoute, initialClaimCards))
      val (drawnCards, drawnState) = drawNCards(state, Nil, Constants.ADDITIONAL_TUNNEL_CARDS)
      val backToDiscardState = drawnState.withMoreDiscardedCards(drawnCards)
      val additionalCardCount: Int = claimedRoute.additionalClaimCardsCount(initialClaimCards, drawnCards)
      if additionalCardCount == 0 then
        claimRoute(backToDiscardState)

      else
        val options: List[SortedBag[Card]] = backToDiscardState.currentPlayerState.possibleAdditionalCards(additionalCardCount, initialClaimCards, drawnCards)
        if options.isEmpty then
          broadcastInfo(players, player.getInfo.didNotClaimRoute(claimedRoute))
          backToDiscardState.forNextTurn
        else
          player.chooseAdditionalCards(options)
          backToDiscardState.waitingForAdditionalCards(claimedRoute, initialClaimCards)

    claimedRoute.level match
      case UNDERGROUND => handleTunnel(gs, claimedRoute)
      case OVERGROUND => claimRoute(gs)

  private def handleDrawTicketsAction(gs: GameState): GameState =
    val players = gs.playerMap
    val player = gs.currentPlayer
    val drawnTickets: SortedBag[Ticket] = gs.topTickets(Constants.IN_GAME_TICKETS_COUNT)
    broadcastInfo(players, player.getInfo.drewTickets(Constants.IN_GAME_TICKETS_COUNT))
    player.chooseTickets(drawnTickets)
    gs.waitingForChosenTickets


  //  @tailrec
  //  private def playingPhase(state: GameState, players: Map[PlayerId, Player], infos: Map[PlayerId, Info]): GameState =
  //    broadcastInfo(players, infos(state.currentPlayerId).canPlay)
  //    broadcastNewStates(players, state)
  //    val player: Player = players(state.currentPlayerId)
  //    val nextTurn = player.nextTurn
  //    val handlingFunction: (GameState, Player, Map[PlayerId, Player], Map[PlayerId, Info]) => GameState = nextTurn match
  //      case DRAW_CARDS => handleDrawCardsAction
  //      case CLAIM_ROUTE => handleClaimRouteAction
  //      case DRAW_TICKETS => handleDrawTicketsAction
  //
  //    val updatedGameState: GameState = handlingFunction(state, player, players, infos)
  //    if updatedGameState.lastTurnBegins then
  //      broadcastInfo(players, infos(updatedGameState.currentPlayerId).lastTurnBegins(updatedGameState.currentPlayerState.carCount))
  //
  //    updatedGameState.lastPlayer match
  //      case Some(pId) if pId == updatedGameState.currentPlayerId => updatedGameState
  //      case _ => playingPhase(updatedGameState.forNextTurn, players, infos)
  //
  //
  //  private def handleDrawCardsAction(state: GameState, player: Player, players: Map[PlayerId, Player], info: Map[PlayerId, Info]): GameState =
  //    @tailrec
  //    def helper(state: GameState, remaining: Int): GameState =
  //      if remaining == 0 then
  //        state
  //      else
  //        broadcastNewStates(players, state)
  //        val slot: Int = player.drawSlot
  //        require(slot == Constants.DECK_SLOT || Constants.FACE_UP_CARD_SLOTS.contains(slot))
  //        val recreatedState: GameState = state.withCardsDeckRecreatedIfNeeded
  //        if slot == Constants.DECK_SLOT then
  //          val withDrawnState: GameState = recreatedState.withBlindlyDrawnCard
  //          broadcastInfo(players, info(withDrawnState.currentPlayerId).drewBlindCard)
  //          helper(withDrawnState, remaining-1)
  //        else
  //          val drawnFaceUp: Card = recreatedState.cardState.faceUpCards(slot)
  //          val withDrawnState: GameState = recreatedState.withDrawnFaceUpCard(slot)
  //          broadcastInfo(players, info(withDrawnState.currentPlayerId).drewVisibleCard(drawnFaceUp))
  //          helper(withDrawnState, remaining-1)
  //
  //    helper(state, 2)
  //
  //
  //  private def handleClaimRouteAction(state: GameState, player: Player, players: Map[PlayerId, Player], info: Map[PlayerId, Info]): GameState =
  //    def claimRoute(state: GameState, claimedRoute: Route, totalChosenCards: SortedBag[Card]): GameState =
  //      val newState: GameState = state.withClaimedRoute(claimedRoute, totalChosenCards)
  //      broadcastInfo(players, info(newState.currentPlayerId).claimedRoute(claimedRoute, totalChosenCards))
  //      newState
  //
  //    def handleTunnel(state: GameState, claimedRoute: Route): GameState =
  //      @tailrec
  //      def drawNCards(state: GameState, drawn: List[Card], remaining: Int): (SortedBag[Card], GameState) =
  //        if remaining == 0 then
  //          (SortedBag.of(drawn.asJava), state)
  //        else
  //          val recreatedState = state.withCardsDeckRecreatedIfNeeded
  //          drawNCards(recreatedState.withoutTopCard, recreatedState.topCard::drawn, remaining-1)
  //
  //      val initialClaimCards = player.initialClaimCards
  //      broadcastInfo(players, info(state.currentPlayerId).attemptsTunnelClaim(claimedRoute, initialClaimCards))
  //      val (drawnCards, drawnState) = drawNCards(state, Nil, Constants.ADDITIONAL_TUNNEL_CARDS)
  //      val backToDiscardState = drawnState.withMoreDiscardedCards(drawnCards)
  //      val additionalCardCount: Int = claimedRoute.additionalClaimCardsCount(initialClaimCards, drawnCards)
  //      if additionalCardCount == 0 then
  //        claimRoute(backToDiscardState, claimedRoute, initialClaimCards)
  //
  //      else
  //        val options: List[SortedBag[Card]] = backToDiscardState.currentPlayerState.possibleAdditionalCards(additionalCardCount, initialClaimCards, drawnCards)
  //        if options.isEmpty then
  //          broadcastInfo(players, info(backToDiscardState.currentPlayerId).didNotClaimRoute(claimedRoute))
  //          backToDiscardState
  //        else
  //          val chosenAdditionalCards: SortedBag[Card] = player.chooseAdditionalCards(options)
  //          if chosenAdditionalCards.isEmpty then
  //            broadcastInfo(players, info(backToDiscardState.currentPlayerId).didNotClaimRoute(claimedRoute))
  //            backToDiscardState
  //
  //          else
  //            claimRoute(backToDiscardState, claimedRoute, initialClaimCards.union(chosenAdditionalCards))
  //
  //
  //    val claimedRoute: Route = player.claimedRoute // Future?
  //    claimedRoute.level match
  //      case UNDERGROUND => handleTunnel(state, claimedRoute)
  //      case OVERGROUND => claimRoute(state, claimedRoute, player.initialClaimCards)
  //
  //
  //  private def handleDrawTicketsAction(state: GameState, player: Player, players: Map[PlayerId, Player], info: Map[PlayerId, Info]): GameState =
  //    val drawnTickets: SortedBag[Ticket] = state.topTickets(Constants.IN_GAME_TICKETS_COUNT)
  //    broadcastInfo(players, info(state.currentPlayerId).drewTickets(Constants.IN_GAME_TICKETS_COUNT)) // using for players?
  //    broadcastNewStates(players, state)
  //    val chosenTickets: SortedBag[Ticket] = player.chooseTickets(drawnTickets)
  //    broadcastInfo(players, info(state.currentPlayerId).keptTickets(chosenTickets.size))
  //    state.withChosenAdditionalTickets(drawnTickets, chosenTickets)
  //
//  def play(players: Map[PlayerId, Player], playerNames: Map[PlayerId, String], tickets: SortedBag[Ticket]): Unit =
//    require(players.size == playerNames.size)
//    val infos: Map[PlayerId, Info] = (for (pId <- PlayerId.ALL) yield pId -> new Info(playerNames(pId))).toMap
//    val initialGameState: GameState = startPhase(players, playerNames, tickets, infos)
//    val lastGameState: GameState = playingPhase(initialGameState, players, infos)
//    endPhase(lastGameState, players, playerNames, infos)
//
//
//  private def startPhase(players: Map[PlayerId, Player], playerNames: Map[PlayerId, String], tickets: SortedBag[Ticket], infos: Map[PlayerId, Info]): GameState =
//    players.foreach{ case (pId, p) => p.initPlayers(pId, playerNames) }
//    val initialGameState: GameState = GameState.initial(tickets)
//    broadcastInfo(players, infos(initialGameState.currentPlayerId).willPlayFirst())
//    val (distributed, initialTicketsDistributedState) = initialGameState.distributeTickets(players.size, Constants.INITIAL_TICKETS_COUNT)
//    players.values.zip(distributed).foreach{ case (p, t) => p.setInitialTicketChoice(t)}
//    @tailrec
//    def evolveInitialState(playerIds: List[PlayerId], currentState: GameState): GameState = playerIds match
//      case pId::tail =>
//        broadcastNewStates(players, currentState)
//        val initialTickets = players(pId).chooseInitialTickets
//        require(initialTickets.size() >= 3)
//        evolveInitialState(tail, currentState.withInitiallyChosenTickets(pId, initialTickets))
//      case Nil => currentState
//
//    val evolvedInitialState = evolveInitialState(players.keys.toList, initialTicketsDistributedState)
//    infos.foreach{case (pId, info) => broadcastInfo(players, info.keptTickets(evolvedInitialState.playerState(pId).get.ticketCount))}
//    evolvedInitialState
//
  //  private def endPhase(state: GameState, players: Map[PlayerId, Player], playerNames: Map[PlayerId, String], infos: Map[PlayerId, Info]): Unit =
  //    val trails: Map[PlayerId, Trail] = PlayerId.ALL.map(pId => pId -> Trail.longest(state.playerState(pId).get.routes)).toMap
  //    val maximum: Int = trails.values.maxBy(_.length).length
  //    val playersWithBonus : Map[PlayerId, Trail] = trails.filter { case (_, trail) => trail.length == maximum}
  //    playersWithBonus.foreach { case (pId, trail) => infos(pId).getsLongestTrailBonus(trail)}
  //    val points: List[Int] = PlayerId.ALL.map(pId => state.playerState(pId).get.finalPoints + (if playersWithBonus.contains(pId) then
  //    {broadcastInfo(players, infos(pId).getsLongestTrailBonus(trails(pId)));Constants.LONGEST_TRAIL_BONUS_POINTS} else 0)).toList
  //    broadcastNewStates(players, state)
  //    // 2 players only. Need to readjust infos for more players
  //    if points.head > points.tail.head then
  //      broadcastInfo(players, infos(PlayerId.PLAYER_1).won(points.head, points.tail.head))
  //
  //    else if points(1) > points.head then
  //      broadcastInfo(players, infos(PlayerId.PLAYER_2).won(points(1), points.head))
  //
  //    else
  //      broadcastInfo(players, Info.draw(playerNames.values.toList.asJava, points.head))
//
//




  private def broadcastInfo(players: Map[PlayerId, Player], info: String): Unit = players.values.foreach(p => p.receiveInfo(info))
  private def broadcastNewStates(players: Map[PlayerId, Player], state: GameState): Unit = players.keys.foreach(pId => players.get(pId).map(p => p.updateState(state, state.playerState(pId).get)))
