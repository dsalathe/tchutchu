package ch.coachdave.tchutchu.game

import ch.coachdave.tchutchu.UnionFind

class StationPartition private (uf: Vector[Int]) extends StationConnectivity:
  override def connected(s1: Station, s2: Station): Boolean = if s1.id >= uf.length || s2.id >= uf.length || s1.id < 0 || s2.id < 0 then s1 == s2 else uf(s1.id) == uf(s2.id)

object StationPartition:
  class Builder(stationCount: Int):
    private val unionFind = UnionFind(stationCount)
    def build: StationPartition = new StationPartition(unionFind.build)
    def connect(station1: Station, station2: Station): Builder = {unionFind.union(station1.id, station2.id);this}
    override def toString: String = build.toString
