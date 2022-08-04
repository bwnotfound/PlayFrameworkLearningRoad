package scalaJS

import slinky.core.Component
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._
import scala.scalajs.js.annotation._
import org.scalajs.dom.document
import org.scalajs.dom.html
import org.scalajs.dom.window
import play.api.libs.json._
import models._
import models.ReadAndWrite._
import org.scalajs.dom.raw.Event

@react class Version7MainComponent extends Component {
  type Props = Unit
  case class State(IsLogin: Boolean)

  def initialState: State = State(false)

  def render(): ReactElement = {
    if (state.IsLogin) {
      TaskListComponent(()=>setState(state.copy(IsLogin = false)))
    } else {
      LoginComponent(()=>setState(state.copy(IsLogin = true)))
    }
  }
}

@react class LoginComponent extends Component {
  case class Props(doLogin: () => Unit)
  case class State(
      loginName: String,
      loginPass: String,
      createName: String,
      createPass: String,
      loginMessage: String,
      createMessage: String
  )

  
  val validateRoute =
    document.getElementById("validateRoute").asInstanceOf[html.Input].value
  val createRoute =
    document.getElementById("createRoute").asInstanceOf[html.Input].value
  implicit val csrfToken =
    document.getElementById("csrfToken").asInstanceOf[html.Input].value

  def initialState: State = State("", "", "", "", "", "")
  def render(): ReactElement = {
    div(
      h2("Login:"),
      br(),
      "Username:",
      input(`type` :="text", id :="loginName", value := state.loginName, onChange := (e => setState(state.copy(loginName = e.target.value)))),
      br(),
      "Password:",
      input(`type` :="password", id :="loginPass", value := state.loginPass, onChange := (e => setState(state.copy(loginPass = e.target.value)))),
      br(),
      button("Login", onClick:= (e => login())),
      state.loginMessage, 
      br(),
      h2("Create User:"),
      br(),
      "Username:",
      input(`type` :="text", id :="createName", value := state.createName, onChange := (e => setState(state.copy(createName = e.target.value)))),
      br(),
      "Password:",
      input(`type` :="password", id :="createPass", value := state.createPass, onChange := (e => setState(state.copy(createPass = e.target.value)))),
      br(),
      button("Login", onClick:= (e => createUser())),
      state.createMessage
      // ce("h2", null, "Create User:"),
      // ce("br"),
      // "Username: ",
      // ce("input", {type: "text", id: "createName", value: this.state.createName, onChange: e => this.changerHandler(e)}),
      // ce("br"),
      // "Password: ",
      // ce("input", {type: "password", id: "createPass", value: this.state.createPass, onChange: e => this.changerHandler(e)}),
      // ce("br"),
      // ce("button", {onClick: e => this.createUser(e)}, "Create User"),
      // ce("span", {id: "create-message"}, this.state.createMessage)
    )
  }

  def login():Unit = {
    val userData = new models.UserData(state.loginName, state.loginPass)
    FetchJson.fetchPost(
      validateRoute,
      userData,
      (status: Boolean) => {
        if (status) {
          props.doLogin()
        } 
        else {
          setState(state.copy(loginMessage="Login failed"))
          window.setTimeout(()=>setState(state.copy(loginMessage="")),1000)
        }
      },
      e => println(e)
    )
  }

  def createUser():Unit = {
    val userData = new UserData(state.createName, state.createPass)
    FetchJson.fetchPost(
      createRoute,
      userData,
      (status: Boolean) => {
        if (status) {
          props.doLogin()
        } else {
          setState(state.copy(createMessage="Create user failed"))
          window.setTimeout(()=>setState(state.copy(createMessage="")),1000)
        }
      },
      e => println(e)
    )
  }

}

@react class TaskListComponent extends Component {
  
  case class Props(doLogout: () => Unit)
  case class State(newTask: String, taskMessage: String, tasks: Seq[TaskData])

  val tasksRoute =
    document.getElementById("tasksRoute").asInstanceOf[html.Input].value
  val deleteRoute =
    document.getElementById("deleteRoute").asInstanceOf[html.Input].value
  val addRoute =
    document.getElementById("addRoute").asInstanceOf[html.Input].value
  val logoutRoute =
    document.getElementById("logoutRoute").asInstanceOf[html.Input].value
  implicit val csrfToken =
    document.getElementById("csrfToken").asInstanceOf[html.Input].value


  def initialState: State = State("", "", Seq.empty[TaskData])

  override def componentDidMount(): Unit = {
    loadTasks()
  }

  def render(): ReactElement = {
    div(
      "Task List",
      br(),
      ul(
        state.tasks.map{ task =>
          li(task.text, key := task.id.toString(), onClick := ((e) => handleDeleteClick(task.id)))
        }
      ),
      br(),
      div(
        input(`type` := "text", value := state.newTask, onChange := (e => setState(state.copy(newTask=e.target.value))) ),
        button("Add Task", onClick := (e => handleAddClick())),
        state.taskMessage
      ),
      br(),
      button("Logout", onClick:= (e=>props.doLogout()))
    )
  }

  def loadTasks() {
    FetchJson.fetchGet(tasksRoute,(tasks: Seq[TaskData]) => {
        setState(state.copy(tasks = tasks))
      },
      e => println(e))
  }

  def handleAddClick() {
    FetchJson.fetchPost(
      addRoute,
      state.newTask,
      (status: Boolean) => {
        if (status) {
          loadTasks()
          setState(state.copy(newTask=""))
        } else {
          setState(state.copy(taskMessage="add task failed"))
          window.setTimeout(()=>setState(state.copy(taskMessage="")),1000)
        }
      },
      e => println(e)
    )
  }

  def handleDeleteClick(itemId: Int) {
    FetchJson.fetchPost(
      deleteRoute,
      itemId,
      (status: Boolean) => {
        if (status) {
          loadTasks()
        } else {
          setState(state.copy(taskMessage="delete task failed"))
          window.setTimeout(()=>setState(state.copy(taskMessage="")),1000)
        }
      },
      e => println(e)
    )
  }

}
