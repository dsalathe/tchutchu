package ch.coachdave.tchutchu.net

import ch.coachdave.tchutchu.SortedBag
import ch.coachdave.tchutchu.game.*
import ch.coachdave.tchutchu.gui.Info

import java.io.{BufferedReader, BufferedWriter, InputStreamReader, OutputStreamWriter}
import java.net.Socket
import java.nio.charset.StandardCharsets.US_ASCII
import Serdes._

class RemotePlayerProxyRawTCP(socket: Socket) extends Player: //TODO try to build it with SIMPmessagetemplate + sessionId for SENDING. Still use socket for RECEIVING (because we need to block execution)

  private val r: BufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream, US_ASCII))
  private val w: BufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream, US_ASCII))

  private def sendMessage(id: MessageId, args: String*): Unit =
    w.write(id.toString + " ")
    w.write(args.mkString(" "))
    w.write("\n")
    w.flush()

  private def receiveMessage(): String = r.readLine()

  override def initPlayers(ownId: PlayerId, playerNames: Map[PlayerId, String]): Unit =
    sendMessage(MessageId.INIT_PLAYERS, playerIdNoNull.serialize(ownId), listOfString.serialize(List(playerNames(PlayerId.PLAYER_1), playerNames(PlayerId.PLAYER_2))))

  override def receiveInfo(info: String): Unit = sendMessage(MessageId.RECEIVE_INFO, string.serialize(info))

  override def updateState(newState: PublicGameState, ownState: PlayerState): Unit = sendMessage(MessageId.UPDATE_STATE, publicGameState.serialize(newState),
    playerState.serialize(ownState))

  override def setInitialTicketChoice(tickets: SortedBag[Ticket]): Unit = sendMessage(MessageId.SET_INITIAL_TICKETS, bagOfTicket.serialize(tickets))

  override def chooseInitialTickets: Unit =//SortedBag[Ticket] =
    sendMessage(MessageId.CHOOSE_INITIAL_TICKETS)
    //bagOfTicket.deserialize(receiveMessage())

  override def nextTurn: Unit = //Player.TurnKind =
    sendMessage(MessageId.NEXT_TURN)
    //turnKind.deserialize(receiveMessage())

  override def chooseTickets(options: SortedBag[Ticket]): Unit = //SortedBag[Ticket] =
    sendMessage(MessageId.CHOOSE_TICKETS, bagOfTicket.serialize(options))
    //bagOfTicket.deserialize(receiveMessage())

  override def drawSlot: Unit =//Int =
    sendMessage(MessageId.DRAW_SLOT)
    //integer.deserialize(receiveMessage())

  override def claimedRoute: Unit =//Route =
    sendMessage(MessageId.ROUTE)
    //route.deserialize(receiveMessage())

  override def initialClaimCards: Unit =//SortedBag[Card] =
    sendMessage(MessageId.CARDS)
    //bagOfCard.deserialize(receiveMessage())

  override def chooseAdditionalCards(options: List[SortedBag[Card]]): Unit =//SortedBag[Card] =
    sendMessage(MessageId.CHOOSE_ADDITIONAL_CARDS, listOfBagOfCard.serialize(options))
    //bagOfCard.deserialize(receiveMessage())
    
  override def getInfo: Info = new Info("truc")
