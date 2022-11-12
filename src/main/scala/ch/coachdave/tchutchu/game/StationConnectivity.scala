package ch.coachdave.tchutchu.game

trait StationConnectivity:
  def connected(s1: Station, s2: Station): Boolean
