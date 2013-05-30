function popitup(url) {
		var newwindow=window.open(url,'Forgot Password','location=middle,height=600,width=600');
		if (window.focus) {newwindow.focus()}
		return false;
}