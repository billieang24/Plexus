package plexus.deserializer.json

import models.Friend
import play.api.libs.json.Format
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsValue

trait FriendDeserializer{
  implicit object FriendFormatter extends Format[Friend]
    def writes(friend: Friend): JsValue = { JsObject(
        Seq(
            "friend"->JsObject(
    	    	Seq(
    	    	    "__type"->JsString("Pointer"),
    	    	    "className"->JsString("_User"),
    	    	    "objectId"->JsString(friend.friendId)
    	    	)
    	    ),
    	    "user"->JsObject(
    	        Seq(
    	        	"__type"->JsString("Pointer"),
    	        	"className"->JsString("_User"),
    	        	"objectId"->JsString(friend.userId)
    	        	)
    	    )
      )
    )
  }
  
    def reads(j: JsValue)= Friend(
        ((j \ "friend").as[JsValue] \ "objectId").as[String],
        ((j \ "user").as[JsValue] \ "objectId").as[String],
    	(j \ "objectId").as[String]
    )
}