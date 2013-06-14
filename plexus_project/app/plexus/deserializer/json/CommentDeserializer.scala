package plexus.deserializer.json

import models.Comment
import models.User
import models.WallPost
import play.api.libs.json.Format
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsValue

trait CommentDeserializer{
  implicit object CommentFormatter extends Format[Comment] with UserDeserializer with WallPostDeserializer{
	def reads(j: JsValue)= Comment(
        (j \ "content").as[String],
        controllers.Application.getUser("objectId",((j \ "pageOwner").as[JsValue] \ "objectId").as[String]),
        controllers.Application.getUser("objectId",((j \ "user").as[JsValue] \ "objectId").as[String]),
    	controllers.Application.get("WallPost?","{\"objectId\":\""+((j \ "WallPost").as[JsValue]\"objectId").as[String]+"\"}").map{
       		result => (result.json \ "results").as[Seq[JsObject]].head.as[WallPost]
    	  }.await.get,
    	(j \ "objectId").as[String]
    )
    def writes(comment: Comment): JsValue =  JsObject(
        Seq(
            "content"-> JsString(comment.content),
            "pageOwner"->JsObject(
    	    	Seq(
    	    	    "__type"->JsString("Pointer"),
    	    	    "className"->JsString("_User"),
    	    	    "objectId"->JsString(comment.pageOwner.objectId)
    	    	)
    	    ),
            "WallPost"-> JsObject(
    	    	Seq(
    	    	    "__type"->JsString("Pointer"),
    	    	    "className"->JsString("WallPost"),
    	    	    "objectId"->JsString(comment.wallPost.objectId)
    	    	)
    	    ),
    	    "user" -> JsObject(
    	    	Seq(
    	    	    "__type"->JsString("Pointer"),
    	    	    "className"->JsString("_User"),
    	    	    "objectId"->JsString(comment.user.objectId)
    	    	)
    	    )
      )
    )
  }
}