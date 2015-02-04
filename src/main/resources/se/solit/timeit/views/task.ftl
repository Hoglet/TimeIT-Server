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
	<#if edit()>
		<h2>Edit task</h2>
		<form method="POST" action='/task/edit' name="Controller">
	<#else>
		<h2>Add task</h2>
		<form method="POST" action='/task/add' name="Controller">
	</#if>
	</div>
	</div>

	<div id="task" class="mainFrame">
		<input type="hidden" name="taskid" value="${task.getID().toString()}"/>
		<table>
			<tr>
				<td>Name:</td>
				<td><input type="text" name="name" value="${task.name}" /></td>
			</tr>
			<tr>
				<td>
				Parent:
				</td>
				<td>
				<select name="parent">
					<#if task.parent??>
			      		<option value="">-</option>
			      	<#else>
			      		<option value="" selected>-</option>
			      	</#if>
		      	<#list parents as item>
		      		<#if task.parent??>
		      			<#if task.parent.ID == item.id>
		           			<option value="${item.id}" selected>${item.indentString}${item.name}</option>
		      			<#else>
		      				<option value="${item.id}">${item.indentString}${item.name}</option>
		      			</#if>
		      		<#else>
		      			<option value="${item.id}">${item.indentString}${item.name}</option>
		      		</#if>
		      	</#list>
		      	</select>
		      	</td>
			</tr>
		</table>

		<p>
			<input type="submit" name="submitType" value="save" /></form>
		</p>
		</div>
	<#include "bottom.ftl">
</body>
</html>
