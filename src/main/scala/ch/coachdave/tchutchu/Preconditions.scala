package ch.coachdave.tchutchu

object Preconditions:
  def checkArgument(shouldBeTrue: Boolean): Unit = if !shouldBeTrue then throw new IllegalArgumentException
