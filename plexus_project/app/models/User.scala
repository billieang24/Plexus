package models

case class User(id: Long, username: String, password: String,givenname:String,lastname:String,gender:String,address:String)

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current


object User {
  
  val user = {
		  get[Long]("id") ~ 
		  get[String]("username")~ 
		  get[String]("password")~ 
		  get[String]("givenname")~ 
		  get[String]("lastname")~ 
		  get[String]("gender")~ 
		  get[String]("address")  map {
		  case id~username~password~givenname~lastname~gender~address=> User(id, username, password,givenname,lastname,gender,address)
  	}
  }
  def findByEmail(username: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where username = {username}").on(
        'username -> username
      ).as(User.user.singleOpt)
    }
  }
  def authenticate(username: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL(
        "select * from user where username = {username} and password = {password}"
      ).on(
        'username -> username,
        'password -> password
      ).as(User.user.singleOpt)
    }
  }
  def create(username: String, password: String,givenname:String,lastname:String,gender:String,month:String,date:String,year:String,address:String)={
	  DB.withConnection { implicit c =>
	  SQL("insert into user (username,password,givenname,lastname,gender,birthdate,address) values ({username},{password},{givenname},{lastname},{gender},{birthdate},{address})").on(
      'username -> username,'password -> password,'givenname -> givenname,'lastname -> lastname,'gender -> gender,'birthdate -> (year+"-"+month+"-"+date),'address -> address
			  ).executeUpdate()
	  }
  }
}