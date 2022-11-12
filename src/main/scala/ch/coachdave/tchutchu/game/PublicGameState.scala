package ch.coachdave.tchutchu.game

case class PublicGameState(ticketsCount: Int, cardState: PublicCardState, currentPlayerId: PlayerId, private val playerState: Map[PlayerId, PublicPlayerState], lastPlayer: Option[PlayerId]):
  require(ticketsCount >= 0)
  require(playerState.size >= 2)

  def canDrawTickets: Boolean = ticketsCount > 0
  def canDrawCards: Boolean = cardState.deckSize + cardState.discardsSize >= 5
  def playerState(playerId: PlayerId): Option[PublicPlayerState] = playerState.get(playerId)
  def currentPlayerState: PublicPlayerState = playerState.get(currentPlayerId).get
  def claimedRoutes: List[Route] = playerState.values.map(_.routes).reduce(_ ++ _)
