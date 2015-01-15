<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.UserEditView" -->
<!DOCTYPE HTML>
<html>
<head><#include "head.ftl">
</head>
<body>
	<#include "top.ftl">
	<div class="tabs"><a href="/user"><div class="tab"><h2>Users</h2></div></a><div class="tab selected"><h2>Edit</h2></div></div>
	<div id="userEdit" class="mainFrame">
	<form method="POST" action='/user/${user.username}' name="Controller" autocomplete="off" >
		<table>
			<tr>
				<td>Username</td>
				<td>${user.username}</td>
			</tr>
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
			<input type="submit" name="submitType" value="save" />&nbsp; <a href="/user/"><button type="button">Cancel</button></a>
		</p>
	</form>
	</div>
	<#include "bottom.ftl">
</body>
</html>
