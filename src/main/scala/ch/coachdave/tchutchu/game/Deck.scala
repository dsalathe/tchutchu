package ch.coachdave.tchutchu.game

import ch.coachdave.tchutchu.SortedBag
import collection.JavaConverters.*
import scala.util.Random.shuffle

class Deck[C <: Comparable[C]] private (cards: List[C]):

  def size: Int = cards.size

  def isEmpty: Boolean = cards.isEmpty

  def topCard: C =
    require(!isEmpty)
    cards.head

  def withoutTopCard: Deck[C] =
    require(!isEmpty)
    new Deck(cards.tail)

  def topCardsSorted(count: Int): SortedBag[C] =
    require(count >= 0)
    require(count <= size)
    SortedBag.of(cards.take(count).asJava)

  def topCards(count: Int): List[C] =
    require(count >= 0)
    require(count <= size)
    cards.take(count)

  def withoutTopCards(count: Int): Deck[C] =
    require(count >= 0)
    require(count <= size)
    new Deck(cards.drop(count))

  def distribute(nHeap: Int, sizeHeap: Int): (List[SortedBag[C]], Deck[C]) =
    require(nHeap * sizeHeap <= size)
    lazy val allSteps: LazyList[(SortedBag[C], Deck[C])] = (SortedBag.of(), this) #:: allSteps.map{ case (_, d) => (d.topCardsSorted(sizeHeap), d.withoutTopCards(sizeHeap))}
    val (distributed, remainings): (List[SortedBag[C]], List[Deck[C]]) = allSteps.drop(1).take(nHeap).toList.unzip
    (distributed, remainings.last)

object Deck:
  def of[C <: Comparable[C]](cards: SortedBag[C]): Deck[C] = new Deck(shuffle(cards.toList.asScala.toList))
