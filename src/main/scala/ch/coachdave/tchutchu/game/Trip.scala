package ch.coachdave.tchutchu.game

import scala.jdk.CollectionConverters.*
import java.util.List as JList


case class Trip(from: Station, to: Station, points: Int):
  require(points > 0, s"$points is not strictly positive")

  def points(stationConnectivity: StationConnectivity): Int = if stationConnectivity.connected(from, to) then points else -points

object Trip:

  def all(from: List[Station], to: List[Station], points: Int): List[Trip] =
    for
      f <- from
      t <- to
    yield apply(f, t, points)

  def all(from: JList[Station], to: JList[Station], points: Int): JList[Trip] = all(from.asScala.toList, to.asScala.toList, points).asJava
