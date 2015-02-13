<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.UserEditView" -->
<!DOCTYPE HTML>
<html>
<head><#include "head.ftl">
</head>
<body>
	<#include "top.ftl">
	<div class="tabs">
	<div class="tab selected"><h2>New password</h2></div></div>
	<div id="userEdit" class="mainFrame">

	<form method="POST" action='/recover/${temporaryKey}' name="Controller" autocomplete="off" >
		Changing password for ${user.username}

		<table>
			<tr>
				<td>Password</td>
				<td><input type="password" name="password"  value="" /></td>
			</tr>
		</table>
		<p>
			<input type="submit" name="submitType" value="save" />&nbsp; <a href="/user/"><button type="button">Cancel</button></a>
		</p>
	</form>
	</div>
	<#include "bottom.ftl">
</body>
</html>
