package ch.coachdave.tchutchu.game

import collection.JavaConverters._

import Constants._
import ch.coachdave.tchutchu.SortedBag
import Route.Level
import java.util.{List as JList}

case class Route(id: String, station1: Station, station2: Station, length: Int, level: Level, color: Option[Color]):
  require(station1 != station2, "stations must be different")
  require(length >= MIN_ROUTE_LENGTH, s"route length should be at least $MIN_ROUTE_LENGTH")
  require(length <= MAX_ROUTE_LENGTH, s"route length should be at most $MAX_ROUTE_LENGTH")
  val stations: List[Station] = List(station1, station2)
  def stationsJava: JList[Station] = stations.asJava

  def stationOpposite(station: Station): Option[Station] = station match
    case `station1` => Some(station2)
    case `station2` => Some(station1)
    case _ => None

  def possibleClaimCards: List[SortedBag[Card]] =
    (for
      nLocomotive <- (0 to (if level == Level.OVERGROUND then 0 else length)).toList
      c <- Color.ALL
      if color.isEmpty || color.get == c
    yield SortedBag.of(length - nLocomotive, Card.of(c), nLocomotive, Card.LOCOMOTIVE)).distinct // ugly quick fix...


  def additionalClaimCardsCount(claimCards: SortedBag[Card], drawnCards: SortedBag[Card]) =
    require(level == Level.UNDERGROUND)
    require(drawnCards.size() == 3)
    val card: Card = claimCards.toSet.asScala.foldLeft(Card.LOCOMOTIVE)((acc, c) => if c == Card.LOCOMOTIVE then acc else c)
    drawnCards.toList.asScala.count(c => c == card || c == Card.LOCOMOTIVE)

  def claimPoints: Int = Constants.ROUTE_CLAIM_POINTS(length)


object Route:
  enum Level extends Enum[Level]:
    case OVERGROUND
    case UNDERGROUND
  end Level
