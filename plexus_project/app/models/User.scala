package models


import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json.JsValue
import play.api.libs.json.JsObject
import play.api.libs.ws.WS
import java.util.Date

case class User(email: String, password: String,firstName:String,lastName:String,gender:String,birthdate:String,objectId:String)