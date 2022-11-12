package ch.coachdave.tchutchu.game

import ch.coachdave.tchutchu.SortedBag
import ch.coachdave.tchutchu.game.Player.TurnKind
import ch.coachdave.tchutchu.gui.Info

trait Player:

  def initPlayers(ownId: PlayerId, playerNames: Map[PlayerId, String]): Unit

  def receiveInfo(info: String): Unit

  def updateState(newState: PublicGameState, ownState: PlayerState): Unit

  def setInitialTicketChoice(tickets: SortedBag[Ticket]): Unit

  def chooseInitialTickets: Unit // SortedBag[Ticket]

  def nextTurn: Unit //TurnKind

  def chooseTickets(options: SortedBag[Ticket]): Unit // SortedBag[Ticket]

  def drawSlot: Unit //Int

  def claimedRoute: Unit //Route

  def initialClaimCards: Unit //SortedBag[Card]

  def chooseAdditionalCards(options: List[SortedBag[Card]]): Unit //SortedBag[Card]
  
  def getInfo: Info



object Player:

  enum TurnKind extends Enum[TurnKind]:
    case DRAW_TICKETS, DRAW_CARDS, CLAIM_ROUTE

  val ALL: Vector[TurnKind] = TurnKind.values.toVector
