package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.User

object Application extends Controller {
  val userForm = Form(
      tuple(
	  "username" -> nonEmptyText,
	  "password" -> nonEmptyText)
  )
  val logInForm = Form(
      tuple(
	  "username" -> nonEmptyText,
	  "password" -> nonEmptyText)
  )
  def main = Action {
    Ok(views.html.Main())
  }
  def signUp = Action {
    Ok(views.html.SignUp(""))
  }
  def logIn = Action {
    implicit request =>
  	logInForm.bindFromRequest.fold(
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
    	if(User.validLogIn(user, pass).size==1)
    		Ok(views.html.SignUp("ok"))
    	else
    		Ok(views.html.SignUp("no"))
    }
    )
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
    		Redirect(routes.Application.main)
    	}
    }
  	)
  }
}