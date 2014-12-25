<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.TaskView" -->
<!DOCTYPE HTML>
<html>
<head>
<#include "head.ftl">
</head>
<body>
	<#include "top.ftl">

	<h1>Choose task to edit</h1>
	<form method="GET" action='/task/edit' name="Controller">
		<p>
			<select name="taskid">
			<#list tasks as entry>
				<option value="${entry.key}">${entry.value}</option>
			</#list>
			</select>
		</p>
		<p>
			<input type="submit" value="OK" />
		</p>
	</form>
	<#include "bottom.ftl">
</body>
</html>
