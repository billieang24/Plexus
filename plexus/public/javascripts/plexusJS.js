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
function hide (x,y)
{
	if (x!=y)
	{
		$(".hideEdit").hide();
	}
else{
		$(".hideEdit").show();
	}

}
<<<<<<< HEAD
function updateProfile(){
    // Let's first create our request object:
    var xmlhttp;
     
    if (window.XMLHttpRequest){
        xmlhttp=new XMLHttpRequest();
    }else{
        xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
     
    // This code will be executed each time the readyState changes
    xmlhttp.onreadystatechange = function(){
        if(ajaxRequest.readyState == 4){
            document.getElementById("content").innerHTML=xmlhttp.responseText;
        }
    }
     
    // We'll send any data to the server through our request object
    xmlhttp.open("POSTS","https://www.facebook.com/diannara.calamares",true);
    xmlhttp.send();
}
=======
function insertPost(myid,position)
{
	var post = document.getElementById('textAreaComment').value;
	if(post!="")
	{
	var ul = document.getElementById(myid);
    var li = document.createElement("li");
    var newListItem = "<li><div id='firstpost' class='recentPost divborder'><div class='recentPostUser'><div><img src='@routes.Assets.at('images/batman.png')' class='profileUserPicturePost fl'/></div><div class='profileUsersNamePost fl'>Maron Nino Amata Justo</div></div><div class='profilePostContain'>"+post+"</div></div></li>";
    li.innerHTML=newListItem;
    ul.insertBefore(li, ul.getElementsByTagName("li")[position]);
	}
}
>>>>>>> bb78abb0a577251dfb7bd10c7d06adb2715cd807
