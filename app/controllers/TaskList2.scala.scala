package controllers

import javax.inject.Inject
import play.api.mvc.ControllerComponents
import play.api.mvc.AbstractController
import javax.inject._

@Singleton
class TaskList2@Inject()(cc:ControllerComponents) extends AbstractController(cc){

  def index = Action{
    Ok(views.html.indexWeb2())
  }

  def login = Action{
    Ok(views.html.loginWeb2())
  }

}
