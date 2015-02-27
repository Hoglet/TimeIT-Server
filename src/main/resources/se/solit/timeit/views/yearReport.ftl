<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.YearReportView" -->
<!DOCTYPE HTML>
<html>
<head>
<#include "head.ftl">
</head>
<body class="report">
	<#include "top.ftl">
	${tabs(0)}
	<div id="YearReport" class="report">
		<div id="dateBar">
		${previousYearLink}<div id="year">${year}</div>${nextYearLink}
		</div>
	<p>
	<p>
	<h2>Totals</h2>
	<hr>
	<table class="timeTable" cellspacing=0>
			<tr><th></th><th colspan="3">Duration</th></tr>
  			<tr><th></th><th class="durationWithChildren">Total</th><th>&nbsp;&nbsp;</th><th class="duration">Singular</th><tr>
        	<#list allTimes as item>
        		<tr>
        		<td class="taskName">
        		${item.getIndentString()}<span class="${getTaskClass(item.getTask())}">${item.getTask().getName()}</span>
        		</td>
        		<td class="duration">
        		${item.getDurationWithChildrenString()}
        		</td>
        		<td></td>
        		<td class="duration">
        		${item.getDurationString()}
        		</td>
        		</tr>
			</#list>
	</table>
	</p>
	<h2>Details</h2>
	<hr>
	<table class="timeTable" cellspacing=0>
	<tr><th colspan="2"></th><th colspan="3">Duration</th></tr>
	<tr class=""><th class="dayOfMonth"></th><th class=""></th><th>Total</th><th>&nbsp;&nbsp;</th><th>Singular</th><th class="lastColumn"></th></tr>
	<#list 1..12 as y>
	<#assign month=getMonth(y)>
	<tr class="monthRow"><td class="month">
	<a href="${yearLink}/${y}">
	${month}
	</a>
	</td><td></td><td></td><td></td><td></td><td></td></tr>
        	<#list getTimes(y) as item>
        		<tr class="${month}">
        		<td>
        		</td>
        		<td class="taskName">
        		${item.getIndentString()}<span class="${getTaskClass(item.getTask())}">${item.getTask().getName()}</span>
        		</td>
        		<td class="duration">
        		${item.getDurationWithChildrenString()}
        		</td>
        		<td></td>
        		<td class="duration">
        		${item.getDurationString()}
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
