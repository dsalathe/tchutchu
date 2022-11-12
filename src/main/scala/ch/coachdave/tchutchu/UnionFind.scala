package ch.coachdave.tchutchu

import scala.annotation.tailrec

final class UnionFind(maxSize: Int):
  private val uf = (0 until maxSize).toArray
  private val sz = Array.fill(maxSize)(1)

  def build = for (i <- (0 until maxSize).toVector) yield find(i) // full flatten each element

  override def toString: String = uf.toVector.toString

  def union(p: Int, q: Int): UnionFind =
    val i = find(p)
    val j = find(q)
    if i == j then this else
      if sz(i) < sz(j) then
        uf(i) = j
        sz(j) += sz(i)
      else
        uf(j) = i
        sz(i) += sz(j)
    this

  @tailrec
  def find(p: Int): Int = if p == uf(p) then p else
    uf(p) = uf(uf(p)) // path compression
    find(uf(p))

  def connected(p: Int, q: Int): Boolean = find(p) == find(q)

object UnionFind:
  def apply(maxSize: Int): UnionFind = new UnionFind(maxSize)
