package controllers;

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
	  return ok(SignupPage.render());
  }
  public static Result profile() {
		return ok(ProfilePage.render());
  }
  public static Result homePage() {
		return ok(homePage.render());
  }
  public static Result verificationPage() {
		return ok(VerificationPage.render());
  }
  public static Result addFriend() {
		return ok(AddFriend.render());
  }
  public static Result uploadPhotoPage() {
		return ok(PhotoUploadPage.render());
  }
}
