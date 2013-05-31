package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {
  
  public static Result main() {
    return ok(MainPage.render());
  }
  public static Result forgotPassword() {
	    return ok(ForgotPasswordPage.render());
  }
  public static Result signUp() {
	    return ok(SignupPage.render());
  }
  public static Result profile() {
		return ok(ProfilePage.render());
  }
	  
}