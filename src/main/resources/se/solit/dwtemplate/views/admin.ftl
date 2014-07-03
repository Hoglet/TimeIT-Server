<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.AdminView"
-->
<!DOCTYPE HTML>
<head>
<#include "head.ftl">
<style>
#dialog {
	background-color: white;
	border: medium ridge;
	border-radius: 9px; padding : 1em; position : fixed;
	display: none;
	position: fixed;
	padding: 1em;
}

#shadyOverlay {
	opacity: 0.50;
	background-color: black; cursor : pointer;
	height: 100%;
	display: none;
	position: fixed;
	width: 100%;
	cursor: pointer;
}

body.showDialog #shadyOverlay {
	display: block;
}

body.showDialog #dialog {
	position: absolute;
	display: block;
	opacity: 1;
	z-index: 99;
	visibility: visible;
	margin-left: -10em;
	margin-top: 2em;
	box-shadow: 5px 5px 5px rgba(0,0,0,0.6);
}

#OKButton {
	float: left;
}

#CancelButton {
	float: right;
}
</style>

</head>
<html>
<body>
	<div id="shadyOverlay"></div>
	<#include "top.ftl">
	<h1>Administration</h1>
	<h2>Users</h2>
	<form name="showall" action="/admin/user" method="POST">
		<table>
			<tbody>
				<tr>

					<td><select multiple="multiple" name="userSelector" id="userSelector"
						onchange="selectionChanged()"> <#list users as user>
							<option value="${user.username}">${user.username}
								(${user.name})</option> </#list>
					</select></td>
					<td valign="top">
					  <input type="submit" id="add" value="add" name="submitType"> <br>
					  <input type="submit" id="edit" value="edit" name="submitType" disabled="true"> <br>
					  <input type="button" id="delete" value="delete" onclick="deleteClicked()" disabled="true">
						<div id="dialog">
							<p>Are you sure you want to delete the selected user?</p>
							<input type="submit" value="OK" name="submitType" id="OKButton">
							<input type="button" value="Cancel" onclick="cancelClicked()"
								id="CancelButton">
						</div>
						</form>
				</tr>
			</tbody>
		</table>
		<script>
			function selectionChanged() {
				if (document.getElementById("userSelector").selectedIndex == "-1") {
					document.getElementById("edit").disabled= true;
					document.getElementById("delete").disabled= true;
				} else {
					document.getElementById("edit").disabled= false;
					document.getElementById("delete").disabled= false;
					document.getElementById("userSelector").selectedIndex = document.getElementById("userSelector").selectedIndex;
				}
			}
			function deleteClicked() {
				document.body.classList.add('showDialog');
			}
			function cancelClicked() {
				document.body.classList.remove('showDialog');
			}
		</script>
</body>
</html>