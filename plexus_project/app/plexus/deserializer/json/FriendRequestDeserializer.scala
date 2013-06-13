package plexus.deserializer.json

import models.FriendRequest
import play.api.libs.json.Format
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsValue

trait FriendRequestDeserializer{
  implicit object FriendRequestFormatter extends Format[FriendRequest]
    def writes(friendRequest: FriendRequest): JsValue = { JsObject(
        Seq(
            "requester" -> JsObject(
    	    	Seq(
    	    	    "__type"->JsString("Pointer"),
    	    	    "className"->JsString("_User"),
    	    	    "objectId"->JsString(friendRequest.requesterId)
    	    	)
    	    ),
    	    "user" -> JsObject(
    	        Seq(
    	        	"__type"->JsString("Pointer"),
    	        	"className"->JsString("_User"),
    	        	"objectId"->JsString(friendRequest.userId)
    	        	)
    	    )
      )
    )
  }
  
    def reads(j: JsValue)= FriendRequest(
        ((j \ "requester").as[JsValue] \ "objectId").as[String],
        ((j \ "user").as[JsValue] \ "objectId").as[String],
    	(j \ "objectId").as[String]
    )
}