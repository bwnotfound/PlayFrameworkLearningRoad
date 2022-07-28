package controllers

import javax.inject._
import play.api.mvc.ControllerComponents
import play.api.mvc.AbstractController
import models.TaskListInMemoryModel
import scala.collection.mutable
import scala.util.control

@Singleton
class TaskList1 @Inject() (cc: ControllerComponents)
    extends AbstractController(cc) {

  def login = Action { implicit request =>
    Ok(views.html.login1())
  }

  def logout = Action {
    Redirect(routes.TaskList1.login).withNewSession
  }

  def infoStringHandle(infoList: List[String]) = Action { implicit request =>
    Ok(views.html.infoStringHandle(infoList))
  }

  def register = Action { implicit request =>
    val postVals = request.body.asFormUrlEncoded
    var exitFlag: Boolean = false
    val exitInfo = mutable.ListBuffer[String]()
    postVals
      .map { argsMap =>
        val usernameOption = argsMap.get("username")
        val passwordOption = argsMap.get("password")
        if (usernameOption.isEmpty) {
          exitInfo.append("Username is illegal")
          exitFlag = true
        }
        if (passwordOption.isEmpty) {
          exitInfo.append("Password is illegal")
          exitFlag = true
        }
        if (exitFlag == false) {
          val username = usernameOption.get.head
          val password = passwordOption.get.head
          if (username.isEmpty()) {
            exitInfo.append("Username cannot be empty")
            exitFlag = true
          }
          if (password.isEmpty()) {
            exitInfo.append("Password cannot be empty")
            exitFlag = true
          }
        }
        if (exitFlag == false) {
          val username = usernameOption.get.head
          val password = passwordOption.get.head
          if (TaskListInMemoryModel.userExist(username)) {
            exitInfo.append("User Already Exist")
            exitFlag = true
          } else {
            TaskListInMemoryModel.createUser(username, password)
          }
        }
        if (exitFlag == false) {
          val username = usernameOption.get.head
          Redirect(routes.TaskList1.taskList).withSession(
            "username" -> username
          )
        } else
          // Ok(views.html.infoStringHandle(exitInfo.toList))
          Redirect(routes.TaskList1.login).flashing(
            "error" -> "Register failed"
          )
      }
      .getOrElse(throw new Exception("Unknown error"))
  }

  def usernameShow = Action { implicit request =>
    Ok(views.html.infoStringHandle(TaskListInMemoryModel.showUser))
  }

  def validateLoginByGet(username: String, password: String) = Action {
    Ok(s"$username logged in with $password")
  }

  def validateLoginByPost = Action { implicit request =>
    val postVals = request.body.asFormUrlEncoded
    postVals
      .map { argsMap =>
        val username = argsMap("username").head
        val password = argsMap("password").head
        if (TaskListInMemoryModel.validateUser(username, password))
          Redirect(routes.TaskList1.taskList).withSession(
            "username" -> username
          )
        else
          Redirect(routes.TaskList1.login).flashing(
            "error" -> "Password/Username illegal"
          )
      }
      .getOrElse(
        Redirect(routes.TaskList1.login).flashing("error" -> "Request illegal")
      )
  }

  def taskList = Action { implicit request =>
    val usernameOption = request.session.get("username")

    usernameOption
      .map { username =>
        val tasks = TaskListInMemoryModel.getTasks(username)
        Ok(views.html.taskList1(tasks))
      }
      .getOrElse(Redirect(routes.TaskList1.login))

  }

  def productShow(prodName: String, prodNum: Int) = Action {
    Ok(s"Product name is $prodName, product number is $prodNum.")
  }

  def addTask = Action { implicit request =>
    val usernameOption = request.session.get("username")
    usernameOption
      .map { username =>
        val postVals = request.body.asFormUrlEncoded
        postVals
          .map { argsMap =>
            val tasks = argsMap("addTask")
            TaskListInMemoryModel.addTasks(username, "", tasks)
            Redirect(routes.TaskList1.taskList)
          }
          .getOrElse(Redirect(routes.TaskList1.taskList))
      }
      .getOrElse(Redirect(routes.TaskList1.login))
  }

  def deleteTask = Action { implicit request =>
    val usernameOption = request.session.get("username")
    usernameOption
      .map { username =>
        val postVals = request.body.asFormUrlEncoded
        postVals
          .map { argsMap =>
            println("---------------------------------------------")
            println(argsMap)
            println("---------------------------------------------")
            val index = argsMap("index").head.toInt
            if(TaskListInMemoryModel.deleteTask(username,index))
              Redirect(routes.TaskList1.taskList)
            else
              Redirect(routes.TaskList1.taskList).flashing("error"->"index illegal")
          }
          .getOrElse(Redirect(routes.TaskList1.taskList).flashing("error"->"Oops, something went wrong"))
      }
      .getOrElse(Redirect(routes.TaskList1.login))
  }

  def randomNumber = Action{
    Ok(util.Random.nextInt(100).toString)
  }

  def randomString(length:Int) = Action{
    Ok(util.Random.nextString(length))
  }

}
