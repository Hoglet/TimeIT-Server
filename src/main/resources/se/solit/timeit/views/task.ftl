<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.TaskView" -->
<!DOCTYPE HTML>
<html>
<head>
<#include "head.ftl">
</head>
<body>
	<#include "top.ftl">
	<div id="task" class="mainFrame">
	<#if edit()>
		<h1>Edit task</h1>
		<form method="POST" action='/task/edit' name="Controller">
	<#else>
		<h1>Add task</h1>
		<form method="POST" action='/task/add' name="Controller">
	</#if>
		<input type="hidden" name="taskid" value="${task.getID().toString()}"/>
		<table>
			<tr>
			<td>id:</td><td id="taskid">${task.getID().toString()}</td>
			</tr>
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

		      	<#list parents as entry>
		      		<#if task.parent??>
		      			<#if task.parent.ID == entry.key>
		           			<option value="${entry.key}" selected>${entry.value}</option>
		      			<#else>
		      				<option value="${entry.key}">${entry.value}</option>
		      			</#if>
		      		<#else>
		      			<option value="${entry.key}">${entry.value}</option>
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
