package ch.coachdave.tchutchu.game

import ch.coachdave.tchutchu.SortedBag
import ch.coachdave.tchutchu.game.Player.TurnKind
import ch.coachdave.tchutchu.gui.Info

trait Player:

  def initPlayers(ownId: PlayerId, playerNames: Map[PlayerId, String]): Unit

  def receiveInfo(info: String): Unit

  def updateState(newState: PublicGameState, ownState: PlayerState): Unit

  def setInitialTicketChoice(tickets: SortedBag[Ticket]): Unit

  def chooseInitialTickets: Unit

  def nextTurn: Unit

  def chooseTickets(options: SortedBag[Ticket]): Unit

  def drawSlot: Unit

  def claimedRoute: Unit

  def initialClaimCards: Unit

  def chooseAdditionalCards(options: List[SortedBag[Card]]): Unit
  
  def getInfo: Info
  
  def sendChatMessage(msg: String): Unit

  def congratulate(winner: Option[PlayerId]): Unit



object Player:

  enum TurnKind extends Enum[TurnKind]:
    case DRAW_TICKETS, DRAW_CARDS, CLAIM_ROUTE

  val ALL: Vector[TurnKind] = TurnKind.values.toVector
