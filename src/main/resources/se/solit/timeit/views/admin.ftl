<#-- @ftlvariable name="" type="se.solit.timeit.resources.AdminView"
-->
<!DOCTYPE HTML>
<head>
<#include "head.ftl">
</head>
<html>
<body>
	<#include "top.ftl">
	<div id="userAdmin" class="mainFrame">
	<h1>Administration</h1>
	<h2>Users</h2>
	<a href="/user/add/"><button type="button">Add</button></a>
	<form name="showall" action="/user" method="POST">
		<table cellspacing=0 cellpadding=0>
			<tbody>
			<#list users as u>
				<tr>
					<td>${u.username} (${u.name})</td>
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
