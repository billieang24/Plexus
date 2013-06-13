package plexus.deserializer.json

import models.WallPost
import play.api.libs.json.Format
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsValue

trait WallPostDeserializer{
  implicit object WallPostFormatter extends Format[WallPost]{
    def writes(wallPost: WallPost): JsValue = { JsObject(
        Seq(
            "content" -> JsString(wallPost.content),
            "postedTo" -> JsObject(
    	    	Seq(
    	    	    "__type"->JsString("Pointer"),
    	    	    "className"->JsString("_User"),
    	    	    "objectId"->JsString(wallPost.postedTo.objectId)
    	    	)
    	    ),
    	    "postedBy" -> JsObject(
    	        Seq(
    	        	"__type"->JsString("Pointer"),
    	        	"className"->JsString("_User"),
    	        	"objectId"->JsString(wallPost.postedBy.objectId)
    	        	)
    	    )
      )
    )
  }
  
    def reads(j: JsValue)= WallPost(
        (j \ "content").as[String],
        controllers.Application.getUser("objectId",((j \ "postedTo").as[JsValue] \ "objectId").as[String]),
        controllers.Application.getUser("objectId",((j \ "postedBy").as[JsValue] \ "objectId").as[String]),
    	(j \ "objectId").as[String]
    )
  }
}