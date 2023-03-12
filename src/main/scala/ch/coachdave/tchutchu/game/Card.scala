package ch.coachdave.tchutchu.game

import java.util
import scala.jdk.CollectionConverters.*

enum Card(oColor: Option[Color]) extends Enum[Card]:
  case BLACK extends Card(Some(Color.BLACK))
  case VIOLET extends Card(Some(Color.VIOLET))
  case BLUE extends Card(Some(Color.BLUE))
  case GREEN extends Card(Some(Color.GREEN))
  case YELLOW extends Card(Some(Color.YELLOW))
  case ORANGE extends Card(Some(Color.ORANGE))
  case RED extends Card(Some(Color.RED))
  case WHITE extends Card(Some(Color.WHITE))
  case LOCOMOTIVE extends Card(None)

  def color: Option[Color] = oColor


object Card:
  val ALL: Vector[Card] = Card.values.toVector
  val COUNT: Int = ALL.length
  val CARS: Vector[Card] = ALL dropRight 1
  def CARS_JAVA: util.List[Card] = CARS.asJava

  def of(color: Color): Card = CARS(color.ordinal)
