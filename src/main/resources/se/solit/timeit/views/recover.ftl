<#-- @ftlvariable name="" type="se.solit.timeit.views.RecoverView" -->
<!DOCTYPE HTML>
<html>
<head>
<#include "head.ftl">
</head>
<body id="landingPage">
	<#include "top.ftl">
    <div class="mainFrame">
    <H1>Recovering password</H1>
    <p>Please enter your mail address and the credentials for that account will be sent to you.</p>

	<form method="POST" action='/recover' name="Controller">
	<input type="text" name="address" value=""  size="30" required="required"/>
	<input type="submit" value="submit"/>
	</form>
	</div>
</body>
</html>
