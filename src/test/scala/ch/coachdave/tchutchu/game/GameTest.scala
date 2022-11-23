package ch.coachdave.tchutchu.game

import ch.coachdave.tchutchu.TestRandomizer.SEED

import collection.JavaConverters.*
import ch.coachdave.tchutchu.{SortedBag, TestRandomizer, UnitSpec, game}
import ch.coachdave.tchutchu.game.{ChMap, Player, PlayerId, Ticket, UserAction}
import ch.coachdave.tchutchu.gui.Info
import ch.coachdave.tchutchu.net.Serdes
import org.mockito.{ArgumentCaptor, ArgumentMatchers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar.mock

class GameTest extends UnitSpec:

  private val tickets: SortedBag[Ticket] = SortedBag.of(ChMap.tickets.asJava)
  val playerInfo: Info = new Info("Anonymous")
  scala.util.Random.setSeed(SEED) // Ensure seed is set

  private def getStubPlayer() =
    val stubPlayer: Player = mock[Player]
    when(stubPlayer.getInfo).thenReturn(playerInfo)
    stubPlayer

  private def getInitialGameState(player1: Player) =

    Game.initGame(player1, PlayerId.PLAYER_1, tickets)

  private def getGameFullGameState(player1: Player, player2: Player) =
    val stubPlayer2: Player = mock[Player]
    when(stubPlayer2.getInfo).thenReturn(playerInfo)
    Game.joiningGame(getInitialGameState(player1), player2, PlayerId.PLAYER_2)


  "The Game" should "be initiated with only one player" in {
    val initialGameState = getInitialGameState(getStubPlayer())
    initialGameState.allPlayer should have size 1
  }

  it should "let a player join an initiated game and init players" in {
    val stubPlayer1 = getStubPlayer()
    val stubPlayer2 = getStubPlayer()
    val completedGameState = getGameFullGameState(stubPlayer1, stubPlayer2)

    completedGameState.allPlayer should have size 2
    verify(stubPlayer2).initPlayers(ArgumentMatchers.eq(PlayerId.PLAYER_2), any())
    verify(stubPlayer1).updateState(any(), any())
  }

  private def testTicketsChosenAndReturnGameState(p1BeforeP2: Boolean,
                                                  stubPlayer1: Player = getStubPlayer(),
                                                  stubPlayer2: Player = getStubPlayer()): GameState =
    val captor1 = ArgumentCaptor.forClass(classOf[SortedBag[Ticket]])
    val captor2 = ArgumentCaptor.forClass(classOf[SortedBag[Ticket]])

    val completedGameState = getGameFullGameState(stubPlayer1, stubPlayer2)
    verify(stubPlayer1).setInitialTicketChoice(captor1.capture())
    verify(stubPlayer2).setInitialTicketChoice(captor2.capture())

    val firstChosen = if p1BeforeP2 then
      Game.updateGame(completedGameState, PlayerId.PLAYER_1, UserAction.INITIAL_TICKETS_CHOSEN,
        Serdes.bagOfTicket.serialize(captor1.getValue)) else
      Game.updateGame(completedGameState, PlayerId.PLAYER_2, UserAction.INITIAL_TICKETS_CHOSEN,
        Serdes.bagOfTicket.serialize(captor2.getValue))
    verify(stubPlayer1, times(2)).updateState(any(), any())
    verify(stubPlayer2, times(2)).updateState(any(), any())
    firstChosen.getNextExpectedAction shouldEqual UserAction.INITIAL_TICKETS_CHOSEN
    val secondChosen = if p1BeforeP2 then
      Game.updateGame(firstChosen, PlayerId.PLAYER_2, UserAction.INITIAL_TICKETS_CHOSEN,
        Serdes.bagOfTicket.serialize(captor2.getValue)) else
      Game.updateGame(firstChosen, PlayerId.PLAYER_1, UserAction.INITIAL_TICKETS_CHOSEN,
        Serdes.bagOfTicket.serialize(captor1.getValue))

    secondChosen.getNextExpectedAction shouldEqual UserAction.PLAY_TURN
    verify(stubPlayer1, times(3)).updateState(any(), any())
    verify(stubPlayer2, times(3)).updateState(any(), any())
    secondChosen

  it should "allow players to choose initial tickets in no any particular order" in {
    testTicketsChosenAndReturnGameState(true)
    testTicketsChosenAndReturnGameState(false)
  }

  it should "let players draw cards a couple of times" in {
    val p1 = getStubPlayer()
    val p2 = getStubPlayer()

    val afterInitialTicketsDrawn = testTicketsChosenAndReturnGameState(true, p1, p2)

    // First turn P1:
    val firstTurnFirstDraw  = Game.updateGame(afterInitialTicketsDrawn, PlayerId.PLAYER_1, UserAction.PLAY_TURN, "FIRST_CARD 1")
    val firstTurnSecondDraw = Game.updateGame(firstTurnFirstDraw, PlayerId.PLAYER_1, UserAction.DRAW_SECOND, "-1")

    // Second turn P2:
    val secondTurnFirstDraw = Game.updateGame(firstTurnSecondDraw, PlayerId.PLAYER_2, UserAction.PLAY_TURN, "FIRST_CARD -1")
    val secondTurnSecondDraw = Game.updateGame(secondTurnFirstDraw, PlayerId.PLAYER_2, UserAction.DRAW_SECOND, "2")

    // Third turn P1:
    val thirdTurnFirstDraw  = Game.updateGame(secondTurnSecondDraw, PlayerId.PLAYER_1, UserAction.PLAY_TURN, "FIRST_CARD -1")
    val thirdTurnSecondDraw = Game.updateGame(thirdTurnFirstDraw, PlayerId.PLAYER_1, UserAction.DRAW_SECOND, "3")

    thirdTurnSecondDraw.playerState(PlayerId.PLAYER_1).get.cards should have size 4+2+2
    thirdTurnSecondDraw.playerState(PlayerId.PLAYER_2).get.cards should have size 4+2

    verify(p2).initPlayers(ArgumentMatchers.eq(PlayerId.PLAYER_2), any())
  }
