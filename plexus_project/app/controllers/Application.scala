package controllers

import models.User
import models.Friend
import models.WallPost
import models.Comment
import models.FriendRequest
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.ws.WS
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.JsString
import plexus.deserializer.json.CommentDeserializer
import plexus.deserializer.json.UserDeserializer
import plexus.deserializer.json.FriendDeserializer
import plexus.deserializer.json.FriendRequestDeserializer
import plexus.deserializer.json.WallPostDeserializer
import scala.collection.mutable.ListBuffer
import java.net.URLEncoder
import models.FriendRequest
import models.FriendRequest
import models.WallPost
import java.util.concurrent.TimeUnit
import plexus.deserializer.json.CommentDeserializer

object Application extends Controller with Secured with UserDeserializer with FriendDeserializer with WallPostDeserializer with FriendRequestDeserializer with CommentDeserializer{
 
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
  val commentForm = Form(
      tuple(
      "userId"->text,
      "content"->text,
      "postId"->text
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
    		val params = request.body.asFormUrlEncoded.get
    		val email = params.get("email") match{
    			case Some(a) => a.head
    		}
    		val password = params.get("password") match{
    			case Some(a) => a.head
    		}
    		val firstName = params.get("firstName") match{
    			case Some(a) => a.head
    		}
    		val lastName = params.get("lastName") match{
    			case Some(a) => a.head
    		}
    		val gender = params.get("gender") match{
    			case Some(a) => a.head
    		}
    		val birthdate = params.get("birthdate") match{
    			case Some(a) => a.head
    		}
    		val user = User(email,password,firstName,lastName,gender,birthdate,null)
    		post("_User",Json.toJson(user))
    		Redirect(routes.Application.index)
    	}
  	)
  }
  def index = IsAuthenticated {  username => implicit request =>
        val friendsObjectIdList = ListBuffer[String]()
       	val friendsList = ListBuffer[User]()
       	val requestsObjectIdList = ListBuffer[String]()
       	val requestsList = ListBuffer[User]()
       	val user = getUser("username",username)
       	get("FriendsList?","{\"user\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+user.objectId+"\"}}").map{
       		result => (result.json \ "results").as[Seq[JsObject]].map{
       			friend => friendsObjectIdList += ((friend \ "friend").as[JsObject] \ "objectId").as[String]
       		}
       	}.await(20000, TimeUnit.MILLISECONDS ).get
       	friendsObjectIdList.map{
        	id => get("_User?","{\"objectId\":\""+id+"\"}").map{
       			result => friendsList += (result.json \ "results").as[List[JsObject]].head.as[User] 
       		}.await(20000, TimeUnit.MILLISECONDS ).get
       	}
       	get("FriendRequests?","{\"user\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+user.objectId+"\"}}").map{
       		result => (result.json \ "results").as[Seq[JsObject]].map{
       			request => requestsObjectIdList += ((request \ "requester").as[JsObject] \ "objectId").as[String]
       		}
       	}.await(20000, TimeUnit.MILLISECONDS ).get
       	requestsObjectIdList.map{
        	id => get("_User?","{\"objectId\":\""+id+"\"}").map{
       			result => requestsList += (result.json \ "results").as[List[JsObject]].head.as[User] 
       		}.await(20000, TimeUnit.MILLISECONDS ).get
       	}
       	Ok(views.html.home(user,friendsList,requestsList))
  }
  def userProfile() = IsAuthenticated { username => implicit request =>
    val user = getUser("username",username)
    val friendsObjectIdList = ListBuffer[String]()
    val commentsList = ListBuffer[Comment]()
    val friendsList = ListBuffer[User]()
    get("FriendsList?","{\"user\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+user.objectId+"\"}}").map{
      result => (result.json \ "results").as[Seq[JsObject]].map{
    	  friend => friendsObjectIdList += ((friend \ "friend").as[JsObject] \ "objectId").as[String]
      }
    }.await(20000, TimeUnit.MILLISECONDS ).get
   	friendsObjectIdList.map{
      id => get("_User?","{\"objectId\":\""+id+"\"}").map{
      	result => friendsList += (result.json \ "results").as[List[JsObject]].head.as[User] 
      }.await(20000, TimeUnit.MILLISECONDS ).get
    }
    val wallPostsObjectIdList = ListBuffer[WallPost]()
    	  get("WallPost?order=-createdAt&","{\"postedTo\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+user.objectId+"\"}}").map{
       		result => (result.json \ "results").as[Seq[JsObject]].map{
       			wallPost => wallPostsObjectIdList += wallPost.as[WallPost]
       		}
    	  }.await(20000, TimeUnit.MILLISECONDS ).get
    get("Comment?","{\"pageOwner\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+user.objectId+"\"}}").map{
       		result => (result.json \ "results").as[Seq[JsObject]].map{
       			comment => commentsList += comment.as[Comment]
       		}
    	  }.await(20000, TimeUnit.MILLISECONDS ).get
    Ok(views.html.profile(user, user, "owner", wallPostsObjectIdList, friendsList,commentsList))
  }
  def profile()=IsAuthenticated {  username => implicit request =>
    idForm.bindFromRequest.fold(
    	errors => BadRequest,
    	value =>{
    	  val params = request.body.asFormUrlEncoded.get
    	  val id = params.get("userId") match{
    			case Some(a) => a.head
    		}
    	  val user = getUser("username",username)
    	  val pageOwner = getUser("objectId",id)
    	  val friendsObjectIdList = ListBuffer[String]()
    	  val friendsList = ListBuffer[User]()
    	  val wallPostsList = ListBuffer[WallPost]()
    	  val commentsList = ListBuffer[Comment]()
    	  val role = {
    	    if(user.objectId.equals(id)) "owner"
    	    else if (
    	        !getFriendOrRequest(user.objectId,"FriendsList?","friend",id).map{
    	        	result => (result.json\"results").as[List[JsObject]].isEmpty
    	        }.await(20000, TimeUnit.MILLISECONDS ).get
    	    ) "friend"
    	    else if (
    	        getFriendOrRequest(id,"FriendRequests?","requester",user.objectId).map{
    	        	result => (result.json\"results").as[List[JsObject]].isEmpty
    	        }.await(20000, TimeUnit.MILLISECONDS ).get
    	    ) "none"
    	    else "friendRequestSent"
    	  }
    	  get("FriendsList?","{\"user\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+pageOwner.objectId+"\"}}").map{
       		result => (result.json \ "results").as[Seq[JsObject]].map{
       			friend => friendsObjectIdList += ((friend \ "friend").as[JsObject] \ "objectId").as[String]
       		}
    	  }.await(20000, TimeUnit.MILLISECONDS ).get
    	  friendsObjectIdList.map{
        	id => get("_User?","{\"objectId\":\""+id+"\"}").map{
       			result => friendsList += (result.json \ "results").as[List[JsObject]].head.as[User] 
       		}.await(20000, TimeUnit.MILLISECONDS ).get
    	  }
    	  get("WallPost?order=-createdAt&","{\"postedTo\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+id+"\"}}").map{
       		result => (result.json \ "results").as[Seq[JsObject]].map{
       			wallPost => wallPostsList += wallPost.as[WallPost]
       		}
    	  }.await(20000, TimeUnit.MILLISECONDS ).get
    	  get("Comment?","{\"pageOwner\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+id+"\"}}").map{
       		result => (result.json \ "results").as[Seq[JsObject]].map{
       			comment => commentsList += comment.as[Comment]
       		}
    	  }.await(20000, TimeUnit.MILLISECONDS ).get
    	  Ok(views.html.profile(pageOwner, user, role, wallPostsList, friendsList,commentsList))
    	}
    )
  }
  def search = IsAuthenticated {  username => implicit request =>
    searchForm.bindFromRequest.fold(
    formWithErrors =>  BadRequest,
    value =>{
      val keyword = request.body.asFormUrlEncoded.get.get("keyword")match{
    			case Some(a) => a.head
    		}
        get("_User?","{\"fullName\":{\"$regex\":\""+keyword+"\"}}").map{
        	result => 
        		val userList = ListBuffer[User]()
        		(result.json \ "results").as[Seq[JsObject]].map{
        			 entity=> userList += (entity).as[User]
        		}
        		Ok(views.html.search(userList))
        }.await(20000, TimeUnit.MILLISECONDS ).get
    }
    )
  }
  def sendFriendRequest=IsAuthenticated {  username => implicit request =>
    idForm.bindFromRequest.fold(
    	errors => BadRequest,
    	value =>{
    	  val params = request.body.asFormUrlEncoded.get
    	  val id = params.get("userId") match{
    			case Some(a) => a.head
    	  }
    	  val user = getUser("username",username)
    	  val data = Json.toJson(FriendRequest(user.objectId,id,null))
    	  post("FriendRequests",data)
    	  Redirect(routes.Application.index)
    	}
    )
  }
  def cancelFriendRequest=IsAuthenticated {  username => implicit request =>
    idForm.bindFromRequest.fold(
    	errors => BadRequest,
    	value =>{
    	  val params = request.body.asFormUrlEncoded.get
    	  val id = params.get("userId") match{
    			case Some(a) => a.head
    	  }
    	  val user = getUser("username",username)
    	  val requestObjectId = getFriendOrRequest(id,"FriendRequests?","requester",user.objectId).map{
       		result => (result.json \ "results").as[Seq[JsObject]].head.as[FriendRequest].objectId
    	  }.await(20000, TimeUnit.MILLISECONDS ).get
    	  delete("FriendRequests/",requestObjectId)
    	  Redirect(routes.Application.index)
    	}
    )
  }
  def ignoreFriendRequest=IsAuthenticated {  username => implicit request =>
    idForm.bindFromRequest.fold(
    	errors => BadRequest,
    	value =>{
    	  val params = request.body.asFormUrlEncoded.get
    	  val id = params.get("userId") match{
    			case Some(a) => a.head
    	  }
    	  val user = getUser("username",username)
    	  val requestObjectId = getFriendOrRequest(user.objectId,"FriendRequests?","requester",id).map{
       		result => (result.json \ "results").as[Seq[JsObject]].head.as[FriendRequest].objectId
    	  }.await(20000, TimeUnit.MILLISECONDS ).get
    	  delete("FriendRequests/",requestObjectId)
    	  Redirect(routes.Application.index)
    	}
    )
  }
  def acceptFriendRequest()=IsAuthenticated {  username => implicit request =>
    idForm.bindFromRequest.fold(
    	errors => BadRequest,
    	value =>{
    	  val params = request.body.asFormUrlEncoded.get
    	  val id = params.get("userId") match{
    			case Some(a) => a.head
    	  }
    	  val user = getUser("username",username)
    	  val data1 = Json.toJson(Friend(user.objectId,id,null))
    	  val data2 = Json.toJson(Friend(id,user.objectId,null))
    	  post("FriendsList",data1)
    	  post("FriendsList",data2)
    	  val requestObjectId = getFriendOrRequest(user.objectId,"FriendRequests?","requester",id).map{
       		result => (result.json \ "results").as[Seq[JsObject]].head.as[FriendRequest].objectId
    	  }.await(20000, TimeUnit.MILLISECONDS ).get
    	  delete("FriendRequests/",requestObjectId)
    	  Redirect(routes.Application.index)
    	}
    )
  }
  def unfriend = IsAuthenticated {  username => implicit request =>
    idForm.bindFromRequest.fold(
    	errors => BadRequest,
    	value =>{
    	  val params = request.body.asFormUrlEncoded.get
    	  val id = params.get("userId") match{
    			case Some(a) => a.head
    	  }
    	  val user = getUser("username",username)
    	  val friendId1 = getFriendOrRequest(user.objectId,"FriendsList?","friend",id).map{
       		result => (result.json \ "results").as[Seq[JsObject]].head.as[Friend].objectId
    	  }.await(20000, TimeUnit.MILLISECONDS ).get
    	  val friendId2 = getFriendOrRequest(id,"FriendsList?","friend",user.objectId).map{
       		result => (result.json \ "results").as[Seq[JsObject]].head.as[Friend].objectId
    	  }.await(20000, TimeUnit.MILLISECONDS ).get
    	  delete("FriendsList/",friendId1)
    	  delete("FriendsList/",friendId2)
    	  Redirect(routes.Application.index)
    	}
    )
  }
  def createWallPost=IsAuthenticated {  username => implicit request =>
    postForm.bindFromRequest.fold(
    	errors => BadRequest,
    	value =>{
    	  val params = request.body.asFormUrlEncoded.get
    	  val id = params.get("userId") match{
    			case Some(a) => a.head
    	  }
    	  val content = params.get("content") match{
    			case Some(a) => a.head
    	  }
    	  val postedBy = getUser("username",username)
    	  val postedTo = getUser("objectId",id)
    	  val data = Json.toJson(WallPost(content,postedTo,postedBy,null))
    	  post("WallPost?",data)
    	  Redirect(routes.Application.index)
    	}
    )
  }
  def deleteWallPost = IsAuthenticated {  username => implicit request =>
    idForm.bindFromRequest.fold(
    	errors => BadRequest,
    	value =>{
    	  val params = request.body.asFormUrlEncoded.get
    	  val id = params.get("userId") match{
    			case Some(a) => a.head
    	  }
    	  delete("WallPost/",id)
    	  Redirect(routes.Application.index)
    	}
    )
  }
  def addComment = IsAuthenticated {  username => implicit request =>
    commentForm.bindFromRequest.fold(
    	errors => BadRequest,
    	value =>{
    	  val params = request.body.asFormUrlEncoded.get
    	  val id = params.get("userId") match{
    			case Some(a) => a.head
    	  }
    	  val postId = params.get("postId") match{
    			case Some(a) => a.head
    	  }
    	  val content = params.get("content") match{
    			case Some(a) => a.head
    	  }
    	  val user = getUser("username",username)
    	  val pageOwner = getUser("objectId",id)
    	  val wallPost = get("WallPost?","{\"objectId\":\""+postId+"\"}").map{
       		result => (result.json \ "results").as[Seq[JsObject]].head.as[WallPost]
    	  }.await(20000, TimeUnit.MILLISECONDS ).get
    	  val data = Json.toJson(Comment(content,pageOwner,user,wallPost,null))
    	  post("Comment?",data)
    	  Redirect(routes.Application.index)
    	}
    )
  }
  def friends = IsAuthenticated {  username => implicit request =>
    val friendsObjectIdList = ListBuffer[String]()
    val friendsList = ListBuffer[User]()
    val user = getUser("username",username)
    get("FriendsList?","{\"user\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+user.objectId+"\"}}").map{
    	result => (result.json \ "results").as[Seq[JsObject]].map{
       		friend => friendsObjectIdList += ((friend \ "friend").as[JsObject] \ "objectId").as[String]
       	}
    }.await(20000, TimeUnit.MILLISECONDS ).get
    friendsObjectIdList.map{
    	id => get("_User?","{\"objectId\":\""+id+"\"}").map{
       		result => friendsList += (result.json \ "results").as[List[JsObject]].head.as[User] 
       	}.await(20000, TimeUnit.MILLISECONDS ).get
    }      	
    Ok(views.html.friends(friendsList,"owner"))
  }
  def othersFriends = IsAuthenticated {  username => implicit request =>
     idForm.bindFromRequest.fold(
    	errors => BadRequest,
    	value =>{
    	  val friendsObjectIdList = ListBuffer[String]()
    	  val friendsList = ListBuffer[User]()
    	  val params = request.body.asFormUrlEncoded.get
    	  val id = params.get("userId") match{
    			case Some(a) => a.head
    	  }
    	  val user = getUser("username",username)
    	  val role = if(user.objectId.equals(id)) "owner" else "none"
    	  get("FriendsList?","{\"user\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+id+"\"}}").map{
    		  result => (result.json \ "results").as[Seq[JsObject]].map{
    			  friend => friendsObjectIdList += ((friend \ "friend").as[JsObject] \ "objectId").as[String]
    		  }
    	  }.await(20000, TimeUnit.MILLISECONDS ).get
    	  friendsObjectIdList.map{
    		  id => get("_User?","{\"objectId\":\""+id+"\"}").map{
    			  result => friendsList += (result.json \ "results").as[List[JsObject]].head.as[User] 
    		  }.await(20000, TimeUnit.MILLISECONDS ).get
    	  }
    	  Ok(views.html.friends(friendsList,role))
    	}
    )
  }
  def about = IsAuthenticated  { username => implicit request =>
    val user = getUser("username",username)
    Ok(views.html.about(user,"owner"))
  }
  def aboutOthers = IsAuthenticated  { username => implicit request =>
     idForm.bindFromRequest.fold(
    	errors => BadRequest,
    	value =>{
    	  val params = request.body.asFormUrlEncoded.get
    	  val id = params.get("userId") match{
    			case Some(a) => a.head
    	  }
    	  val user = getUser("objectId",id)
    	  Ok(views.html.about(user,"none"))
    	}
     )
  }
  def post (className: String, data: JsValue)={
    WS.url("https://api.parse.com/1/classes/"+className).withHeaders("X-Parse-Application-Id" ->  "nu0BVvz9z6IQjHTr1ihno16q5tVZTWuD0IH4oaTI","X-Parse-REST-API-Key" -> "8vaHXeKVeVFuJa6ZqSedLHsv57OatWjgiegD3vTo","Content-Type"-> "application/json").post(data)
  }
  def delete (className: String, data: String)={
    WS.url("https://api.parse.com/1/classes/"+className+data).withHeaders("X-Parse-Application-Id" ->  "nu0BVvz9z6IQjHTr1ihno16q5tVZTWuD0IH4oaTI","X-Parse-REST-API-Key" -> "8vaHXeKVeVFuJa6ZqSedLHsv57OatWjgiegD3vTo").delete
  }
  def get (className: String, data: String)={
    WS.url("https://api.parse.com/1/classes/"+className+"where="+URLEncoder.encode(data, "UTF-8")).withHeaders("X-Parse-Application-Id" ->  "nu0BVvz9z6IQjHTr1ihno16q5tVZTWuD0IH4oaTI","X-Parse-REST-API-Key" -> "8vaHXeKVeVFuJa6ZqSedLHsv57OatWjgiegD3vTo").get
  }
  def getFriendOrRequest (userId: String, className: String, parameter:String, parameterId: String) ={
    get(className,"{\"user\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+userId+"\"},\""+parameter+"\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+parameterId+"\"}}")
  }
  def getUser(parameter: String, data: String)={
    get("_User?","{\""+parameter+"\":\""+data+"\"}").map{
       		result =>(result.json \ "results").as[List[JsObject]].head.as[User] 
       	}.await(20000, TimeUnit.MILLISECONDS ).get
  }
  def getLogIn (user: String, pass: String)={
    WS.url("https://api.parse.com/1/login?username="+URLEncoder.encode(user, "UTF-8")+"&password="+URLEncoder.encode(pass, "UTF-8")).withHeaders("X-Parse-Application-Id" ->  "nu0BVvz9z6IQjHTr1ihno16q5tVZTWuD0IH4oaTI","X-Parse-REST-API-Key" -> "8vaHXeKVeVFuJa6ZqSedLHsv57OatWjgiegD3vTo").get
  }
}