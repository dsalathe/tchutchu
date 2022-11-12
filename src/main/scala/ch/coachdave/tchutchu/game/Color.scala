package ch.coachdave.tchutchu.game

enum Color extends Enum[Color]:
  case BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE

object Color:
  val ALL: Vector[Color] = values.toVector
  val COUNT: Int = Color.values.length
