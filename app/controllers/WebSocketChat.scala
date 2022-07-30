package controllers

import javax.inject.Inject
import play.api.mvc._
import javax.inject._
import models.TaskListInMemoryModel
import models._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.libs.streams.ActorFlow
import actors.ChatActor
import akka.actor.Props
import actors.ChatManager

@Singleton
class WebSocketChat @Inject() (cc: ControllerComponents)(implicit
    system: ActorSystem,
    mat: Materializer
) extends AbstractController(cc) {
    
  val manager = system.actorOf(Props[ChatManager](), "Manager")

  def index = Action { implicit request =>
    Ok(views.html.chatPage())
  }

  def socket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef { out =>
      ChatActor.props(out, manager)
    }
  }
}
