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
  def index = Action {
	  Redirect(routes.Application.users)
  }
  
  def users = Action {
	Ok(views.html.index(User.all(), userForm))
  }
  
  def newUser = Action { implicit request =>
  	userForm.bindFromRequest.fold(
    formWithErrors => BadRequest(views.html.index(User.all(), formWithErrors)),
    value => {
    var params = request.body.asFormUrlEncoded.get
    var u = params.get("username")
    var p = params.get("password")

    println("username >>>> " + u)
    println("passqwurdd >>>> " + p)
    //    User.create(
//        u match {
//        	case Some(List(a)) => a.toString
//        },
//        p match{
//            case Some(List(a)) => a.toString
//        }
//    )
    Redirect(routes.Application.users)
    }
  	)
  }
  def deleteUser(id: Long) = Action {
	  User.delete(id)
	  Redirect(routes.Application.users)
  }
}