<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.MonthReportView" -->
<!DOCTYPE HTML>
<html>
<head>
<#include "head.ftl">
</head>
<body class="report">
	<#include "top.ftl">
	${tabs(1)}
	<div id="MonthReport" class="report">
		<div id="dateBar">
		${previousMonthLink}<div id="month">${month}</div>${nextMonthLink} ${previousYearLink}<div id="year">${year}</div>${nextYearLink}
		</div>
	<p>
	<p>
	<h2>Totals</h2>
	<hr>
	<table class="timeTable" cellspacing=0>
		<tr><th></th><th class="duration">Duration</th><th>&nbsp;&nbsp;</th><th class="durationWithChildren">(With children)</th><tr>
        	<#list allTimes as item>
        	<#assign task=item.getTask()>
        		<tr>
        		<td class="taskName">
        		${item.getIndentString()}${task.getName()}
        		</td>
        		<td class="duration">
                ${item.getDurationString()}
        		</td>
        		<td></td>
        		<td class="durationWithChildren">
                ${item.getDurationWithChildrenString()}
        		</td>
        		</tr>
			</#list>
	</table>
	</p>
	<h2>Details</h2>
	<hr>
	<table class="timeTable" cellspacing=0>
	<tr class=""><th class="dayOfMonth"></th><th></th><th class=""></th><th></th><th>Duration</th><th>&nbsp;</th><th>(With&nbsp;children)</th><th class="lastColumn"></th></tr>
	<#list 1..daysInMonth as d>
	<#assign day=getDay(d)>
	<tr class="dayRow ${day}"><td class="dayOfMonth">
		<a href="${monthLink}/${d}">
		${d}
		</a>
	</td><td></td><td class="${day}">${day}</td><td></td><td>&nbsp;</td><td>&nbsp;</td><td></td><td class="lastColumn"></td></tr>
        	<#list getTimes(d) as item>
        		<tr class="${day}">
        		<td>
        		</td>
        		<td>
        		<td>
        		<td class="taskName">
        		${item.getIndentString()}${item.getTask().getName()}
        		</td>
        		<td class="duration">
                ${item.getDurationString()}
        		</td>
        		<td></td>
        		<td class="durationWithChildren">
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
