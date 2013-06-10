package models


import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json.JsValue
import play.api.libs.json.JsObject
import play.api.libs.ws.WS
import java.util.Date

case class User(email: String, password: String,firstName:String,lastName:String,gender:String,birthdate:String)

object User {
  def convertToUser(j: JsValue):User = {
    new User(
      (j \ "username").as[String],
      null,
      (j \ "firstName").as[String],
      (j \ "lastName").as[String],
      (j \ "gender").as[String],
      ((j \ "birthdate").as[JsObject] \ "iso").as[String]
    )
  }
  def convertToUserMap(j: JsValue) = {
    List((j \ "username").as[String],
        (j \ "firstName").as[String],
        (j \ "lastName").as[String],
        (j \ "gender").as[String],
        ((j \ "birthdate").as[JsObject] \ "iso").as[String],
        (j \ "objectId").as[String]
        )
  }
}