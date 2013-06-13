package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.ws.WS
import java.net.URLEncoder

object Auth extends Controller{
  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ).verifying ("Invalid email or password", result => result match {
        case (email, password) => check(email, password)
      })
  )
  def check(user: String, pass :String)={
    Application.getLogIn(user,pass).await.get.status==200
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
    Redirect(routes.Auth.login).withNewSession.flashing(
      "success" -> "You are now logged out.")
  }  
}
trait Secured {
  
  private def username(request: RequestHeader) = {
    request.session.get(Security.username)
  }
  
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Auth.login)
  
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }
}