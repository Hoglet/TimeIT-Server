<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.UserEditView" -->
<!DOCTYPE HTML>
<html>
<head><#include "head.ftl">
</head>
<body>
	<#include "top.ftl">
	<div id="userEdit" class="mainFrame">
	<h1>Edit settings for user ${user.username}</h1>
	<form method="POST" action='/user/${user.username}' name="Controller" autocomplete="off" >
		<input type="hidden" name="userName" value="${user.username}"/>
		<table>
			<tr>
				<td>Real name</td>
				<td><input type="text" name="name"  value="${user.name}" /></td>
			</tr>
			<tr>
				<td>Password</td>
				<td><input type="password" name="password"  value="${user.password}" /></td>
			</tr>
			<tr>
				<td>E-mail</td>
				<td><input type="text" name="email" value="${user.email}" /></td>
			</tr>
			<#if currentUser.hasRole("Admin")>
			<tr>
				<td>Roles</td>
				<td>
				<#list roles as role>
						<input type="checkbox" name="roles" value="${role.getName()}"
						<#if role.getCheckedState() >
							checked
						</#if>

						>
						${role.getName()} <br>
				</#list>
				</td>
			</tr>
			</#if>
		</table>

		<p>
			<input type="submit" name="submitType" value="save" />&nbsp; <a href="/admin/"><button type="button">Cancel</button></a>
		</p>
	</form>
	</div>
	<#include "bottom.ftl">
</body>
</html>
