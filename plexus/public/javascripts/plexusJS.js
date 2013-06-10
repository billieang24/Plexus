function popitup(url) {
		var newwindow=window.open(url,'Forgot Password','location=middle,height=600,width=600');
		if (window.focus) {newwindow.focus()}
		return false;
}
function changeTab(num1,num2){
var x = "#liTab"+num1;
var y = "#liTab"+num2;
$(x).attr("class", "active");
$(y).removeAttr("class");
<<<<<<< HEAD
}
=======
}
function changeTextFriendRequest()
{
	document.getElementById('friendRequestButton').innerHTML="Friend Request Sent";
}

>>>>>>> b97fcf2c0cea1fc5477687f40455158e9fd670e6
