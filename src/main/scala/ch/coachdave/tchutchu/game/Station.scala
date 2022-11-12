package ch.coachdave.tchutchu.game

case class Station(id: Int, name: String):
  require(id >= 0, "Id cannot be negative")


  override def toString: String = name
