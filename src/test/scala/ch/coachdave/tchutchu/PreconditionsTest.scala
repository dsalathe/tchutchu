package ch.coachdave.tchutchu

import ch.coachdave.tchutchu.Preconditions.*
import org.scalatest._

class PreconditionsTest extends UnitSpec:

  "Preconditions" should "throw nothing if boolean is true" in {
    checkArgument(true)
  }

  it should "throw an IllegalArgumentException otherwise" in {
    assertThrows[IllegalArgumentException](checkArgument(false))
  }
