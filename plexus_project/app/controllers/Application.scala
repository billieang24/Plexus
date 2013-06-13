package controllers

import models.User
import models.Friend
import models.FriendRequest
import models.WallPost
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.ws.WS
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.JsString
import plexus.deserializer.json.UserDeserializer
import plexus.deserializer.json.FriendDeserializer
import plexus.deserializer.json.FriendRequestDeserializer
import plexus.deserializer.json.WallPostDeserializer
import scala.collection.mutable.ListBuffer
import java.net.URLEncoder
import models.FriendRequest
import models.FriendRequest

object Application extends Controller with Secured with UserDeserializer with FriendDeserializer with WallPostDeserializer with FriendRequestDeserializer{
 
  val signUpForm = Form( //NO VALIDATIONS YET !!!!
      tuple(
      "email" -> text,
      "password" -> nonEmptyText,
      "firstName" -> text,
      "lastName" -> text,
      "gender" -> text,
      "birthdate"-> date
      )
  )
  val postForm = Form(
      tuple(
      "userId"->text,
      "content"->text
      )
  )
  val idForm = Form(
      "userId"->text
  )
  val searchForm = Form(
      "keyword"->text
  )
  def signUp = Action {
    Ok(views.html.sign_up(signUpForm))
  }
  def newUser = Action { implicit request =>
    signUpForm.bindFromRequest.fold(
    formWithErrors =>  BadRequest(views.html.sign_up(formWithErrors)),
    value => { 
    		var params = request.body.asFormUrlEncoded.get
    		var email = params.get("email") match{
    			case Some(a) => a.head
    		}
    		var password = params.get("password") match{
    			case Some(a) => a.head
    		}
    		var firstName = params.get("firstName") match{
    			case Some(a) => a.head
    		}
    		var lastName = params.get("lastName") match{
    			case Some(a) => a.head
    		}
    		var gender = params.get("gender") match{
    			case Some(a) => a.head
    		}
    		var birthdate = params.get("birthdate") match{
    			case Some(a) => a.head
    		}
    		var user = User(email,password,firstName,lastName,gender,birthdate,null)
    		post("_User",Json.toJson(user))
    		Redirect(routes.Application.index)
    	}
  	)
  }
  def index = IsAuthenticated {  username => implicit request =>
        val friendsObjectIdList = ListBuffer[String]()
       	val requestsObjectIdList = ListBuffer[String]()
       	val friendsList = ListBuffer[User]()
       	val requestsList = ListBuffer[User]()
       	var user = get("_User","{\"username\":\""+username+"\"}").map{
       		result =>(result.json \ "results").as[List[JsObject]].head.as[User] 
       	}.await.get
       	get("FriendsList","{\"user\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+user.objectId+"\"}}").map{
       		result => (result.json \ "results").as[Seq[JsObject]].map{
       			friend => friendsObjectIdList += ((friend \ "friend").as[JsObject] \ "objectId").as[String]
       		}
       	}.await.get
       	get("FriendRequests","{\"user\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+user.objectId+"\"}}").map{
       		result => (result.json \ "results").as[Seq[JsObject]].map{
       			request => requestsObjectIdList += ((request \ "requester").as[JsObject] \ "objectId").as[String]
       		}
       	}.await.get
       	requestsObjectIdList.map{
        	id => get("_User","{\"objectId\":\""+id+"\"}").map{
       			result => requestsList += (result.json \ "results").as[List[JsObject]].head.as[User] 
       		}.await.get
       	}
       	friendsObjectIdList.map{
        	id => get("_User","{\"objectId\":\""+id+"\"}").map{
       			result => friendsList += (result.json \ "results").as[List[JsObject]].head.as[User] 
       		}.await.get
       	}
       	Ok(views.html.home(user,friendsList,requestsList))
  }
  def profile()=IsAuthenticated {  username => implicit request =>
    idForm.bindFromRequest.fold(
    	errors => BadRequest,
    	value =>{
    	  var params = request.body.asFormUrlEncoded.get
    	  var id = params.get("userId") match{
    			case Some(a) => a.head
    		}
    	  var user = get("_User","{\"username\":\""+username+"\"}").map{
       		result => (result.json \ "results").as[List[JsObject]].head.as[User]
    	  }.await.get
    	  var pageOwner = get("_User","{\"objectId\":\""+id+"\"}").map{
       		result => (result.json \ "results").as[List[JsObject]].head.as[User]
    	  }.await.get
    	  var notFriend=get("FriendsList","{\"user\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+user.objectId+"\"},\"friend\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+id+"\"}}").map{
    	    result => (result.json\"results").as[List[JsObject]].isEmpty
    	  }.await.get
    	  var role = {
    	    if(user.objectId.equals(id)) "owner"
    	    else if (!notFriend) "friend"
    	    else "none"
    	  }
    	  Ok(views.html.profile(pageOwner, role))
    	}
    )
  }
  def search = IsAuthenticated {  username => implicit request =>
    searchForm.bindFromRequest.fold(
    formWithErrors =>  BadRequest,
    value =>{
      var keyword = request.body.asFormUrlEncoded.get.get("keyword")match{
    			case Some(a) => a.head
    		}
        get("_User","{\"fullName\":{\"$regex\":\""+keyword+"\"}}").map{
        	result => 
        		val userList = ListBuffer[User]()
        		(result.json \ "results").as[Seq[JsObject]].map{
        			 entity=> userList += (entity).as[User]
        		}
        		Ok(views.html.search(userList))
        }.await.get
    }
    )
  }
  def sendFriendRequest=IsAuthenticated {  username => implicit request =>
    idForm.bindFromRequest.fold(
    	errors => BadRequest,
    	value =>{
    	  var params = request.body.asFormUrlEncoded.get
    	  var id = params.get("userId") match{
    			case Some(a) => a.head
    	  }
    	  var user = get("_User","{\"username\":\""+username+"\"}").map{
    	    result =>println(result.json)
    	    (result.json \ "results").as[List[JsObject]].head.as[User]
    	  }.await.get
    	  var data = Json.toJson(FriendRequest(user.objectId,id,null))
    	  println(data)
    	  post("FriendRequests",data).map{
    	    result => println(result.json)
    	  }
    	  Redirect(routes.Application.index)
    	}
    )
  }
  def acceptFriendRequest()=IsAuthenticated {  username => implicit request =>
    idForm.bindFromRequest.fold(
    	errors => BadRequest,
    	value =>{
    	  var params = request.body.asFormUrlEncoded.get
    	  var id = params.get("userId") match{
    			case Some(a) => a.head
    	  }
    	  var user = get("_User","{\"username\":\""+username+"\"}").map{
    	    result =>(result.json \ "results").as[List[JsObject]].head.as[User]
    	  }.await.get
    	  var data1 = Json.toJson(Friend(user.objectId,id,null))
    	  var data2 = Json.toJson(Friend(id,user.objectId,null))
    	  post("FriendsList",data1)
    	  post("FriendsList",data2)
    	  var requestObjectId = get("FriendRequests","{\"user\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+user.objectId+"\"},\"requester\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+id+"\"}}").map{
       		result => (result.json \ "results").as[Seq[JsObject]].head.as[FriendRequest].objectId
    	  }.await.get
    	  delete("FriendRequests/",requestObjectId)
    	  Redirect(routes.Application.index)
    	}
    )
  }
  def unfriend = IsAuthenticated {  username => implicit request =>
    idForm.bindFromRequest.fold(
    	errors => BadRequest,
    	value =>{
    	  var params = request.body.asFormUrlEncoded.get
    	  var id = params.get("userId") match{
    			case Some(a) => a.head
    	  }
    	  var user = get("_User","{\"username\":\""+username+"\"}").map{
    	    result =>(result.json \ "results").as[List[JsObject]].head.as[User]
    	  }.await.get
    	  var friendId1 = get("FriendsList","{\"user\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+user.objectId+"\"},\"friend\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+id+"\"}}").map{
       		result => println(result.json)
       		(result.json \ "results").as[Seq[JsObject]].head.as[Friend].objectId
    	  }.await.get
    	  var friendId2 = get("FriendsList","{\"user\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+id+"\"},\"friend\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+user.objectId+"\"}}").map{
       		result => println(result.json)
       		(result.json \ "results").as[Seq[JsObject]].head.as[Friend].objectId
    	  }.await.get
    	  println(friendId1 +">>"+friendId2)
    	  delete("FriendsList/",friendId1).map{
    	    result => println(result.json)
    	  }
    	  delete("FriendsList/",friendId2)
    	  Redirect(routes.Application.index)
    	}
    )
  }
  def createWallPost=IsAuthenticated {  username => implicit request =>
    idForm.bindFromRequest.fold(
    	errors => BadRequest,
    	value =>{
    	  var params = request.body.asFormUrlEncoded.get
    	  var id = params.get("userId") match{
    			case Some(a) => a.head
    	  }
    	  var content = params.get("content") match{
    			case Some(a) => a.head
    	  }
    	  var postedBy = get("_User","{\"username\":\""+username+"\"}").map{
    	    result =>(result.json \ "results").as[List[JsObject]].head.as[User]
    	  }.await.get
    	  var postedTo = get("_User","{\"objectId\":\""+id+"\"}").map{
    	    result =>(result.json \ "results").as[List[JsObject]].head.as[User]
    	  }.await.get
    	  var data = Json.toJson(WallPost(content,postedTo.objectId,postedBy.objectId,null))
    	  post("Post",data).map{
    	    result => println(result.json)
    	  }
    	  Redirect(routes.Application.index)
    	}
    )
  }
  def editProfile = IsAuthenticated { username => implicit request =>
    Ok(views.html.edit_profile())
  }
  def post (className: String, data: JsValue)={
    WS.url("https://api.parse.com/1/classes/"+className).withHeaders("X-Parse-Application-Id" ->  "nu0BVvz9z6IQjHTr1ihno16q5tVZTWuD0IH4oaTI","X-Parse-REST-API-Key" -> "8vaHXeKVeVFuJa6ZqSedLHsv57OatWjgiegD3vTo","Content-Type"-> "application/json").post(data)
  }
  def delete (className: String, data: String)={
    WS.url("https://api.parse.com/1/classes/"+className+data).withHeaders("X-Parse-Application-Id" ->  "nu0BVvz9z6IQjHTr1ihno16q5tVZTWuD0IH4oaTI","X-Parse-REST-API-Key" -> "8vaHXeKVeVFuJa6ZqSedLHsv57OatWjgiegD3vTo").delete
  }
  def get (className: String, data: String)={
    WS.url("https://api.parse.com/1/classes/"+className+"?where="+URLEncoder.encode(data, "UTF-8")).withHeaders("X-Parse-Application-Id" ->  "nu0BVvz9z6IQjHTr1ihno16q5tVZTWuD0IH4oaTI","X-Parse-REST-API-Key" -> "8vaHXeKVeVFuJa6ZqSedLHsv57OatWjgiegD3vTo").get
  }
  def getLogIn (user: String, pass: String)={
    WS.url("https://api.parse.com/1/login?username="+URLEncoder.encode(user, "UTF-8")+"&password="+URLEncoder.encode(pass, "UTF-8")).withHeaders("X-Parse-Application-Id" ->  "nu0BVvz9z6IQjHTr1ihno16q5tVZTWuD0IH4oaTI","X-Parse-REST-API-Key" -> "8vaHXeKVeVFuJa6ZqSedLHsv57OatWjgiegD3vTo").get
  }
}