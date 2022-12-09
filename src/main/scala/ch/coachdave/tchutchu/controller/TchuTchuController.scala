package ch.coachdave.tchutchu.controller

import ch.coachdave.tchutchu.ai.RandomPlayer
import ch.coachdave.tchutchu.SortedBag
import ch.coachdave.tchutchu.game.{ChMap, Game, GameState, Player, PlayerId, Ticket}
import ch.coachdave.tchutchu.model.{ClientNotification, MetaAction, TchuMessage}
import ch.coachdave.tchutchu.net.{MessageId, RemotePlayerProxyWS}
import ch.coachdave.tchutchu.gui.Info
import ch.coachdave.tchutchu.model.MetaAction.*
import ch.coachdave.tchutchu.net.Serdes.string

import collection.JavaConverters.*
import org.springframework.beans.factory.annotation.Autowired

import java.security.Principal
import org.springframework.messaging.handler.annotation.{Header, MessageMapping, SendTo}
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.{SendToUser, SubscribeMapping}
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.function.ServerRequest.Headers
import org.springframework.web.util.HtmlUtils
import com.google.common.cache.{Cache, CacheBuilder, CacheLoader}

import java.util.UUID
import java.util.concurrent.{ConcurrentLinkedQueue, TimeUnit}
import scala.annotation.tailrec
import scala.collection.mutable

@Controller
class TchuTchuController {

  def getCache[K, V](neutralE: V): Cache[K, V] = CacheBuilder.newBuilder().expireAfterAccess(24, TimeUnit.HOURS).asInstanceOf[CacheBuilder[K, V]]
    .build(new CacheLoader[K, V] {
      override def load(key: K): V =
        println(s"Invalid call with key $key")
        neutralE
    })

  @Autowired
  private val messagingTemplate : SimpMessagingTemplate = null


  private val userIdToGameId: Cache[String, String] = getCache("")
  private val userIdToPlayerId: Cache[String, PlayerId] = getCache(null)
  private val gameIdToGameState: Cache[String, GameState] = getCache(null)
  private val shortNameGameToGameId: Cache[String, String] = getCache("")

  private val queue = new ConcurrentLinkedQueue[String]()


  @MessageMapping(Array("/tchu"))
  def handleClientCommand(message: TchuMessage, principal: Principal): Unit =
    val userId: String = if message.userId.isEmpty then principal.getName else message.userId
    println(userId)
    println(message)
    message.metaAction match
      case INIT_GAME =>
        require(!(userIdToGameId.asMap() containsKey userId)) //TODO IllegalArgument controller advice?
        val playerName::shortName::_ : List[String] = message.data.split(" ").toList
        val gameId: String = initGame(string.deserialize(playerName), principal, userId)
        val confirmedShortName = determineShortName(shortName)
        shortNameGameToGameId.asMap().put(confirmedShortName, gameId)
        val okPrefix = if confirmedShortName == shortName then "ok" else "nok"
        messagingTemplate.convertAndSendToUser(principal.getName, "/queue/tchu-events", ClientNotification(MessageId.GAME_ID.toString, s"$okPrefix $confirmedShortName"))

      case JOIN_SPECIFIC_GAME =>
        require(!(userIdToGameId.asMap() containsKey userId))
        val playerName::shortName::Nil : List[String] = message.data.split(" ").toList
        val playerId: PlayerId = PlayerId.PLAYER_2
        joinGame(principal, userId, shortNameGameToGameId.asMap().get(shortName), string.deserialize(playerName), playerId)
        shortNameGameToGameId.asMap().remove(shortName)
        //TODO messaging tells player blabla joined the game?

      case JOIN_ANY_GAME =>
        if queue.isEmpty then
          queue.add(initGame(string.deserialize(message.data), principal, userId))
        else
          val gameId = queue.remove()
          joinGame(principal, userId, gameId, string.deserialize(message.data), PlayerId.PLAYER_2)

      case RECONNECT =>
        val gameId = userIdToGameId.asMap.get(userId)
        if gameId == null then
          messagingTemplate.convertAndSendToUser(principal.getName, "/queue/tchu-events",
            ClientNotification(MessageId.RECONNECT.toString, userId))
        else
          val gs = gameIdToGameState.asMap.get(gameId)
          val playerId = userIdToPlayerId.asMap.get(userId)
          gameIdToGameState.asMap.put(gameId, Game.updatePlayer(gs, principal.getName, playerId))

      case PLAY =>//TODO add FORFEIT action here?
        val gameId = userIdToGameId.asMap.get(userId)
        if gameId == null then
          messagingTemplate.convertAndSendToUser(principal.getName, "/queue/tchu-events",
            ClientNotification(MessageId.RECONNECT.toString, userId))
        else
          val gs = gameIdToGameState.asMap.get(gameId)
          val playerId = userIdToPlayerId.asMap.get(userId)
          val newGameState = Game.updateGame(gs, playerId, message.playAction, message.data)

          if newGameState.isOver then {
            Game.endPhase(newGameState)
          } else {
            gameIdToGameState.asMap.put(gameId, newGameState)
          }


      case CHAT =>
        println("CHAT case")
        val gameId = Option(userIdToGameId.asMap.get(userId))
        gameId match
          case Some(gId) =>
            val playerName = string.serialize(gameIdToGameState.asMap.get(gId).playerMap(userIdToPlayerId.asMap.get(userId)).getInfo.getPlayerName)
            gameIdToGameState.asMap.get(gId).playerMap foreach ( _._2.sendChatMessage(s"$playerName ${message.data}"))
          case None =>
            val pseudoName = string.serialize(s"User-${userId.substring(userId.length - 3)}")
            messagingTemplate.convertAndSend("/topic/tchu-events",
              ClientNotification(MessageId.CHAT.toString,  s"$pseudoName ${message.data}"))

      case SAVE =>
        ???

      case LOAD =>
        ???


  private def joinGame(principal: Principal, userId: String, gameId: String, playerName: String, playerId: PlayerId): Unit = {
    val completedInitialGameState: GameState = Game.joiningGame(gameIdToGameState.asMap.get(gameId),
      RemotePlayerProxyWS(messagingTemplate, principal.getName, new Info(playerName)), playerId)
    gameIdToGameState.asMap.put(gameId, completedInitialGameState)
    userIdToGameId.asMap().put(userId, gameId)
    userIdToPlayerId.asMap().put(userId, playerId)
    messagingTemplate.convertAndSendToUser(principal.getName, "/queue/tchu-events",
      ClientNotification(MessageId.USER_ID.toString, userId))
  }

  private def initGame(playerName: String, principal: Principal, userId: String) = {
    val tickets: SortedBag[Ticket] = SortedBag.of(ChMap.tickets.asJava)
    val playerId: PlayerId = PlayerId.PLAYER_1
    val initialGs: GameState = Game.initGame(RemotePlayerProxyWS(messagingTemplate, username = principal.getName, info = new Info(playerName)), playerId, tickets)
    val gameId = generateGameId()
    gameIdToGameState.asMap.put(gameId, initialGs)
    userIdToGameId.asMap().put(userId, gameId)
    userIdToPlayerId.asMap().put(userId, playerId)
    messagingTemplate.convertAndSendToUser(principal.getName, "/queue/tchu-events",
      ClientNotification(MessageId.USER_ID.toString, userId))
    gameId
  }

  @tailrec
  private def determineShortName(shortName: String): String =
    if shortNameGameToGameId.asMap() containsKey shortName then
      determineShortName(shortName + generateGameId().substring(0, 1))
    else
      shortName

  def generateGameId(): String = UUID.randomUUID().toString

}
