package controllers

import javax.inject.Inject
import play.api.mvc.ControllerComponents
import play.api.mvc.AbstractController
import javax.inject._
import models.TaskListInMemoryModel
import models._
import play.api.libs.json._
import play.api.libs.functional.syntax._


@Singleton
class TaskList3 @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  def load = Action { implicit request=>
    Ok(views.html.indexWeb3())
  }

  implicit val userDataReads = Json.reads[UserData] 

  // val optional = (JsPath \ "username").read[String] and (JsPath \ "password").read[String]

  def validateUser = Action{ implicit request =>
    request.body.asJson.map { body =>
      Json.fromJson[UserData](body) match {
        case JsSuccess(ud, path) =>
           if (TaskListInMemoryModel.validateUser(ud.username, ud.password)) {
            Ok(Json.toJson(true))
              .withSession("username" -> ud.username, "csrfToken"->play.filters.csrf.CSRF.getToken.get.value)
          } else {
            Ok(views.html.loginWeb2())
          }
        case e @ JsError(_) => Redirect(routes.TaskList3.load)
      }
    }.getOrElse(Redirect(routes.TaskList3.load))

  }

  def taskList = Action { implicit request =>
    val usernameOption = request.session.get("username")
    usernameOption.map { username =>
      Ok(TaskListInMemoryModel.getTasksJson(username))
    }.getOrElse(Ok(Json.obj(
        "status"->false
      )))
  }

}
