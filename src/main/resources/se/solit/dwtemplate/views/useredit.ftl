<#-- @ftlvariable name=""
type="se.solit.dwtemplate.resources.LandingView" -->
<!DOCTYPE HTML>
<head><#include "head.ftl">
</head>
<html>
<body>
	<#include "top.ftl">
	<h1>Edit user</h1>
	<form method="POST" action='/admin/user/edit' name="Controller">
		<table>
			<tr>
				<td>Real name</td>
				<td><input type="text" name="name" value="Jan Jansons" /></td>
			</tr>
			<tr>
				<td>User</td>
				<td>
				<input type="hidden" name="userName" value="eeek"/>
				<input type="text" name="bewUserName" value="janjan" /></td>
			</tr>
			<tr>
				<td>Password</td>
				<td><input type="password" name="password" value="hemligt" /></td>
			</tr>
			<tr>
				<td>E-mail</td>
				<td><input type="text" name="email" value="janjan@solit.se" /></td>
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