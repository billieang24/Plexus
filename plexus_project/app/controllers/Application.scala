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
  def main = Action {
    Ok(views.html.Main())
  }
  def signUp = Action {
    println("get")
    Ok(views.html.SignUp(""))
  }
  def newUser = Action { implicit request =>
  	userForm.bindFromRequest.fold(
    formWithErrors => BadRequest,
    value => {
    	var params = request.body.asFormUrlEncoded.get
    	var u = params.get("username")
    	var p = params.get("password")
    	var p2 = params.get("password2")
    	if(!(p2 match {case Some(a) => a.head}).equals(
    			p match{
    				case Some(a) => a.head
    			}))
    	{
    		println("post")
    	 	Ok(views.html.SignUp("Password confirmation error"))
    	}
    	else{
    		User.create(
    			u match {
    				case Some(a) => a.head
    			},
    			p match{
    				case Some(a) => a.head
    			}
    	)
    	Redirect(routes.Application.main)
    	}
    }
  	)
  }
}