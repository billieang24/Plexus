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
>>>>>>> 8ef1cca716ac3fe81fd7eb4f4322b48c251fc809
