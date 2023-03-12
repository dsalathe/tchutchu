package ch.coachdave.tchutchu.game

import scala.annotation.tailrec

class Trail private (l: Int, s1: Option[Station], s2: Option[Station], trail: List[Route]):
  val length: Int = l
  val station1: Option[Station] = s1
  val station2: Option[Station] = s2

  override def toString: String =
    def buildRoad(trail: List[Route]) = trail.foldLeft(List(Trail.getHead(trail).get))((acc, route) => route.stationOpposite(acc.head).get::acc)
    trail match
      case Nil => "Empty trail"
      case trail => buildRoad(trail).mkString(" - ") + s" ($length)"


object Trail:
  def getHead(trail: List[Route]): Option[Station] = trail match
    case Nil => None
    case r::Nil => Some(r.station1)
    case r1::r2::_ => if r2.stations.contains(r1.station1) then Some(r1.station2) else Some(r1.station1)

  def getTail(trail: List[Route]): Option[Station] = trail match
    case Nil => None
    case r::Nil => Some(r.station2)
    case trail => getHead(trail.reverse)

  def longest(routes: List[Route]): Trail =

    def canProlonge(c: List[Route])(pr: Route): Boolean = c match
      case Nil => false
      case Route(_, s1, s2, _, _, _)::Nil => (pr.stations.toSet & Set(s1, s2)).nonEmpty
      case Route(_, s1, s2, _, _, _)::middle::_ => middle.stations.contains(s1) && pr.stations.contains(s2) || middle.stations.contains(s2) && pr.stations.contains(s1)
    def rs(c: List[Route]): List[Route] = routes filterNot c.contains filter canProlonge(c)

    @tailrec
    def helper(cs: List[List[Route]], longest: List[Route]): List[Route] = cs match
      case Nil => longest
      case cs =>
        val csPrime =
          for
            c <- cs
            r <- rs(c)
          yield r::c
        helper(csPrime, (longest::csPrime).maxBy(_.map(_.length).sum))

    routes match
      case Nil => new Trail(0, None, None, Nil)
      case rs =>
        val trail = helper(routes.map(List(_)), List(rs.maxBy(_.length)))
        new Trail(trail.map(_.length).sum, getHead(trail), getTail(trail), trail)
