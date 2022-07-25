package models

import scala.collection.mutable
import java.lang.ProcessBuilder.Redirect

object TaskListInMemoryModel {

  private val userMap = mutable.Map[String, String]("a" -> "a")
  private val taskMap = mutable.Map[String, mutable.ArrayBuffer[String]](
    "a" -> mutable.ArrayBuffer[String]("1", "2", "3")
  )

  def userExist(username: String): Boolean = userMap.contains(username)

  def validateUser(username: String, password: String): Boolean = {
    if (userExist(username))
      userMap.get(username).map(_ == password).getOrElse(false)
    else false
  }

  def createUser(username: String, password: String): Boolean = {
    val result = userMap.get(username) match {
      case None        => true
      case Some(value) => false
    }
    if (result) {
      userMap.addOne(username -> password)
      taskMap.addOne(username -> mutable.ArrayBuffer[String]())
      true
    } else false
  }

  def showUser: List[String] = {
    val userSeq = mutable.ListBuffer[String]()
    userMap.foreach(argsMap => userSeq.append(argsMap._1).append(argsMap._2))
    userSeq.toList
  }

  def deleteUser(username: String, password: String): Boolean = {
    if (userExist(username)) {
      userMap -= username
    }
    true
  }
  def getTasks(username: String): Seq[String] = {
    taskMap.get(username).getOrElse(mutable.ArrayBuffer[String]()).toSeq
  }
  def addTasks(
      username: String,
      task: String,
      tasks: Seq[String] = Seq[String]()
  ): Unit = {
    if (tasks.isEmpty) {
      if (!task.isEmpty) {
        taskMap.get(username).map { userTask =>
          userTask += task
        }
      }
    } else {
      taskMap.get(username).map { userTask =>
        tasks.foreach(x => if (!x.isEmpty) userTask += x)
      }
    }
  }
  def deleteTask(username: String, index: Int): Boolean = {
    taskMap
      .get(username).map { userTask =>
        if (index >= userTask.length || index < 0)
          false
        else {
          userTask.remove(index)
          true
        }
      }
      .getOrElse(false)
  }
}
