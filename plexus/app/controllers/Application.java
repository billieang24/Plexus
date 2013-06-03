package controllers;

import models.Bar;
import play.*;
import play.api.data.Form;
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
	  Bar bar = Form.form(Bar.class).bindFromRequest().get();
	  bar.save();
	  return redirect(MainPage.render());
  }
  public static Result profile() {
		return ok(ProfilePage.render());
  }
}
