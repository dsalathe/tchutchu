package ch.coachdave.tchutchu.model

import ch.coachdave.tchutchu.game.UserAction

case class TchuMessage(metaAction: MetaAction, playAction: UserAction, data: String, userId: String)
