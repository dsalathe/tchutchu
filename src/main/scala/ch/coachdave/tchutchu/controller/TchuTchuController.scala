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
import scala.collection.mutable

@Controller
class TchuTchuController {

  @Autowired
  private val messagingTemplate : SimpMessagingTemplate = null


  private val userIdToGameId = mutable.Map[String, String]()
  private val userIdToPlayerId = mutable.Map[String, PlayerId]()
  private val gameIdToGameState = mutable.Map[String, GameState]()


  @MessageMapping(Array("/tchu"))
  def handleClientCommand(message: TchuMessage, principal: Principal): Unit =
    val userId = principal.getName
    println(userId)
    println(message)
    message.metaAction match
      case INIT_GAME =>
        require(!(userIdToGameId.keySet contains userId)) //TODO IllegalArgument controller advice?
        val tickets: SortedBag[Ticket] = SortedBag.of(ChMap.tickets.asJava)
        val playerId : PlayerId = PlayerId.PLAYER_1
        val initialGs : GameState = Game.initGame(RemotePlayerProxyWS(messagingTemplate, username = principal.getName, info = new Info(message.data)), playerId, tickets)
        val gameId = generateGameId()
        gameIdToGameState(gameId) = initialGs
        userIdToGameId(userId) = gameId
        userIdToPlayerId(userId) = playerId
        messagingTemplate.convertAndSendToUser(principal.getName, "/queue/tchu-events", ClientNotification(MessageId.GAME_ID.toString, gameId))

      case JOIN_GAME =>
        require(!(userIdToGameId.keySet contains userId))
        val gameId::playerName::Nil : List[String] = message.data.split(" ").toList
        val playerId: PlayerId = PlayerId.PLAYER_2
        val completedInitialGameState: GameState = Game.joiningGame(gameIdToGameState(gameId),
          RemotePlayerProxyWS(messagingTemplate, principal.getName, new Info(playerName)), playerId)
        gameIdToGameState(gameId) = completedInitialGameState
        userIdToGameId(userId) = gameId
        userIdToPlayerId(userId) = playerId
        //TODO messaging tells player blabla joined the game?


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
        messagingTemplate.convertAndSend("/topic/tchu-events", ClientNotification(MessageId.CHAT.toString, message.data))

      case SAVE =>
        ???

      case LOAD =>
        ???



  def generateGameId(): String = UUID.randomUUID().toString

}