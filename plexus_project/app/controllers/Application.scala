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

object Application extends Controller with Secured {
 
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
  val searchForm = Form(
      "keyword"->text
  )
  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ).verifying ("Invalid email or password", result => result match {
        case (email, password) => check(email, password).await.get.status==200
      })
  )
  def check(user: String, pass :String)={
    WS.url("https://api.parse.com/1/login?username="+URLEncoder.encode(user, "UTF-8")+"&password="+URLEncoder.encode(pass, "UTF-8")).withHeaders("X-Parse-Application-Id" ->  "nu0BVvz9z6IQjHTr1ihno16q5tVZTWuD0IH4oaTI","X-Parse-REST-API-Key" -> "8vaHXeKVeVFuJa6ZqSedLHsv57OatWjgiegD3vTo").get
  }
  def index = IsAuthenticated {  username =>
        implicit request =>
        Async{ //TODO decide what to pass to a homepage
        	
        	/*WS.url("https://api.parse.com/1/classes/FriendsList?where="+URLEncoder.encode("{\"user\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\"pLaWkUq7pK\"}}", "UTF-8")).withHeaders("X-Parse-Application-Id" ->  "nu0BVvz9z6IQjHTr1ihno16q5tVZTWuD0IH4oaTI","X-Parse-REST-API-Key" -> "8vaHXeKVeVFuJa6ZqSedLHsv57OatWjgiegD3vTo").get.map{
        		result => (result.json \ "results").as[List[JsObject]].map{
        		  friend => println(friend)
        		}
        	}*/
        	
        	WS.url("https://api.parse.com/1/classes/_User?where="+URLEncoder.encode("{\"username\":\""+username+"\"}", "UTF-8")).withHeaders("X-Parse-Application-Id" ->  "nu0BVvz9z6IQjHTr1ihno16q5tVZTWuD0IH4oaTI","X-Parse-REST-API-Key" -> "8vaHXeKVeVFuJa6ZqSedLHsv57OatWjgiegD3vTo").get.map{
        		result => Ok(views.html.home(User.convertToUserMap((result.json \ "results").as[List[JsObject]].head))) 
        	}        
        }
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
    		var customerInfo = JsObject(Seq(("username"->JsString(email)),("email"->JsString(email)),("password"->JsString(password)),("firstName"->JsString(firstName)),("lastName"->JsString(lastName)),("gender"->JsString(gender)),("fullName"->JsString((firstName.toLowerCase()+lastName.toLowerCase()).replaceAll(" ", ""))),("birthdate"->JsObject(Seq("__type"->JsString("Date"),"iso"->JsString(birthdate+"T18:02:52.249Z"))))))
    		WS.url("https://api.parse.com/1/classes/_User").withHeaders("X-Parse-Application-Id" ->  "nu0BVvz9z6IQjHTr1ihno16q5tVZTWuD0IH4oaTI","X-Parse-REST-API-Key" -> "8vaHXeKVeVFuJa6ZqSedLHsv57OatWjgiegD3vTo","Content-Type"-> "application/json").post(customerInfo)
    		Redirect(routes.Application.index)
    	}
  	)
  }
  def profile(id:String)=IsAuthenticated {  username => implicit request =>
    Ok(id)
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
        		  println((result.json \ "results").as[List[JsObject]])
        		  val BranchInformation = ListBuffer[List[String]]()
        		
        		 (result.json \ "results").as[Seq[JsObject]].map{
        			 entity=>
        			 	BranchInformation += User.convertToUserMap(entity)
        		 	}
        		  Ok(views.html.searchy(BranchInformation))
      
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