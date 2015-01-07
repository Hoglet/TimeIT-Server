<#-- @ftlvariable name="" type="se.solit.timeit.resources.AdminView"
-->
<!DOCTYPE HTML>
<head>
<#include "head.ftl">
</head>
<html>
<body>
	<div id="shadyOverlay"></div>
	<#include "top.ftl">
	<div id="userAdmin" class="mainFrame">
	<h1>Administration</h1>
	<h2>Users</h2>
	<form name="showall" action="/admin/user" method="POST">
		<table>
			<tbody>
				<tr>

					<td><select multiple="multiple" name="userSelector" id="userSelector"
						onchange="selectionChanged()"> <#list users as u>
							<option value="${u.username}">${u.username}
								(${u.name})</option> </#list>
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
		</div>
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
