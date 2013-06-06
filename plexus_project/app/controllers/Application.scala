package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.User

object Application extends Controller with Secured{
  val userForm = Form(
      tuple(
	  "username" -> nonEmptyText,
	  "password" -> nonEmptyText)
  )
  val loginForm = Form(
    tuple(
      "username" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => User.authenticate(email, password).isDefined
    })
  )
  def index = IsAuthenticated { username => _ =>
    User.findByEmail(username).map { user =>
        println("oo")
      Ok(
        views.html.Home(user)
      )
    }.getOrElse(Forbidden)
  }
  def login = Action { implicit request =>
    Ok(views.html.Main(loginForm))
  }
  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You are now logged out.")
  }
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.Main(formWithErrors)),
      user => {
        println("oo")
        Redirect(routes.Application.index).withSession("username" -> user._1)
      }
    )
  }
  def signUp = Action {
    Ok(views.html.SignUp(""))
  }
  def newUser = Action { implicit request =>
  	userForm.bindFromRequest.fold(
    formWithErrors => {
      BadRequest
    },
    value => {
    	var params = request.body.asFormUrlEncoded.get
    	var user = params.get("username") match{
    		case Some(a) => a.head
    	}
    	var pass = params.get("password") match{
    		case Some(a) => a.head
    	}
    	var repass = params.get("repassword") match{
    		case Some(a) => a.head
    	}
    	var date = params.get("date") match{
    		case Some(a) => a.head
    	}
    	var year = params.get("year") match{
    		case Some(a) => a.head
    	}
    	var given = params.get("givenname") match{
    		case Some(a) => a.head
    	}
    	var last = params.get("lastname") match{
    		case Some(a) => a.head
    	}
    	var gen = params.get("gender") match{
    		case Some(a) => a.head
    	}
    	var month = {
    	  (params.get("month") match{
    	  	case Some(a) => a.head
    	  	case None => null
    	  }) match{
    	    case "January"=>1
    	    case "February"=>2
    	    case "March"=>3
    	    case "April"=>4
    	    case "May"=>5
    	    case "June"=>6
    	    case "July"=>7
    	    case "August"=>8
    	    case "September"=>9
    	    case "October"=>10
    	    case "November"=>11
    	    case "December"=>12   	    
    	  }
    	}
    	var add = params.get("address")match{
    		case Some(a) => a.head
    	}
    	if(!repass.equals(pass))
    	 	Ok(views.html.SignUp("Password confirmation error"))
    	else if((month==4||month==6||month==9||month==11)&&date.toInt>30)
    		Ok(views.html.SignUp("Invalid date"))
    	else if(month==2 && year.toInt%4==0 && date.toInt>29)
    		Ok(views.html.SignUp("Invalid date"))
    	else if(month==2 &&	date.toInt>28)
    		Ok(views.html.SignUp("Invalid date"))
    	else{
    		User.create(
    			user,
    			pass,
    			given,
    			last,
    			gen,
    			month.toString,
    			date,
    			year,
    			add)
    		Redirect(routes.Application.index)
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