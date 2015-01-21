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
        	<#list allTimes as item>
        		<tr>
        		<td>
        		</td>
        		<td class="taskName">
        		${item.getIndentString()}${item.getTask().getName()}
        		</td>
        		<td class="duration">
        		${item.getDurationString()}
        		</td>
        		<td></td>
        		<td class="duration">
        		${item.getDurationWithChildrenString()}
        		</td>
        		</tr>
			</#list>
	</table>
	</p>
	<h2>Details</h2>
	<hr>
	<table class="timeTable" cellspacing=0>
	<tr class=""><th class="dayOfMonth"></th><th class=""></th><th>Duration</th><th>&nbsp;</th><th>(With&nbsp;children)</th><th class="lastColumn"></th></tr>
	<#list 1..12 as y>
	<#assign month=getMonth(y)>
	<tr class="monthRow"><td class="month">${month}</td><td></td><td></td><td></td><td></td><td></td></tr>
        	<#list getTimes(y) as item>
        		<tr class="${month}">
        		<td>
        		</td>
        		<td class="taskName">
        		${item.getIndentString()}${item.getTask().getName()}
        		</td>
        		<td class="duration">
        		${item.getDurationString()}
        		</td>
        		<td></td>
        		<td class="duration">
        		${item.getDurationWithChildrenString()}
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
