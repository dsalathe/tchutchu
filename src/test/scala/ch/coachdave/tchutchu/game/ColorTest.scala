package ch.coachdave.tchutchu.game

import ch.coachdave.tchutchu.UnitSpec
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ColorTest extends UnitSpec:

  "Color Enum" should "have 8 elements" in {
    Color.COUNT shouldEqual 8
  }
