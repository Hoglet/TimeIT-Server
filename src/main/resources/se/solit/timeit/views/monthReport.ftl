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
		<div style="display: inline-block">${previousYearLink}<div id="year">${year}</div>${nextYearLink}</div> <div style="display: inline-block">${previousMonthLink}<div id="month">${month}</div>${nextMonthLink}</div>
		</div>
	<p>
	<p>
	<h2>Totals</h2>
	<hr>
	<table class="timeTable" cellspacing=0>
		<tr><th></th><th colspan="3">Duration</th></tr>
		<tr><th></th><th class="durationWithChildren">Total</th><th>&nbsp;&nbsp;</th><th class="duration">Singular</th><tr>
        	<#list allTimes as item>
        	<#assign task=item.getTask()>
        		<tr>
        		<td class="taskName">
        		${item.getIndentString()}<span class="${getTaskClass(task)}">${task.getName()}</span>
        		</td>
        		<td class="durationWithChildren">
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
	<tr><th colspan="4"><th colspan="3">Duration</th></tr>
	<tr class=""><th class="dayOfMonth"></th><th></th><th class=""></th><th></th><th>Total</th><th>&nbsp;</th><th>Singular</th><th class="lastColumn"></th></tr>
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
        		${item.getIndentString()}<span class="${getTaskClass(item.getTask())}">${item.getTask().getName()}</span>
        		</td>
        		<td class="durationWithChildren">
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
