<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.UserEditView" -->
<!DOCTYPE HTML>
<html>
<head><#include "head.ftl">
</head>
<body>
	<#include "top.ftl">
	<div id="userDelete" class="mainFrame">
	<h1>You are about to delete:</h1>

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
			<input type="submit" name="submitType" value="Yes" />&nbsp; <a href="/admin/"><button type="button">No</button></a>
		</p>
	</form>
	</div>
	<#include "bottom.ftl">
</body>
</html>
