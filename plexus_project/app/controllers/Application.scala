package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.User
import play.api.libs.ws.WS
import play.api.libs.json
import play.api.libs.json.Format
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.JsArray
import play.api.libs.json.JsString
import java.net.URLEncoder
import scala.collection.mutable.ListBuffer
import plexus.deserializer.json.UserDeserializer
import play.api.libs.concurrent.Akka
import play.api.Play.current
import views.html.defaultpages.badRequest
object Application extends Controller with Secured with UserDeserializer{
 
  val userForm = Form( //NO VALIDATIONS YET !!!!
      tuple(
      "email" -> text,
          "password" -> nonEmptyText,
          "firstName" -> text,
          "lastName" -> text,
          "gender" -> text,
          "birthdate"-> date
      )
  )
  val idForm = Form(
      "id"->text
  )
  val searchForm = Form(
      "keyword"->text
  )
  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ).verifying ("Invalid email or password", result => result match {
        case (email, password) => check(email, password)
      })
  )
  def check(user: String, pass :String)={
    WS.url("https://api.parse.com/1/login?username="+URLEncoder.encode(user, "UTF-8")+"&password="+URLEncoder.encode(pass, "UTF-8")).withHeaders("X-Parse-Application-Id" ->  "nu0BVvz9z6IQjHTr1ihno16q5tVZTWuD0IH4oaTI","X-Parse-REST-API-Key" -> "8vaHXeKVeVFuJa6ZqSedLHsv57OatWjgiegD3vTo").get.await.get.status==200
  }
  def index = IsAuthenticated {  username =>
        implicit request =>
        	val objectIdList = ListBuffer[String]()
        	val friendsList = ListBuffer[User]()
        	var user = WS.url("https://api.parse.com/1/classes/_User?where="+URLEncoder.encode("{\"username\":\""+username+"\"}", "UTF-8")).withHeaders("X-Parse-Application-Id" ->  "nu0BVvz9z6IQjHTr1ihno16q5tVZTWuD0IH4oaTI","X-Parse-REST-API-Key" -> "8vaHXeKVeVFuJa6ZqSedLHsv57OatWjgiegD3vTo").get.map{
        		result =>(result.json \ "results").as[List[JsObject]].head.as[User] 
        	}
        	WS.url("https://api.parse.com/1/classes/FriendsList?where="+URLEncoder.encode("{\"user\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+user.await.get.objectId+"\"}}", "UTF-8")).withHeaders("X-Parse-Application-Id" ->  "nu0BVvz9z6IQjHTr1ihno16q5tVZTWuD0IH4oaTI","X-Parse-REST-API-Key" -> "8vaHXeKVeVFuJa6ZqSedLHsv57OatWjgiegD3vTo").get.map{
        		results => 
        			(results.json \ "results").as[Seq[JsObject]].map{
        				friend => objectIdList += ((friend \ "friend").as[JsObject] \ "objectId").as[String]
        		 	}
        	}.await.get
        			objectIdList.map{
        				id => WS.url("https://api.parse.com/1/classes/_User?where="+URLEncoder.encode("{\"objectId\":\""+id+"\"}", "UTF-8")).withHeaders("X-Parse-Application-Id" ->  "nu0BVvz9z6IQjHTr1ihno16q5tVZTWuD0IH4oaTI","X-Parse-REST-API-Key" -> "8vaHXeKVeVFuJa6ZqSedLHsv57OatWjgiegD3vTo").get.map{
        					result => 
        						println(result.json)
        						friendsList += (result.json \ "results").as[List[JsObject]].head.as[User] 
        				}.await.get
        			}
        			println("object id >>>>>"+objectIdList)
        			println("user >>>>>"+friendsList)
        			Ok(views.html.home(user.await.get,friendsList))
  }
  def login = Action { implicit request =>
    Ok(views.html.index(loginForm))
  }
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index(formWithErrors)),
      user => Redirect(routes.Application.index).withSession("username" -> user._1)
    )
  }
  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You are now logged out.")
  }
  def signUp = Action {
    Ok(views.html.sign_up(userForm))
  }
  def newUser = Action { implicit request =>
    userForm.bindFromRequest.fold(
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
    		WS.url("https://api.parse.com/1/classes/_User").withHeaders("X-Parse-Application-Id" ->  "nu0BVvz9z6IQjHTr1ihno16q5tVZTWuD0IH4oaTI","X-Parse-REST-API-Key" -> "8vaHXeKVeVFuJa6ZqSedLHsv57OatWjgiegD3vTo","Content-Type"-> "application/json").post(Json.toJson(user))
    		Redirect(routes.Application.index)
    	}
  	)
  }
  def profile()=IsAuthenticated {  username => implicit request =>
    idForm.bindFromRequest.fold(
    	errors => BadRequest,
    	value =>{
    	  var params = request.body.asFormUrlEncoded.get
    	  var id = params.get("id") match{
    			case Some(a) => a.head
    		}
    	  Ok(id)
    	}
    )
  }
  def search = IsAuthenticated {  username =>
    implicit request =>
    searchForm.bindFromRequest.fold(
    formWithErrors =>  BadRequest,
    value =>{
      var keyword = request.body.asFormUrlEncoded.get.get("keyword")match{
    			case Some(a) => a.head
    		}
      Async{     
        WS.url("https://api.parse.com/1/classes/_User?where="+URLEncoder.encode("{\"fullName\":{\"$regex\":\""+keyword+"\"}}", "UTF-8")).withHeaders("X-Parse-Application-Id" ->  "nu0BVvz9z6IQjHTr1ihno16q5tVZTWuD0IH4oaTI","X-Parse-REST-API-Key" -> "8vaHXeKVeVFuJa6ZqSedLHsv57OatWjgiegD3vTo").get.map{
        		result => 
        		  val userList = ListBuffer[User]()
        		 (result.json \ "results").as[Seq[JsObject]].map{
        			 entity=> userList += (entity).as[User]
        		 	}
        		  Ok(views.html.searchy(userList))
      
        }
      }
    }
    )
  }
}
trait Secured {
  
  private def username(request: RequestHeader) = {
    request.session.get(Security.username)
  }
  
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login)
  
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }
}