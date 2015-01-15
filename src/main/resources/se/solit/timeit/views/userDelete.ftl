<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.UserEditView" -->
<!DOCTYPE HTML>
<html>
<head><#include "head.ftl">
</head>
<body>
	<#include "top.ftl">
	<div class="tabs"><a href="/user"><div class="tab"><h2>Users</h2></div></a><div class="tab selected"><h2>Delete</h2></div></div>

	<div id="userDelete" class="mainFrame">
	You are about to delete:
	<p>
	<form method="POST" action='/user/delete/${user.username}' name="Controller"  >
		<table>
			<tr>
				<td>Username: </td>
				<td>${user.username}</td>
			</tr>
			<tr>
				<td>Real name: </td>
				<td>${user.name}</td>
			</tr>
			<tr>
				<td>E-mail: </td>
				<td>${user.email}</td>
			</tr>
		</table>
		<p>
		<b>Are you really sure?</b><br>
			<input type="submit" name="submitType" value="Yes" />&nbsp; <a href="/user/"><button type="button">No</button></a>
		</p>
	</form>
	</p>
	</div>
	<#include "bottom.ftl">
</body>
</html>
