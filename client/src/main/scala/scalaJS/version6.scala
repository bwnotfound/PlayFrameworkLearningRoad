package scalaJS

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.experimental._
import slinky.web.html.head
import slinky.web.html.method
import play.api.libs.json.Json
import models.ReadAndWrite._
import scala.scalajs.js.Thenable.Implicits._
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsError
import scala.concurrent.ExecutionContext
import scala.scalajs.js.annotation.JSExportTopLevel
import models.TaskData
import models.UserData

object Version6 {

  implicit val ec = ExecutionContext.global

  implicit val csrfToken =
    document.getElementById("csrfToken").asInstanceOf[html.Input].value
  val validateRoute =
    document.getElementById("validateRoute").asInstanceOf[html.Input].value
  val tasksRoute =
    document.getElementById("tasksRoute").asInstanceOf[html.Input].value
  val createRoute =
    document.getElementById("createRoute").asInstanceOf[html.Input].value
  val deleteRoute =
    document.getElementById("deleteRoute").asInstanceOf[html.Input].value
  val addRoute =
    document.getElementById("addRoute").asInstanceOf[html.Input].value
  val logoutRoute =
    document.getElementById("logoutRoute").asInstanceOf[html.Input].value

  val loginSection =
    document.getElementById("login-section").asInstanceOf[html.Div]
  val taskSection =
    document.getElementById("task-section").asInstanceOf[html.Div]

  def init(): Unit = {
    println(
      "------------version6 init!---------------"
    )
    taskSection.setAttribute("hidden", "true")
  }

  def shiftVisibility(): Unit = {
    if (loginSection.hasAttribute("hidden")) {
      loginSection.removeAttribute("hidden")
      taskSection.setAttribute("hidden", "true")
    } else {
      taskSection.removeAttribute("hidden")
      loginSection.setAttribute("hidden", "true")
    }
  }

  def loginSuccess(): Unit = {
    shiftVisibility()
    loadTasks()
    document.getElementById("loginName").asInstanceOf[html.Input].value = ""
    document.getElementById("loginPass").asInstanceOf[html.Input].value = ""
    document.getElementById("createName").asInstanceOf[html.Input].value = ""
    document.getElementById("createPass").asInstanceOf[html.Input].value = ""
  }

  def delayExecution[A](target: dom.Element)(msg: String, time: Double = 1000): Unit = {
    target.innerHTML = msg
    window.setTimeout(() => {
      target.innerHTML = ""
    }, time)
  }

  @JSExportTopLevel("login")
  def login(): Unit = {
    val username =
      document.getElementById("loginName").asInstanceOf[html.Input].value
    val password =
      document.getElementById("loginPass").asInstanceOf[html.Input].value
    val data = new models.UserData(username, password)

    FetchJson.fetchPost(
      validateRoute,
      data,
      (status: Boolean) => {
        if (status) {
          loginSuccess()
        } else {
          delayExecution(
            document
              .getElementById("login-message")
              .asInstanceOf[html.Span]
          )("Login Failed")
        }
      },
      e => println(e)
    )
  }

  @JSExportTopLevel("createUser")
  def createUser() {
    val username =
      document.getElementById("createName").asInstanceOf[html.Input].value;
    val password =
      document.getElementById("createPass").asInstanceOf[html.Input].value;
    val data = new UserData(username, password)
    FetchJson.fetchPost(
      createRoute,
      data,
      (status: Boolean) => {
        if (status) {
          loginSuccess()
        } else {
          delayExecution(document
            .getElementById("create-message")
            .asInstanceOf[html.Span])("User Creation Failed")
        }
      },
      e => println(e)
    )
  }

  @JSExportTopLevel("loadTasks")
  def loadTasks(): Unit = {
    println("In load task!!!!")
    val div = document.getElementById("task-list").asInstanceOf[html.Div]
    val ul = document.createElement("ul").asInstanceOf[html.UList]
    val headers = new Headers()
    headers.set("Content-Type", "application/json")
    headers.set("Csrf-Token", csrfToken)
    FetchJson.fetchGet(
      tasksRoute,
      (tasks: Seq[TaskData]) => {
        for (task <- tasks) {
          val li = document.createElement("li").asInstanceOf[html.LI]
          val text = document.createTextNode(task.text)
          li.appendChild(text)
          li.onclick = e => delete(task.id)
          ul.appendChild(li)
        }
        div.innerHTML = ""
        div.appendChild(ul)
      },
      e => println(e)
    )
  }

  @JSExportTopLevel("delete")
  def delete(itemId: Int): Unit = {
    FetchJson.fetchPost(
      deleteRoute,
      itemId,
      (status: Boolean) => {
        if (status) {
          loadTasks()
        } else {
          delayExecution(document
            .getElementById("task-message")
            .asInstanceOf[html.Span])("Failed to delete.")
        }
      },
      e => println(e)
    )
  }

  @JSExportTopLevel("logout")
  def logout() {
    FetchJson.fetchGet(
      logoutRoute,
      (status: Boolean) => {
        shiftVisibility()
      },
      e => println(e)
    )
  }

  @JSExportTopLevel("addTask")
  def addTask(): Unit = {
    val task = document.getElementById("newTask").asInstanceOf[html.Input]
    val taskMessage = document.getElementById("task-message").asInstanceOf[html.Span]
    FetchJson.fetchPost(
      addRoute,
      task.value,
      (status: Boolean) => {
        if (status) {
          loadTasks();
          task.value = "";
        } else {
          delayExecution(taskMessage)("Failed to add.")
        }
      },
      e => println(e)
    )
  }

}
