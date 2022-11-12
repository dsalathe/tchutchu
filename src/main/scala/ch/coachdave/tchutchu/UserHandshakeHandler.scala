package ch.coachdave.tchutchu

import com.sun.security.auth.UserPrincipal
import org.springframework.http.server.ServerHttpRequest
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.support.DefaultHandshakeHandler

import java.security.Principal
import java.util
import java.util.UUID

class UserHandshakeHandler extends DefaultHandshakeHandler {

  override def determineUser(request: ServerHttpRequest, wsHandler: WebSocketHandler, attributes: util.Map[String, AnyRef]): Principal =
    new UserPrincipal("user-" + UUID.randomUUID().toString)

}
