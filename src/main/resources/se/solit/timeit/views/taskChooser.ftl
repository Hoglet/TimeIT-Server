<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.TaskView" -->
<!DOCTYPE HTML>
<html>
<head>
<#include "head.ftl">
</head>
<body>
	<#include "top.ftl">
	<div class="tabs"><a href="/"><div class="tab">
	<h3>Home</h3></div></a>
	<div class="tab selected">
	<#if isEditMode()>
		<h2>Edit</h2>
		<form method="GET" action='/task/edit' name="Controller">
	<#else>
		<h2>Delete</h2>
		<form method="POST" action='/task/delete' name="Controller">
	</#if>
	</div>
	</div>

	<div id="taskChooser" class="mainFrame">
		<p>
		Choose task: <select name="taskid">
			<#list tasks as entry>
				<option value="${entry.key}">${entry.value}</option>
			</#list>
			</select>
		</p>
		<p>
			<input type="submit" value="OK" />
		</p>
	</form>
	</div>
	<#include "bottom.ftl">
</body>
</html>
