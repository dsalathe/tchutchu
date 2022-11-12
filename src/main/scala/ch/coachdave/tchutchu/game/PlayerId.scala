package ch.coachdave.tchutchu.game

import ch.coachdave.tchutchu.game.PlayerId._

enum PlayerId:
  case PLAYER_1, PLAYER_2


  def next = ALL((this.ordinal+1)%COUNT)

object PlayerId:
  def ALL: Vector[PlayerId] = values.toVector
  def COUNT: Int = ALL.length
