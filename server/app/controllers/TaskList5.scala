package controllers

import javax.inject._

import play.api.mvc._
import play.api.i18n._
import models.TaskListInMemoryModel
import play.api.libs.json._
import models._

import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

@Singleton
class TaskList5 @Inject() (protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents)(implicit ec: ExecutionContext) 
    extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

  private val model = new TaskListDatabaseModel(db)

  def load = Action { implicit request =>
    Ok(views.html.version5Main())
  }

  implicit val userDataReads = Json.reads[UserData]
  implicit val taskDataWrites = Json.writes[TaskData]

  def withJsonBody[A](f: A => Future[Result])(implicit request: Request[AnyContent], reads: Reads[A]): Future[Result] = {
    request.body.asJson.map { body =>
      Json.fromJson[A](body) match {
        case JsSuccess(a, path) => f(a)
        case e @ JsError(_) => Future.successful(Redirect(routes.TaskList3.load()))
      }
    }.getOrElse(Future.successful(Redirect(routes.TaskList3.load())))
  }

  def withSessionUsername(f: String => Result)(implicit request: Request[AnyContent]) = {
    request.session.get("username").map(f).getOrElse(Ok(Json.toJson(Seq.empty[String])))
  }

  def withSessionUserId(f: Int => Future[Result])(implicit request: Request[AnyContent]) = {
    request.session.get("userId").map(userIdString=>f(userIdString.toInt)).getOrElse(Future.successful(Ok(Json.toJson(Seq.empty[String]))))
  }

  def validate = Action.async { implicit request =>
    withJsonBody[UserData] { ud =>
      model.validateUser(ud.username, ud.password).map{ userExist =>
        userExist match {
          case Some(userId) => 
            Ok(Json.toJson(true))
              .withSession("username" -> ud.username, "userId" -> userId.toString, "csrfToken" -> play.filters.csrf.CSRF.getToken.map(_.value).getOrElse(""))
          case None => 
            Ok(Json.toJson(false))
        } 
      } 
    }
  }

  def createUser = Action.async { implicit request =>
    withJsonBody[UserData] { ud =>
      model.createUser(ud.username, ud.password).map{userIdOption=>
        userIdOption match {
          case Some(userId) =>
            Ok(Json.toJson(true))
              .withSession("username" -> ud.username, "userId" -> userId.toString, "csrfToken" -> play.filters.csrf.CSRF.getToken.map(_.value).getOrElse(""))
          case None => 
            Ok(Json.toJson(false))
        }
      }
    }
  }

  def deleteUser(username:String) = Action.async {
    model.deleteUser(username).map{status =>
      Ok(Json.toJson(status))
    }
  }

  def taskList = Action.async { implicit request =>
    withSessionUserId { userId =>
      model.getTasks(userId).map{ taskData =>
        Ok(Json.toJson(taskData))
      }
    }
  }

  def addTask = Action.async { implicit request =>
    withSessionUserId { userId =>
      withJsonBody[String] { task =>
        model.addTask(userId, task).map{addStatus =>
          if(addStatus)
            Ok(Json.toJson(true))
          else
            Ok(Json.toJson(false))
        };
      }
    }
  }

  def delete = Action.async { implicit request =>
    withJsonBody[Int] { itemId =>
      model.removeTask(itemId).map{deleteStatus =>
        Ok(Json.toJson(deleteStatus))
      }
    }
  }
  
  def logout = Action { implicit request =>
    Ok(Json.toJson(true)).withSession(request.session - "username")
  }

}