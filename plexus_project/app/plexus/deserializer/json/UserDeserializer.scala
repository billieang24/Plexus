package plexus.deserializer.json

import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import models.User

trait UserDeserializer {
  implicit object UserFormatter extends Format[User] {
    def reads(j: JsValue): User = User(
      (j \ "username").as[String],
      null,
      (j \ "firstName").as[String],
      (j \ "lastName").as[String],
      (j \ "gender").as[String],
      ((j \ "birthdate").as[JsObject] \ "iso").as[String].substring(0, 10),
      (j \ "objectId").as[String]
      )

    def writes(user: User): JsValue = JsObject(
        Seq(
            ("username"->JsString(user.email)),
            ("email"->JsString(user.email)),
            ("password"->JsString(user.password)),
            ("firstName"->JsString(user.firstName)),
            ("lastName"->JsString(user.lastName)),
            ("gender"->JsString(user.gender)),
            ("fullName"->JsString((user.firstName.toLowerCase()+user.lastName.toLowerCase()).replaceAll(" ", ""))),
            ("birthdate"->JsObject(Seq("__type"->JsString("Date"),"iso"->JsString(user.birthdate+"T00:00:00.000Z"))))
        )
    )
  }
}