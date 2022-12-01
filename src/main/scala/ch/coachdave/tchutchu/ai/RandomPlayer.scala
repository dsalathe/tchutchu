package ch.coachdave.tchutchu.ai

import ch.coachdave.tchutchu.SortedBag
import ch.coachdave.tchutchu.game.Player.TurnKind
import ch.coachdave.tchutchu.game.{Card, ChMap, Player, PlayerId, PlayerState, PublicGameState, Route, Ticket}
import ch.coachdave.tchutchu.gui.Info

import scala.collection.MapView
import scala.collection.mutable.Queue
import scala.util.Random.{nextInt, shuffle}
import collection.JavaConverters.*
import scala.annotation.tailrec

class RandomPlayer extends Player:
  val CALLS_LIMIT = 10_000
  val MIN_CARD_COUNT = 16
  val DRAW_TICKETS_ODDS = 15
  val ABANDON_TUNNEL_ODDS = 10
  val DRAW_ALL_TICKETS_TURN = 30

  var calls: Queue[PlayerMethod] = Queue()
  var allTurns: Queue[TurnKind] = Queue()
  var allInfos: Queue[String] = Queue()
  var allGameStates: Queue[PublicGameState] = Queue()
  var allOwnStates: Queue[PlayerState] = Queue()
  var allTicketsSeen: Queue[SortedBag[Ticket]] = Queue()

  var ownId: PlayerId = null
  var playerNames: Map[PlayerId, String] = null
  var routeToClaim: Route = null
  var iClaimCards: SortedBag[Card] = null
  val allRoutes: Seq[Route] = ChMap.routes

  def registerCall(key: PlayerMethod): Unit =
    calls.enqueue(key)
    if calls.size >= CALLS_LIMIT then throw new TooManyCallsError

  def callSummary(): MapView[PlayerMethod, Int] =
    calls.groupBy(identity).view.mapValues(_.size)

  def gameState(): PublicGameState = allGameStates.last

  def ownState(): PlayerState = allOwnStates.last

  def ownName(): String =
    if ownId == null || playerNames == null then
      "<anonyme>"
    else
      playerNames.getOrElse(ownId, "<anonyme>")

  override def initPlayers(ownId: PlayerId, playerNames: Map[PlayerId, String]): Unit =
    registerCall(PlayerMethod.INIT_PLAYERS)
    this.ownId = ownId
    this.playerNames = playerNames

  override def receiveInfo(info: String): Unit =
    registerCall(PlayerMethod.RECEIVE_INFO)
    allInfos.enqueue(info)

  override def updateState(newState: PublicGameState, ownState: PlayerState): Unit =
    registerCall(PlayerMethod.UPDATE_STATE)
    allGameStates.enqueue(newState)
    allOwnStates.enqueue(ownState)

  override def setInitialTicketChoice(tickets: SortedBag[Ticket]): Unit =
    registerCall(PlayerMethod.SET_INITIAL_TICKET_CHOICE)
    allTicketsSeen.enqueue(tickets)

  override def chooseInitialTickets: Unit =//SortedBag[Ticket] =
    registerCall(PlayerMethod.CHOOSE_INITIAL_TICKETS)
    //allTicketsSeen.front

  override def nextTurn: Unit = //TurnKind =
    registerCall(PlayerMethod.NEXT_TURN)
    val turn: TurnKind = TurnKind.DRAW_CARDS// doNextTurn()
    allTurns.enqueue(turn)
    //turn

  def doNextTurn(): Unit = println("Nothing for now") //TurnKind =
//    val gs = gameState()
//    if gs.canDrawTickets && (allTurns.size >= DRAW_ALL_TICKETS_TURN - 10 || (nextInt(DRAW_TICKETS_ODDS) == 0)) then
//      TurnKind.DRAW_TICKETS
//    else
//      val os = ownState()
//      val claimedRoutes = gs.claimedRoutes
//      val claimableRoutes = allRoutes.filterNot(claimedRoutes.contains).filter(os.canClaimRoute)

//      if claimableRoutes.isEmpty || os.cardCount < MIN_CARD_COUNT then
//        TurnKind.DRAW_CARDS
//
//      else
//        @tailrec
//        def favorsTunnel(route: Route, remaining: Int): Route =
//          if remaining == 0 || route.level == Route.Level.UNDERGROUND then route else favorsTunnel(claimableRoutes(nextInt(claimableRoutes.size)), remaining-1)
//
//        val route = favorsTunnel(claimableRoutes(nextInt(claimableRoutes.size)), 3)
//        val cards = os.possibleClaimCards(route)
//        routeToClaim = route
//        iClaimCards = if cards.isEmpty then null else cards.head
//        TurnKind.CLAIM_ROUTE

  override def chooseTickets(options: SortedBag[Ticket]): Unit =//SortedBag[Ticket] =
    registerCall(PlayerMethod.CHOOSE_TICKETS)
    allTicketsSeen.enqueue(options)
//    val shuffledOptions = shuffle(options.toList.asScala)
//    val kept: Int = 1 + nextInt(options.size())
//    SortedBag.of(shuffledOptions.take(kept).asJava)

  override def drawSlot: Unit = //Int =
    registerCall(PlayerMethod.DRAW_SLOT)
    //nextInt(6) - 1

  override def claimedRoute: Unit =//Route =
    registerCall(PlayerMethod.CLAIMED_ROUTE)
    //routeToClaim

  override def initialClaimCards: Unit =//SortedBag[Card] =
    registerCall(PlayerMethod.INITIAL_CLAIM_CARD)
    //iClaimCards

  override def getInfo: Info = new Info("Robot")

  override def sendChatMessage(msg: String): Unit = ???

  override def congratulate(): Unit = ???

  override def chooseAdditionalCards(options: List[SortedBag[Card]]): Unit =//SortedBag[Card] =
    registerCall(PlayerMethod.CHOOSE_ADDITIONAL_CARDS)
    //if nextInt(ABANDON_TUNNEL_ODDS) == 0 then SortedBag.of() else options(nextInt(options.size))


class TooManyCallsError extends Error

enum PlayerMethod:
  case INIT_PLAYERS, RECEIVE_INFO, UPDATE_STATE, SET_INITIAL_TICKET_CHOICE, CHOOSE_INITIAL_TICKETS, NEXT_TURN, CHOOSE_TICKETS, DRAW_SLOT, CLAIMED_ROUTE, INITIAL_CLAIM_CARD, CHOOSE_ADDITIONAL_CARDS
