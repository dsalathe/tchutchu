package ch.coachdave.tchutchu.game

case class PublicPlayerState(ticketCount: Int, cardCount: Int, routes: List[Route]):
  require(ticketCount >= 0)
  require(cardCount >= 0)

  val carCount = Constants.INITIAL_CAR_COUNT - routes.map(_.length).sum
  val claimPoints = routes.map(_.claimPoints).sum
