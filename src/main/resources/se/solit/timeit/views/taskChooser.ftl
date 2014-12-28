<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.TaskView" -->
<!DOCTYPE HTML>
<html>
<head>
<#include "head.ftl">
</head>
<body>
	<#include "top.ftl">
	<#if isEditMode()>
		<h1>Choose task to edit</h1>
		<form method="GET" action='/task/edit' name="Controller">
	<#else>
		<h1>Choose task to delete</h1>
		<form method="POST" action='/task/delete' name="Controller">
	</#if>
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
