package ch.coachdave.tchutchu.net

import ch.coachdave.tchutchu.UnitSpec
import ch.coachdave.tchutchu.game.{Card, ChMap, PlayerId, PublicCardState, PublicGameState, PublicPlayerState, Route}

class SerdesTest extends UnitSpec:

  "Serdes" should "serialize a gamestate" in {
    val publicGameStateSerde: Serde[PublicGameState] = Serdes.publicGameState
    val fu: List[Card] = List(Card.RED, Card.WHITE, Card.BLUE, Card.BLACK, Card.RED)
    val cs: PublicCardState = PublicCardState(fu, 30, 31)
    val rs1: List[Route] = ChMap.routes.take(2)
    val ps: Map[PlayerId, PublicPlayerState] = Map(PlayerId.PLAYER_1 -> PublicPlayerState(10, 11, rs1), PlayerId.PLAYER_2 -> PublicPlayerState(20, 21, Nil))
    val gs: PublicGameState = PublicGameState(40, cs, PlayerId.PLAYER_2, ps, None)

    publicGameStateSerde.serialize(gs) shouldEqual "40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:"
    publicGameStateSerde.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:") shouldEqual gs
  }
