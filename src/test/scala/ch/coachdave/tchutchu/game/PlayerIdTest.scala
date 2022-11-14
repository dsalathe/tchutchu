package ch.coachdave.tchutchu.game

import PlayerId.*
import ch.coachdave.tchutchu.UnitSpec
class PlayerIdTest extends UnitSpec:

  "PlayerId" should "be defined correctly" in {
    ALL shouldEqual List(PLAYER_1, PLAYER_2)
  }

  it should "find the next player accordingly" in {
    PLAYER_1.next shouldEqual PLAYER_2
    PLAYER_2.next shouldEqual PLAYER_1
  }
