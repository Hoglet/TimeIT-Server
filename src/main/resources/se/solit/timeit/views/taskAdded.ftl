<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.TaskAddedView" -->
<!DOCTYPE HTML>
<html>
<head>
<#include "head.ftl">
</head>
<body>
	<#include "top.ftl">

	<h1>Task added successfully</h1>
	<form method="get" action='/' name="Controller">
			<input type="submit" name="submitType" value="OK" />
	</form>
	<#include "bottom.ftl">
</body>
</html>