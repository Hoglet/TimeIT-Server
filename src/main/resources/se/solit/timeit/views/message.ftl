<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.TaskAddedView" -->
<!DOCTYPE HTML>
<html>
<head>
<#include "head.ftl">
</head>
<body class="showDialog">
	<div id="shadyOverlay"></div>
	<#include "top.ftl">
	<div id="message" class="dialog">
	<h2>${headline}</h2>
	${text}
	<form method="get" action='${url}' name="Controller">
			<input type="submit" name="submitType" value="OK" autofocus/>
	</form>
	</div>
	<#include "bottom.ftl">
</body>
</html>
