<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.YearReportView" -->
<!DOCTYPE HTML>
<html>
<head>
<#include "head.ftl">
</head>
<body class="report">
	<#include "top.ftl">
	${tabs(1)}
	<div id="YearReport" class="report">
		<div id="dateBar">
		${previousYearLink}<div id="year">${year}</div>${nextYearLink}
		</div>
	<p>
	<p>
	<h2>Totals</h2>
	<hr>
	<table class="timeTable" cellspacing=0>
        	<#list allTimes as entry>
        		<tr>
        		<td>
        		</td>
        		<td>
        		<td>
        		<td class="taskName">
        		${entry.key}
        		</td>
        		<td class="duration">
        		${entry.value}
        		</td>
        		<td></td>
        		</tr>
			</#list>
	</table>
	</p>
	<h2>Details</h2>
	<hr>
	<table class="timeTable" cellspacing=0>
	<#list 1..12 as y>
	<#assign month=getMonth(y)>
	<tr class="monthRow"><td class="month">${month}</td><td></td><td></td><td></td><td class="lastColumn"></td></tr>
        	<#list getTimes(y) as entry>
        		<tr class="${month}">
        		<td>
        		</td>
        		<td>
        		<td>
        		<td class="taskName">
        		${entry.key}
        		</td>
        		<td class="duration">
        		${entry.value}
        		</td>
        		<td></td>
        		</tr>
			</#list>
	</#list>
	</table>
	</p>
	</div>
	<#include "bottom.ftl">
</body>
</html>
