package controllers

import javax.inject.Inject
import play.api.mvc._
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

  def withJsonBody[A](f:A=>Result)(implicit request:Request[AnyContent], reads:Reads[A]) = {
    request.body.asJson.map { body =>
      Json.fromJson[A](body) match {
        case JsSuccess(a, _) => f(a)
        case e @ JsError(_) => Ok(Json.obj("status"->false))
      }
    }.getOrElse(Ok(Json.obj("status"->false)))
  }

  def validateUser = Action{ implicit request =>

    withJsonBody((ud:UserData) => {
      if (TaskListInMemoryModel.validateUser(ud.username, ud.password)) {
        Ok(Json.obj("status"->true))
          .withSession("username" -> ud.username, "csrfToken"->play.filters.csrf.CSRF.getToken.get.value)
      } else {
        Ok(Json.obj("status"->false))
      }
    })
  }

  def createUser = Action{ implicit request =>
    request.body.asJson.map { body =>
      Json.fromJson[UserData](body) match {
        case JsSuccess(ud, _) =>
           if (TaskListInMemoryModel.createUser(ud.username, ud.password)) {
            Ok(Json.obj("status"->true))
              .withSession("username" -> ud.username, "csrfToken"->play.filters.csrf.CSRF.getToken.get.value)
          } else {
            Ok(Json.obj("status"->false))
          }
        case e @ JsError(_) => Ok(Json.obj("status"->false))
      }
    }.getOrElse(Ok(Json.obj("status"->false)))

  }
  
  def taskList = Action { implicit request =>
    val usernameOption = request.session.get("username")
    usernameOption.map { username =>
      Ok(TaskListInMemoryModel.getTasksJson(username))
    }.getOrElse(Ok(Json.obj("status"->false)))
  }

  def addTask = Action { implicit request =>
    val usernameOption = request.session.get("username")
    usernameOption.map { username =>
      request.body.asJson.map{ body=>
        body.validate[String] match {
          case JsSuccess(task,_)=>TaskListInMemoryModel.addTaskJson(username,task)
          case _ => println("------------------error occurred when addTask---------------------")
        }
      }
      Ok(Json.obj("status"->true))
    }.getOrElse(Ok(Json.obj("status"->false)))
  }

  def deleteTask = Action {implicit request =>
    val usernameOption = request.session.get("username")
    usernameOption.map { username =>
      request.body.asJson.map{ body=>
        println(body)
        body.validate[Int] match {
          case JsSuccess(index,_)=>TaskListInMemoryModel.deleteTaskJson(username, index)
          case _ => println("------------------error occurred when deleteTask---------------------")
        }
      }
      Ok(Json.obj("status"->true))
    }.getOrElse(Ok(Json.obj("status"->false)))
  }

  def logout = Action {
    Redirect(routes.TaskList3.load).withNewSession
  }

}
