package ch.coachdave.tchutchu.net

import ch.coachdave.tchutchu.SortedBag
import ch.coachdave.tchutchu.game.{Card, Player, PlayerId, PlayerState, PublicGameState, Ticket}
import ch.coachdave.tchutchu.gui.Info
import ch.coachdave.tchutchu.model.ClientNotification
import ch.coachdave.tchutchu.net.Serdes.{bagOfCard, bagOfTicket, listOfBagOfCard, listOfString, playerId, playerIdNoNull, playerState, publicGameState, string, integer}
import org.springframework.messaging.simp.SimpMessagingTemplate

object RemotePlayerProxyWS:
  def apply(messagingTemplate : SimpMessagingTemplate, username: String, info: Info): RemotePlayerProxyWS = new RemotePlayerProxyWS(messagingTemplate, username, info)

case class RemotePlayerProxyWS(messagingTemplate : SimpMessagingTemplate, username: String, info: Info) extends Player:

  private def sendMessage(id: MessageId, args: String*): Unit =
    messagingTemplate.convertAndSendToUser(username, "/queue/tchu-events", ClientNotification(id.toString, args.mkString(" ")))


  override def initPlayers(ownId: PlayerId, playerNames: Map[PlayerId, String]): Unit =
    sendMessage(MessageId.INIT_PLAYERS, playerIdNoNull.serialize(ownId), listOfString.serialize(List(playerNames(PlayerId.PLAYER_1), playerNames(PlayerId.PLAYER_2))))

  override def receiveInfo(info: String): Unit = sendMessage(MessageId.RECEIVE_INFO, string.serialize(info))

  override def updateState(newState: PublicGameState, ownState: PlayerState): Unit = sendMessage(MessageId.UPDATE_STATE, publicGameState.serialize(newState),
    playerState.serialize(ownState))

  override def setInitialTicketChoice(tickets: SortedBag[Ticket]): Unit = sendMessage(MessageId.SET_INITIAL_TICKETS,
    correctDoubledTickets(bagOfTicket.serialize(tickets)))

  override def chooseInitialTickets: Unit = //TODO challenge: still useful?
    sendMessage(MessageId.CHOOSE_INITIAL_TICKETS)

  override def nextTurn: Unit =
    sendMessage(MessageId.NEXT_TURN)

  override def chooseTickets(options: SortedBag[Ticket]): Unit =
    sendMessage(MessageId.CHOOSE_TICKETS, correctDoubledTickets(bagOfTicket.serialize(options)))

  override def drawSlot: Unit =
    sendMessage(MessageId.DRAW_SLOT)

  override def claimedRoute: Unit =
    sendMessage(MessageId.ROUTE)

  override def initialClaimCards: Unit =
    sendMessage(MessageId.CARDS)

  override def chooseAdditionalCards(options: List[SortedBag[Card]]): Unit =
    sendMessage(MessageId.CHOOSE_ADDITIONAL_CARDS, listOfBagOfCard.serialize(options))

  override def getInfo: Info = info

  override def getUserName: String = username

  override def sendChatMessage(msg: String): Unit = sendMessage(MessageId.CHAT,  msg)

  override def congratulate(winner: Option[PlayerId]): Unit = sendMessage(MessageId.CONGRATULATE, playerId.serialize(winner))

  override def withNewChannel(channelId: String): Player = RemotePlayerProxyWS(messagingTemplate, channelId, info)

  private def correctDoubledTickets(tickets: String): String =
    val indices: List[Int] = tickets.split(",").toList.map(integer.deserialize)
    indices.groupBy(i => i).values.flatMap(l => l.zipWithIndex map { case (e, i) => e + i }).mkString(",")




