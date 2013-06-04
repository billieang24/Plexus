package models

case class User(id: Long, username: String, password: String)

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current


object User {
  
  val user = {
		  get[Long]("id") ~ 
		  get[String]("username")~ 
		  get[String]("password")  map {
		  case id~username~password => User(id, username, password)
  	}
  }
 
  def all(): List[User] = DB.withConnection { implicit c =>
  	SQL("select * from user").as(user *)
  }
  def create(username: String, password: String) {
	  DB.withConnection { implicit c =>
	  SQL("insert into user (username,password) values ({username},{password})").on(
      'username -> username,'password -> password
			  ).executeUpdate()
	  }
  }

  def delete(id: Long) {
	  DB.withConnection { implicit c =>
	  SQL("delete from user where id = {id}").on(
			  'id -> id
			  ).executeUpdate()
}
  }
}