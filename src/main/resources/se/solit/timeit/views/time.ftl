<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.TimeView" -->
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
		<h2>Add time</h2>
	</div>
	</div>

	<div id="time" class="mainFrame">
		<form method="POST" action='/time/add' name="Controller">
		<#assign date=startDate>
		<#assign start=startTime>
		<#assign stop=stopTime>
		<input type="hidden" name="timeid" value="${time.getID()}"/>
		<table>
			<tr>
				<td>
				Task:
				</td>
				<td>
					<select name="taskid">
					<#list tasks as task>
						<option value="${task.id}">${task.indentString}${task.name}</option>
					</#list>
					</select>
		      	</td>
			</tr>
			<tr>
				<td>Date:</td>
				<td><input type="date" name="date" value="${date}"  size="12" required="required"/> (YYYY-mm-dd)</td>
			</tr>
			<tr>
				<td>Start:</td>
				<td><input type="text" name="start" value="${start}" size="12" pattern="[0-2][0-9]\:[0-5][0-9]"  required="required"/> (HH:mm) </td>
			</tr>
			<tr>
				<td>Stop:</td>
				<td><input type="text" name="stop" value="${stop}"  size="12" pattern="[0-2][0-9]\:[0-5][0-9]" required="required"/> (HH:mm) </td>
			</tr>
		</table>

		<p>
			<input type="submit" name="submitType" value="save" /></form>
		</p>
		</div>
	<#include "bottom.ftl">
</body>
</html>
