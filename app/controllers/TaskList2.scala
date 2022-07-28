package controllers

import javax.inject.Inject
import play.api.mvc.ControllerComponents
import play.api.mvc.AbstractController
import javax.inject._
import models.TaskListInMemoryModel

@Singleton
class TaskList2 @Inject() (cc: ControllerComponents)
    extends AbstractController(cc) {

  def index = Action { implicit request =>
    // if(request.session.get("username").isEmpty)

    // else
    //   Redirect(routes.TaskList2.taskListContent)
    Ok(views.html.indexWeb2())
  }

  def login = Action {
    Ok(views.html.loginWeb2())
  }

  def logout = Action {
    Redirect(routes.TaskList2.index).withNewSession
  }

  def registerContent = Action {
    Ok(views.html.registerWeb2())
  }

  def taskListContent = Action { implicit request =>
    val usernameOption = request.session.get("username")
    usernameOption
      .map { username =>
        Ok(views.html.taskList2(TaskListInMemoryModel.getTasks(username)))
      }
      .getOrElse(Redirect(routes.TaskList2.login))
  }

  def validateUser = Action {
    implicit request =>
      val postVals = request.body.asFormUrlEncoded
      postVals
        .map { argsMap =>
          val username = argsMap("username").head
          val password = argsMap("password").head
          if (TaskListInMemoryModel.validateUser(username, password)) {
            Ok(views.html.taskList2(TaskListInMemoryModel.getTasks(username)))
              .withSession("username" -> username)
          } else {
            Ok(views.html.loginWeb2())
          }
        }
        .getOrElse(
          Ok(views.html.loginWeb2())
        )
  }

  def createUser(username: String, password: String) = Action {
    implicit request =>
      if (TaskListInMemoryModel.createUser(username, password)) {
        Ok(views.html.taskList2(TaskListInMemoryModel.getTasks(username)))
          .withSession("username" -> username)
      } else {
        Ok("false")
      }
  }

  def deleteTask(index: Int) = Action { implicit request =>
    val usernameOption = request.session.get("username")
    usernameOption
      .map { username =>
        if (TaskListInMemoryModel.deleteTask(username, index))
          Ok("Success!")
        else
          Ok("something went wrong")
      }
      .getOrElse(Ok("Something went wrong!"))
  }

  def addTask(task: String) = Action { implicit request =>
    val usernameOption = request.session.get("username")
    usernameOption
      .map { username =>
        TaskListInMemoryModel.addTasks(username, "", Seq[String](task))
        Ok("true")
      }
      .getOrElse(Ok("False"))
  }

}
