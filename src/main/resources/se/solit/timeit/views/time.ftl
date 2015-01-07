<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.TimeView" -->
<!DOCTYPE HTML>
<html>
<head>
<#include "head.ftl">
</head>
<body>
	<#include "top.ftl">
	<div id="time" class="mainFrame">
		<h1>Add time</h1>
		<form method="POST" action='/time/add' name="Controller">
		<#assign date=time.start.toString('yyyy-MM-dd')>
		<#assign start=time.start.toString('hh:mm')>
		<#assign stop=time.stop.toString('hh:mm')>
		<input type="hidden" name="timeid" value="${time.getID()}"/>
		<table>
			<tr>
			<td>id:</td><td id="timeid">${time.getID()}</td>
			</tr>
			<tr>
				<td>
				Task:
				</td>
				<td>
					<select name="taskid">
					<#list tasks as entry>
						<option value="${entry.key}">${entry.value}</option>
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
				<td><input type="text" name="start" value="${start}" size="12" pattern="[0-2][1-9]\:[0-5][0-9]"  required="required"/> (HH:mm) </td>
			</tr>
			<tr>
				<td>Stop:</td>
				<td><input type="text" name="stop" value="${stop}"  size="12" pattern="[0-2][1-9]\:[0-5][0-9]" required="required"/> (HH:mm) </td>
			</tr>
		</table>

		<p>
			<input type="submit" name="submitType" value="save" /></form>
		</p>
		</div>
	<#include "bottom.ftl">
</body>
</html>
