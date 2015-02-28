<#-- @ftlvariable name="" type="se.solit.timeit.views.IndexView" -->
<!DOCTYPE HTML>
<html>
<head>
<#include "head.ftl">
</head>
<body id="mainPage">
    <#include "top.ftl">
    <div id="timeit-webui" class="mainFrame">
    	<div id="tasks" class="container">
    		<h2>Tasks</h2>
    		<div class="toolbar">
    			<span>
    			<a href="/task/add">
					<button type="button">Add</button>
				</a>
				</form>
				</span>
			</div>
    		<div id="tasks-inner" class="data">
				<table>
				<#list tasks as task>
	        		<tr>
        			<td class="name">${task.indentString}${task.name}</td>
        			<td>&nbsp;</td>
        			<td><a href="/task/edit/${task.id}"><button>Edit</button></a></td>
        			<td><a href="/task/delete/${task.id}"><button>Delete</button></a></td>
        			</tr>
				</#list>
				</table>
			</div>
		</div>
	    <div id="times" class="container">
	   	 	<h2>Times</h2>
	    	<div class="toolbar">
	    		<span>
		    	<form method="get" action='/time/add' name="Controller">
				<button type="submit" value="add" >Add</button>
				</form>
				</span>
			</div>
			<div class="data">
				<h3>Today</h3>
    	   		<div class="summary">
    	   		<table>
		        	<#list todaysTimes as item>
    	    			<tr>
    	    			<td class="name">
       		 			${item.getTask().getName()}
        				</td>
        				<td class="duration">
        				${item.getDurationWithChildrenStringAlways()}
        				</td>
        				</tr>
					</#list>
				</table>
				</div>
				<h3>This month</h3>
				<div class="summary">
       			<table>
	        	<#list monthsTimes as item>
        			<tr>
        			<td class="name">
        			${item.getTask().getName()}
        			</td>
        			<td class="duration">
        			${item.getDurationWithChildrenStringAlways()}
	        		</td>
        			</tr>
				</#list>
				</table>
				</div>
				<h3>This year</h3>
				<div class="summary">
			   	<table>
        		<#list yearsTimes as item>
        			<tr>
        			<td class="name">
        			${item.getTask().getName()}
        			</td>
        			<td class="duration">
        			${item.getDurationWithChildrenStringAlways()}
        			</td>
        			</tr>
				</#list>
				</table>
				</div>
			</div>
		</div>
	</div>
    </body>
</html>
