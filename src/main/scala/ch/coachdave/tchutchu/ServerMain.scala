package ch.coachdave.tchutchu

import ch.coachdave.tchutchu.ai.RandomPlayer
import ch.coachdave.tchutchu.game.{ChMap, Game, Player, PlayerId, Ticket}
import ch.coachdave.tchutchu.net.RemotePlayerProxyRawTCP
import ch.coachdave.tchutchu.SortedBag

import java.net.{ServerSocket, Socket}
import collection.JavaConverters.*
import scala.concurrent.{ Future, ExecutionContext }
import ExecutionContext.Implicits.global

//TODO remove this is a dead class
object ServerMain:

  def getNRemotePlayers(nPlayers: Int): List[Player] =
    val actualN = if nPlayers == 0 then 2 else nPlayers
    val firstPort = 5108
    for port <- (firstPort until firstPort+actualN).toList yield new RemotePlayerProxyRawTCP({
      val serverSocket = new ServerSocket(port)
      serverSocket.setSoTimeout(30_000) // Timeout after 30s for testing, think to increase value for prod => config?
      serverSocket.accept()
    })

  def startServer(names: String*): Unit =//List[Int] =
    val p1::p2::Nil: List[String] = names.toList match
      case l @ _::_::Nil => l
      case n::Nil => List(n, "ROBOT")
      case Nil => List("Ada", "Charles")
      case _ => throw new IllegalArgumentException(s"arguments $names are not compliant: please provide exactly 0, 1 or 2 names")


    val MAX_PLAYERS = 2

    val remotePlayers: List[Player] = getNRemotePlayers(names.length)
    val robotPlayers: List[Player] = List.fill(MAX_PLAYERS - remotePlayers.size)(new RandomPlayer)
    val allPlayers: List[Player] = remotePlayers ++ robotPlayers

    val tickets: SortedBag[Ticket] = SortedBag.of(ChMap.tickets.asJava)
    val playerNames: Map[PlayerId, String] = Map(PlayerId.PLAYER_1 -> p1, PlayerId.PLAYER_2 -> p2)
    val players: Map[PlayerId, Player] = (PlayerId.ALL zip allPlayers).toMap
//    Future {
//      Game.play(players, playerNames, tickets)
//    }
//    remotePlayers.map(p => p.getPort)
