package ch.coachdave.tchutchu.controller

import ch.coachdave.tchutchu.ai.RandomPlayer
import ch.coachdave.tchutchu.SortedBag
import ch.coachdave.tchutchu.game.{ChMap, Game, GameState, Player, PlayerId, Ticket}
import ch.coachdave.tchutchu.model.{ClientNotification, MetaAction, TchuMessage}
import ch.coachdave.tchutchu.net.{MessageId, RemotePlayerProxyWS}
import ch.coachdave.tchutchu.gui.Info
import ch.coachdave.tchutchu.model.MetaAction.*

import collection.JavaConverters.*
import org.springframework.beans.factory.annotation.Autowired

import java.security.Principal
import org.springframework.messaging.handler.annotation.{Header, MessageMapping, SendTo}
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.{SendToUser, SubscribeMapping}
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.function.ServerRequest.Headers
import org.springframework.web.util.HtmlUtils

import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue
import scala.annotation.tailrec
import scala.collection.mutable

@Controller
class TchuTchuController {

  @Autowired
  private val messagingTemplate : SimpMessagingTemplate = null


  private val userIdToGameId = mutable.Map[String, String]() //TODO for memory mgmt: easy first solution might be using guava cache with timeout
  private val userIdToPlayerId = mutable.Map[String, PlayerId]()
  private val gameIdToGameState = mutable.Map[String, GameState]()
  private val queue = new ConcurrentLinkedQueue[String]()
  private val shortNameGameToGameId = mutable.Map[String, String]()


  @MessageMapping(Array("/tchu"))
  def handleClientCommand(message: TchuMessage, principal: Principal): Unit =
    val userId = principal.getName
    println(userId)
    println(message)
    message.metaAction match
      case INIT_GAME =>
        require(!(userIdToGameId.keySet contains userId)) //TODO IllegalArgument controller advice?
        val playerName::shortName::_ : List[String] = message.data.split(" ").toList
        val gameId: String = initGame(playerName, principal, userId)
        val confirmedShortName = determineShortName(shortName)
        shortNameGameToGameId.put(confirmedShortName, gameId)
        val okPrefix = if confirmedShortName == shortName then "ok" else "nok"
        messagingTemplate.convertAndSendToUser(principal.getName, "/queue/tchu-events", ClientNotification(MessageId.GAME_ID.toString, s"$okPrefix $confirmedShortName"))

      case JOIN_SPECIFIC_GAME =>
        require(!(userIdToGameId.keySet contains userId))
        val playerName::shortName::Nil : List[String] = message.data.split(" ").toList
        val playerId: PlayerId = PlayerId.PLAYER_2
        joinGame(principal, userId, shortNameGameToGameId(shortName), playerName, playerId)
        shortNameGameToGameId -= shortName
        //TODO messaging tells player blabla joined the game?

      case JOIN_ANY_GAME =>
        if queue.isEmpty then
          queue.add(initGame(message.data, principal, userId))
        else
          val gameId = queue.remove()
          joinGame(principal, userId, gameId, message.data, PlayerId.PLAYER_2)


      case PLAY =>//TODO add FORFEIT action here?
        val gameId = userIdToGameId(userId)
        val newGameState = Game.updateGame(gameIdToGameState(gameId), userIdToPlayerId(userId), message.playAction, message.data)

        if newGameState.isOver then {
          gameIdToGameState -= gameId
          newGameState.allPlayer foreach {
            case p : RemotePlayerProxyWS =>
              userIdToGameId -= p.username
              userIdToPlayerId -= p.username
          }
        } else {
          gameIdToGameState(gameId) = newGameState
        }


      case CHAT =>
        println("CHAT case")
        val gameId = userIdToGameId.get(userId)
        gameId match
          case Some(gId) =>
            val playerName = gameIdToGameState(gId).playerMap(userIdToPlayerId(userId)).getInfo.getPlayerName
            gameIdToGameState(gId).playerMap foreach ( _._2.sendChatMessage(s"$playerName ${message.data}"))
          case None =>
            messagingTemplate.convertAndSend("/topic/tchu-events",
              ClientNotification(MessageId.CHAT.toString,  s"User-${userId.substring(userId.length - 3)} ${message.data}"))

      case SAVE =>
        ???

      case LOAD =>
        ???


  private def joinGame(principal: Principal, userId: String, gameId: String, playerName: String, playerId: PlayerId): Unit = {
    val completedInitialGameState: GameState = Game.joiningGame(gameIdToGameState(gameId),
      RemotePlayerProxyWS(messagingTemplate, principal.getName, new Info(playerName)), playerId)
    gameIdToGameState(gameId) = completedInitialGameState
    userIdToGameId(userId) = gameId
    userIdToPlayerId(userId) = playerId
  }

  private def initGame(playerName: String, principal: Principal, userId: String) = {
    val tickets: SortedBag[Ticket] = SortedBag.of(ChMap.tickets.asJava)
    val playerId: PlayerId = PlayerId.PLAYER_1
    val initialGs: GameState = Game.initGame(RemotePlayerProxyWS(messagingTemplate, username = principal.getName, info = new Info(playerName)), playerId, tickets)
    val gameId = generateGameId()
    gameIdToGameState(gameId) = initialGs
    userIdToGameId(userId) = gameId
    userIdToPlayerId(userId) = playerId
    gameId
  }

  @tailrec
  private def determineShortName(shortName: String): String =
    if shortNameGameToGameId contains shortName then
      determineShortName(shortName + generateGameId().substring(0, 1))
    else
      shortName

  def generateGameId(): String = UUID.randomUUID().toString

}
