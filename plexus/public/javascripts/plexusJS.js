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
}
function changeTextFriendRequest(x)
{
	if(x==0)
	{
		document.getElementById('friendRequestButton').innerHTML="Friend Request Sent";
		$('.friendRequestButton').dropdown();
	}
	else if(x==1)
		document.getElementById('friendRequestButton').innerHTML="Add as friend";
}
