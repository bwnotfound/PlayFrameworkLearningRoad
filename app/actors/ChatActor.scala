package actors

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorRef

class ChatActor(out: ActorRef, manager: ActorRef) extends Actor {
  
  import ChatActor._

    manager ! ChatManager.NewChatter(self)

  def receive = {
    case s: String                => manager ! ChatManager.Message(s)
    case ChatManager.Message(msg) => out ! msg
    case m => println("Unhandled message in ChatActor.receive:" + m)
  }
}

object ChatActor {
  def props(out: ActorRef, manager: ActorRef) = Props(
    new ChatActor(out, manager)
  )
}
