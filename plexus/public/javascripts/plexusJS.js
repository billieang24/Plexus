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
function changeTextFriendRequest(x,y)
{
	if(y==1)
	{
		document.getElementById('friendRequestButton').style.visibility="visible";
			if(x==1)
			{
				document.getElementById('friendRequestButton').innerHTML="Add as Friend";
				$('#friendRequestButton').attr("onClick", "addAsFriendButton()");
				$('#unfriendCancelButton').attr("onClick", "addAsFriendButton(1)");
			}
			else if(x==2)
			{
				document.getElementById('friendRequestButton').innerHTML="Friend";
				$('#friendRequestButton').attr("onClick", "friendButton()");
				document.getElementById('unfriendCancelButton').innerHTML="Unfriend";
				$('#unfriendCancelButton').attr("onClick", "friendButton(1)");
				$('#friendRequestButton').dropdown();
			}
	}
	else if(y==2)
	{
		document.getElementById('friendRequestButton').style.visibility="hidden";
	}
}
function addAsFriendButton(x)
{
	document.getElementById('friendRequestButton').innerHTML="Friend Request Sent";
	document.getElementById('unfriendCancelButton').innerHTML="Cancel Friend Request";
	$('#friendRequestButton').dropdown();
	if(x==1)
		changeTextFriendRequest(1,1);
}
function friendButton(x)
{
	$('#friendRequestButton').dropdown();
	if(x==1)
		changeTextFriendRequest(1,1);
}
function textAreaAdjust(o) {
    o.style.height = "47px";
    o.style.height = (o.scrollHeight)+"px";
}
function hide (userName1,userName2)
{
	if (userId1!=userId2)
	{
		$(".hideEdit").hide();
	}else{
		$(".hideEdit").show();
	}

}