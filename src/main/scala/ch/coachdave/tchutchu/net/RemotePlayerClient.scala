package ch.coachdave.tchutchu.net

import ch.coachdave.tchutchu.game.*

import java.io.{BufferedReader, BufferedWriter, InputStreamReader, OutputStreamWriter}
import java.net.Socket
import java.nio.charset.StandardCharsets.US_ASCII
import java.util.regex.Pattern
import scala.annotation.tailrec
import MessageId.*
import ch.coachdave.tchutchu.SortedBag

final class RemotePlayerClient(player: Player, name: String, port: Int):

  val socket: Socket = new Socket(name, port)

  val r: BufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream, US_ASCII))
  val w: BufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream, US_ASCII))

  private def sendMessage(msg: String): Unit =
    w.write(msg)
    w.write("\n")
    w.flush()

  @tailrec
  def run(): Unit =
    val msg = r.readLine()
    if msg == null then return else
      val Array(id, args*) = msg.split(Pattern.quote(" "))
//      MessageId.valueOf(id) match
//        case INIT_PLAYERS =>
//          val Seq(playerId, playerNames) = args
//          val p1::p2::Nil: List[String] = Serdes.listOfString.deserialize(playerNames)
//          player.initPlayers(Serdes.playerIdNoNull.deserialize(playerId), Map(PlayerId.PLAYER_1 -> p1, PlayerId.PLAYER_2 -> p2))
//
//        case RECEIVE_INFO =>
//          player.receiveInfo(Serdes.string.deserialize(args.head))
//
//        case UPDATE_STATE =>
//          val Seq(publicGameState, playerState) = args
//          player.updateState(Serdes.publicGameState.deserialize(publicGameState), Serdes.playerState.deserialize(playerState))
//
//        case SET_INITIAL_TICKETS =>
//          player.setInitialTicketChoice(Serdes.bagOfTicket.deserialize(args.head))
//
//        case CHOOSE_INITIAL_TICKETS =>
//          val chosenTickets: SortedBag[Ticket] = player.chooseInitialTickets
//          sendMessage(Serdes.bagOfTicket.serialize(chosenTickets))
//
//        case NEXT_TURN =>
//          val next = player.nextTurn
//          sendMessage(Serdes.turnKind.serialize(next))
//
//        case CHOOSE_TICKETS =>
//          val tickets: SortedBag[Ticket] = player.chooseTickets(Serdes.bagOfTicket.deserialize(args.head))
//          sendMessage(Serdes.bagOfTicket.serialize(tickets))
//
//        case DRAW_SLOT =>
//          val slot = player.drawSlot
//          sendMessage(Serdes.integer.serialize(slot))
//
//        case ROUTE =>
//          val route = player.claimedRoute
//          sendMessage(Serdes.route.serialize(route))
//
//        case CARDS =>
//          val cards = player.initialClaimCards
//          sendMessage(Serdes.bagOfCard.serialize(cards))
//
//        case CHOOSE_ADDITIONAL_CARDS =>
//          val additionalCards = player.chooseAdditionalCards(Serdes.listOfBagOfCard.deserialize(args.head))
//          sendMessage(Serdes.bagOfCard.serialize(additionalCards))

      run()
