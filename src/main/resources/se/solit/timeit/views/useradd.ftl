<#-- @ftlvariable name=""
type="se.solit.dwtemplate.resources.UserAddView" -->
<!DOCTYPE HTML>
<html>
<head><#include "head.ftl">
</head>
<body>
	<#include "top.ftl">
	<h1>Add user</h1>

	<form method="POST" action='/admin/user/add' name="Controller">
		<table>
			<tr>
				<td>Real name</td>
				<td><input type="text" name="name" /></td>
			</tr>
			<tr>
				<td>User</td>
				<td><input type="text" name="userName" /></td>
			</tr>
			<tr>
				<td>Password</td>
				<td><input type="password" name="password" /></td>
			</tr>
			<tr>
				<td>E-mail</td>
				<td><input type="text" name="email" /></td>
			</tr>
			<tr>
				<td>Roles</td>
				<td>
				<#list roles as role>
						<input type="checkbox" name="roles" value="${role.getName()}">${role.getName()}<br>
				</#list>
				</td>
			</tr>
		</table>

		<p>
			<input type="submit" name="submitType" value="save" />&nbsp; <input
				type="submit" name="submitType" value="cancel" />&nbsp;
		</p>
	</form>
	<#include "bottom.ftl">
</body>
</html>