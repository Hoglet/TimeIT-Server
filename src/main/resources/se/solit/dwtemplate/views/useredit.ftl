<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.UserEditView" -->
<!DOCTYPE HTML>
<head><#include "head.ftl">
</head>
<html>
<body>
	<#include "top.ftl">

	<h1>Edit settings for user ${user.username}</h1>
	<form method="POST" action='/admin/user/edit' name="Controller">
		<input type="hidden" name="userName" value="${user.username}"/>
		<table>
			<tr>
				<td>Real name</td>
				<td><input type="text" name="name" value="${user.name}" /></td>
			</tr>
			<tr>
				<td>Password</td>
				<td><input type="password" name="password" value="${user.password}" /></td>
			</tr>
			<tr>
				<td>E-mail</td>
				<td><input type="text" name="email" value="${user.email}" /></td>
			</tr>

		</table>

		<p>
			<input type="submit" name="editUser" value="save" />&nbsp; <input
				type="submit" name="cancel" value="cancel" />&nbsp;
		</p>
	</form>
	<#include "bottom.ftl">
</body>
</html>