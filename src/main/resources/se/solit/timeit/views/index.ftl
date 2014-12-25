<#-- @ftlvariable name="" type="se.solit.timeit.views.IndexView" -->
<!DOCTYPE HTML>
<html>
<head>
<#include "head.ftl">
</head>
<body>
    <#include "top.ftl">
    <div id="timeit-webui">
    <div id="tasks">
    	<h2>Tasks</h2>
    	<div class="toolbar">
    	<span>
    	<form method="get" action='/task/add' name="Controller">
			<input type="submit" value="Add" />
		</form>
		</span>
		<span>
    	<form method="get" action='/task' name="Controller">
			<input type="submit" name="action" value="edit" />
		</form>
		</span>
		</div>
    	<div id="tasks-inner">
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
	</div>
    </body>
</html>
