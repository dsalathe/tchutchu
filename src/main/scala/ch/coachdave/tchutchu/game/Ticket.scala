package ch.coachdave.tchutchu.game

import scala.jdk.CollectionConverters.*
import scala.collection.immutable.TreeSet

case class Ticket(trips: List[Trip]) extends Ordered[Ticket]:
  require(trips.nonEmpty, "No trips given")
  require(trips.map{case Trip(from, _, _) => from.name}.toSet.size == 1, s"Multiple starts: ${trips.map{case Trip(from, _, _) => from.name}.toSet}")
  def this(trips: java.util.List[Trip]) = this(trips.asScala.toList)

  def this(from: Station, to: Station, points: Int) = this(List(Trip(from, to, points)))

  def text: String = trips match
    case Trip(from, to, points)::Nil => s"$from - $to ($points)"
    case Trip(from, _, _)::tail => s"$from - " + collection.SortedSet(tail: _*)(Ordering.by[Trip, String](_.to.name)).map{case Trip(_, to, points) => s"$to ($points)"}.mkString("{", ", ", "}")
    case Nil => ""

  def points(stationConnectivity: StationConnectivity): Int = trips.map(_.points(stationConnectivity)).max

  def compare(that: Ticket): Int = text.compare(that.text)


object Ticket:

  def apply(from: Station, to: Station, points: Int): Ticket = apply(List(Trip(from, to, points)))
