<#-- @ftlvariable name="" type="se.solit.timeit.resources.UserAdminView"
-->
<!DOCTYPE HTML>
<head>
<#include "head.ftl">
</head>
<html>
<body>
	<#include "top.ftl">
	<div class="tabs"><div class="tab selected"><h2>Users</h2></div></div>
	<div id="userAdmin" class="mainFrame">

	<a href="/user/add/"><button type="button">Add</button></a>
	<form name="showall" action="/user" method="POST">
		<table cellspacing=0 cellpadding=0  id="userlist">
			<tbody>
			<#list users as u>
				<tr>
					<td class="username">${u.username} (${u.name})</td>
					<td>&nbsp;</td>
					<td><a href="/user/${u.username}"><button type="button">Edit</button></a></td>
					<td><a href="/user/delete/${u.username}"><button type="button">Delete</button></a></td>
				<tr>
			</#list>
			</tbody>
		</table>
		</div>
</body>
</html>
