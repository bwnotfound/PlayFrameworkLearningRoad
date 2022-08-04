package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class TaskList7 @Inject() (cc: ControllerComponents)
    extends AbstractController(cc) {

  def load = Action { implicit request =>
    Ok(views.html.version7Main())
  }
}
