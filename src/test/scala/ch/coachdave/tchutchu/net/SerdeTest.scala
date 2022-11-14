package ch.coachdave.tchutchu.net

import ch.coachdave.tchutchu.SortedBag
import ch.coachdave.tchutchu.game.Color
import ch.coachdave.tchutchu.UnitSpec

class SerdeTest extends UnitSpec:

  "Serde" should "work with Int" in {
    val intSerde: Serde[Int]  = Serde.of(_.toString, _.toInt)
    intSerde.serialize(2021) shouldEqual "2021"
    intSerde.deserialize("2021") shouldEqual 2021
  }

  it should "work with enums" in {
    val color: Serde[Option[Color]] = Serde.oneOf(Color.ALL)
    color.serialize(Some(Color.BLACK)) shouldEqual "0"
    color.deserialize("4") shouldEqual Some(Color.YELLOW)
  }

  it should "handle empty string to None value and vice-versa" in {
    val color: Serde[Option[Color]] = Serde.oneOf(Color.ALL)
    color.serialize(None) shouldEqual ""
    color.deserialize("") shouldEqual None
  }

  it should "handle list of enums" in {
    val color: Serde[Option[Color]] = Serde.oneOf(Color.ALL)
    val listOfColor: Serde[List[Option[Color]]] = Serde.listOf(color, "+")
    listOfColor.serialize(List(Some(Color.BLACK), Some(Color.YELLOW))) shouldEqual "0+4"
    listOfColor.deserialize("0+4") shouldEqual List(Some(Color.BLACK), Some(Color.YELLOW))
  }

  it should "handle bag of enum" in {
    val color: Serde[Color] = Serde.oneOfNoNull(Color.ALL)
    val listOfColor: Serde[SortedBag[Color]] = Serde.bagOf(color, "-")
    listOfColor.serialize(SortedBag.of(1, Color.YELLOW, 2, Color.BLACK)) shouldEqual "0-0-4"
    listOfColor.deserialize("0-0-4") shouldEqual SortedBag.of(1, Color.YELLOW, 2, Color.BLACK)
  }

  it should "handle list of bag of colors" in {
    val color: Serde[Color] = Serde.oneOfNoNull(Color.ALL)
    val bagOfColor: Serde[SortedBag[Color]] = Serde.bagOf(color, "-")
    val listOfBagOfColor: Serde[List[SortedBag[Color]]] = Serde.listOf(bagOfColor, "+")
    listOfBagOfColor.serialize(List(SortedBag.of(1, Color.YELLOW, 2, Color.BLACK), SortedBag.of(1, Color.VIOLET, 1, Color.GREEN))) shouldEqual "0-0-4+1-3"
    listOfBagOfColor.deserialize("0-0-4+1-3") shouldEqual List(SortedBag.of(1, Color.YELLOW, 2, Color.BLACK), SortedBag.of(1, Color.VIOLET, 1, Color.GREEN))
  }