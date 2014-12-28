<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.TaskAddedView" -->
<!DOCTYPE HTML>
<html>
<head>
<#include "head.ftl">
</head>
<body>
	<#include "top.ftl">

	<h1>${headline}</h1>
	${text}
	<form method="get" action='${url}' name="Controller">
			<input type="submit" name="submitType" value="OK" />
	</form>
	<#include "bottom.ftl">
</body>
</html>
