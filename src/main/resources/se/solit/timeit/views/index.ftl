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
    	<form method="get" action='/task/add' name="Controller">
			<button type="submit" value="add" >Add</button>
		</form>
		</span>
		<span>
    	<form method="get" action='/task' name="Controller">
			<button type="submit" name="action" value="edit" >Edit</button>
		</form>
		</span>
		<span>
    	<form method="get" action='/task' name="Controller">
			<button type="submit" name="action" value="delete" >Delete</button>
		</form>
		</span>
		</div>
    	<div id="tasks-inner" class="data">
        <#function test items>
        	<#assign result>
        	<ol class="tree">
        	<#list items as entry>
        		<li>
        		<label for="${entry.key.name}">${entry.key.name}</label>
				<input id="${entry.key.name}" type="checkbox" >
		        		<#if entry.value??>
        			${test(entry.value)}
        		</#if>
        		</li>
			</#list>
			</ol>
			</#assign>
			<#return result>
		</#function>
		<#-- call the macro: -->
		<#if tasks??>
			${test(tasks)}
		</#if>
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
        	<#list todaysTimes as entry>
        		<tr>
        		<td>
        		${entry.key}
        		</td>
        		<td>
        		${entry.value}
        		</td>
        		</tr>
			</#list>
		</table>
		</div>
		<h3>This month</h3>
		<div class="summary">
       	<table>
        	<#list monthsTimes as entry>

        		<tr>
        		<td>
        		${entry.key}
        		</td>
        		<td>
        		${entry.value}
        		</td>
        		</tr>
			</#list>
		</table>
		</div>
		<h3>This year</h3>
		   <div class="summary">
		   <table>
        	<#list yearsTimes as entry>

        		<tr>
        		<td>
        		${entry.key}
        		</td>
        		<td>
        		${entry.value}
        		</td>
        		</tr>
			</#list>
		</table>
		</div>
	</div>
	</div>
    </body>
</html>
