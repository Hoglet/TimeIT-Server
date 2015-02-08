<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.DayReportView" -->
<!DOCTYPE HTML>
<html>
<head>
<#include "head.ftl">
</head>
<body class="report">
	<#include "top.ftl">
	${tabs(2)}
	<div id="DayReport" class="report">
		<div id="dateBar">
		${previousYearLink}<div id="year">${year}</div>${nextYearLink}${previousMonthLink}<div id="month">${month}</div>${nextMonthLink} ${previousDayLink}<div id="day"> ${dayOfmonth} (${day})</div>${nextDayLink}
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
        		${item.getIndentString()}<a href="/report/${user}/${year}/${monthOfYear}/${dayOfmonth}/${task.getID()}"><span class="${getTaskClass(task)}">${task.getName()}</div>
        		</a>
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
	<table class="timeTable">
	<tr><th></th><th></th>
	<#if numberOfTasks gt 0>
	<#list 0..(numberOfTasks-1) as column>
	  <#assign task=getTask(column)>
	  <th class="${getColumnClass(column)}"> ${task.getName()} </th><th>&nbsp;</th>
	</#list>
	</#if>
	</tr>
	<#list 0..23 as hour>
	<tr><td><small>${hour}</small></td><td></td>
		<#if numberOfTasks gt 0>
			<#list 0..(numberOfTasks-1) as taskColumn>
				<td class="${getCellClass( hour, taskColumn)}"></td><td></td>
			</#list>
		</#if>
	</tr>
	</#list>
	</table>
	<#include "bottom.ftl">
</body>
</html>
