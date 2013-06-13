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
            "post" -> JsString(wallPost.content),
            "postedTo" -> JsObject(
    	    	Seq(
    	    	    "__type"->JsString("Pointer"),
    	    	    "className"->JsString("_User"),
    	    	    "objectId"->JsString(wallPost.postedToId)
    	    	)
    	    ),
    	    "postedBy" -> JsObject(
    	        Seq(
    	        	"__type"->JsString("Pointer"),
    	        	"className"->JsString("_User"),
    	        	"objectId"->JsString(wallPost.postedById)
    	        	)
    	    )
      )
    )
  }
  
    def reads(j: JsValue)= WallPost(
        (j \ "content").as[String],
        ((j \ "postedTo").as[JsValue] \ "objectId").as[String],
        ((j \ "postedBy").as[JsValue] \ "objectId").as[String],
    	(j \ "objectId").as[String]
    )
  }
}