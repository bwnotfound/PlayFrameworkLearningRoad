package models

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext
import models.Tables._
import scala.concurrent.Future
import org.mindrot.jbcrypt.BCrypt

class TaskListDatabaseModel(db: Database)(implicit ec: ExecutionContext) {

  def userExistByString(username: String): Future[Boolean] = {
    db.run(Users.filter(userRow => userRow.username === username).result).map{userRows =>
      userRows.nonEmpty
    }
  }

  def userExistById(userId: Int): Future[Boolean] = {
    db.run(Users.filter(userRow => userRow.id === userId).result).map{userRows =>
      userRows.nonEmpty
    }
  }

  def getUserId(username: String): Future[Option[Int]] = {
    db.run(Users.filter(userRow => userRow.username === username).result).map{userRows => 
      userRows.headOption.map{userRow=>
          userRow.id
      } 
    }
  }

  def validateUser(username: String, password: String): Future[Option[Int]] = {
    val matchs = db.run(Users.filter(userRow => userRow.username === username).result)
    matchs.map{userRows => 
      userRows.headOption.flatMap{userRow=>
        if(BCrypt.checkpw(password, userRow.password))
          Some(userRow.id)
        else
          None
      } 
    }
  }
  
  def createUser(username: String, password: String): Future[Option[Int]] = {
    if(username.isEmpty || password.isEmpty())
          Future.successful(None)
    else{
      userExistByString(username).flatMap{ userExistStatus =>
        if(userExistStatus){
          Future.successful(None)
        }
        else{
          db.run(Users += UsersRow(-1, username, BCrypt.hashpw(password, BCrypt.gensalt()))).flatMap{ addCount =>
            if(addCount > 0)
              getUserId(username)
            else
              Future.successful(None)
          }
        }
      }
    }   
  }
  
  def deleteUser(username: String): Future[Boolean] = { //dev function: need to remove
    val matchs = db.run(Users.filter(userRow => userRow.username===username).delete)
    matchs.map{ deleteCount=>
      deleteCount > 0
    }
  }

  def getTasks(userId: Int): Future[Seq[TaskData]] = {
    db.run(
      (for{
        user<- Users if user.id === userId
        item<- Items if item.userId === user.id
      }yield{
        item
      }
    ).result
    ).map(_.map(item=>TaskData(item.itemId, item.text)))
  }
  
  def addTask(userId: Int, task: String): Future[Boolean] = {
    if(task.isEmpty)
      Future[Boolean](false)
    else
      userExistById(userId).flatMap{ userExistStatus =>
        if(userExistStatus){
          db.run(Items += ItemsRow(-1, userId, task)).map { addCount =>
            addCount > 0
          }
        }
        else{
          Future[Boolean](false)
        }
      }
  }
  
  def removeTask(itemId: Int): Future[Boolean] = {
    db.run(Items.filter(_.itemId === itemId).delete).map{ deleteCount =>
      deleteCount > 0
    }
  }

}